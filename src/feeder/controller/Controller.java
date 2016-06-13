package feeder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;

import javax.swing.JEditorPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.rometools.rome.io.FeedException;

import feeder.model.Channel;
import feeder.model.News;
import feeder.model.Model;
import feeder.views.EditCategoryDialog;
import feeder.views.EditFeedDialog;
import feeder.views.NewCategoryDialog;
import feeder.views.NewFeedDialog;
import feeder.views.View;

/**
 * <b>Controller</b> in this application works as a 'bridge' between View and Model.
 * It manages the displayed Views and takes care of saving and restoring data on
 * start and close of the application.
 * http://stackoverflow.com/questions/2663674/correct-implementation-of-mvc-architecture
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-02
 * @version	1.0
 */
public class Controller {
	private Model model;
	private DefaultMutableTreeNode selectedNode;
	
	/**
	 * Constructor. Loads the Model - either from data.dat file (if exists) or 
	 * loads default model with one category and one channel. Saves the model to file
	 * before closing of the application using shutdownHook.
	 */
	public Controller() {
		model = loadModel();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				saveModel();
			}
		}));
	}

	/////////////////////////////////////////
	//////////// MODEL SAVE/LOAD ////////////
	/////////////////////////////////////////
	
	private void saveModel() {
		try {
			FileOutputStream file = new FileOutputStream("data.dat");
			ObjectOutputStream data = new ObjectOutputStream(file);
			data.writeObject(model);
			data.close();
			file.close();
		} 
		catch (IOException e) {
			View.alertMessage("Wystąpił błąd w zapisie listy kanałów! Upewnij się, że masz prawa do zapisu na dysku.");
		}
	}

	private Model loadModel() {
		Model model = null;

		if(new File("data.dat").exists()) {			
			FileInputStream file;
			try {
				file = new FileInputStream("data.dat");
				model = (Model) new ObjectInputStream(file).readObject();
				file.close();
			} 
			catch (ClassNotFoundException | IOException e) {
				View.alertMessage(e.getMessage());
			}
		}
		else {
			model = new Model();
			try {
				model.addNewCategory("Kategoria");
				model.addNewChannel("BBC", "http://feeds.bbci.co.uk/news/rss.xml", "Kategoria");
			} catch (IllegalArgumentException | FeedException | IOException e) {
				View.alertMessage(e.getMessage());
			}
		}

		return model;
	}

	///////////////////////////////////////////////////
	///////////// VIEW COMPONENTS HELPERS /////////////
	///////////////////////////////////////////////////
	
	
	/**
	 * @return Categories list.
	 */
	public String[] getCategories() {
		return model.getCategoriesList();
	}

	/**
	 * @return Tree model.
	 */
	public DefaultTreeModel getTreeModel() {
		return model.getTreeModel();
	}
	
	/**
	 * Converts Channel object to an HTML string displayable by JPane.
	 * 
	 * @param channel Channel to format.
	 * @return HTML-formatted String ready to be displayed.
	 */
	public String convertNewsToHTML(Channel channel) {
		// maybe should've used something like Gagawa
		// but I think it's not worth the hassle
		String html = "<html><h2 style='font-family: Tahoma'>" + channel.getName() + "</h2>";

		try {
			for(News news : channel.getChannelContent()) {
				html += "<div style='font-family: Tahoma; font-size:13px; margin-bottom: 10px'>";
				html += "<a style='font-weight: bold;' href='" + news.getLink() + "'>" + news.getTitle() + "</a>";
				html += " (" + news.getDate() + ")<br>";
				if(channel.isAggregated() == true) {
					html += "Z kanału: " + news.getChannel() + "<br>";
				}
				html += news.getDescription() + "</div><hr>";
			}
		} 
		catch (IllegalArgumentException | FeedException | IOException e) {
			View.alertMessage("Wystąpił problem przy wczytywaniu nagłówków.");
		}

		return html;
	}

	////////////////////////////////////////////
	////////////// EVENT HANDLERS //////////////
	////////////////////////////////////////////
	
	
	/**
	 * Handles event of left click on an element of the tree,
	 * which changes the contents of main panel.
	 * 
	 * @param tree Reference to JTree.
	 * @param feedPresenter Reference to display panel.
	 */
	public void elementInTreeFocused(JTree tree, JEditorPane feedPresenter) {
		try {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) return;
			Object nodeInfo = node.getUserObject();
			DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();

			if(category.equals(model.getRoot())) {
				// parameter is nodeInfo, NOT category, category contains Root
				String content = convertNewsToHTML(model.getCategoryChannel(nodeInfo.toString()));
				feedPresenter.setText(content);
			}
			else { 
				String content = convertNewsToHTML(model.getChannel(category.toString(), nodeInfo.toString()));
				feedPresenter.setText(content);
			}
			feedPresenter.setCaretPosition(0);
		}
		catch (NoSuchElementException | IllegalArgumentException exception) {
			View.alertMessage(exception.getMessage());
		}
		
	}
	
	/**
	 * Handles event of adding a new Feed.
	 * 
	 * @param name Name of the Feed.
	 * @param url URL of the Feed.
	 * @param category Category to put the new Feed.
	 */
	public void addNewFeedEvent(String name, String url, String category) {
		try {
			if(!model.addNewChannel(name, url, category)) {
				View.alertMessage("Kanał o takiej nazwie już istnieje!");
			}
		} catch (IllegalArgumentException | FeedException | IOException e) {
			View.alertMessage("Kanał o takiej nazwie już istnieje!");
		}
	}

	/**
	 * Handles event of adding new Category.
	 * 
	 * @param name Name of the category.
	 */
	public void addNewCategoryEvent(String name) {
		if(!model.addNewCategory(name)) {
			View.alertMessage("Kategoria o takiej nazwie już istnieje!");
		}
	}
	
	/**
	 * Handles event of changing Category name.
	 *
	 * @param oldName Name of Category to change.
	 * @param newName New name of this Category.
	 */
	public void changeCategoryName(String oldName, String newName) {
		if(!model.setCategoryName(oldName, newName)) {
			View.alertMessage("Wystąpił błąd przy zmianie nazwy, upewnij się, że kategoria o takiej nazwie już nie istnieje.");
		}
	}
	
	/**
	 * Handles event of changing Channel name.
	 * 
	 * @param category Category of the Channel.
	 * @param oldName Name of the Channel to change.
	 * @param newName New name of this Channel.
	 */
	public void changeChannelName(String category, String oldName, String newName) {
		if(!model.setChannelName(category, oldName, newName)) {
			View.alertMessage("Wystąpił błąd przy zmianie nazwy, upewniej się, że nie istnieje już kanał o takiej samej nazwie.");
		}
	}

	/**
	 * Handles event of changing URL of a Channel.
	 * 
	 * @param categoryName Category of the Channel.
	 * @param channelName Name of the Channel to change the URL.
	 * @param newUrl New source of the Channel.
	 */
	public void changeChannelUrl(String categoryName, String channelName, String newUrl) {
		if(!model.setChannelUrl(categoryName, channelName, newUrl)) {
			View.alertMessage("Wystąpił błąd przy zmianie URLa, plik data.dat jest prawdopodobnie uszkodzony.");
		}
	}
	
	/**
	 * Handles event of moving Channel from one category to the other.
	 * 
	 * @param channelName Channel to move.
	 * @param oldCategoryName "from" Category
	 * @param newCategoryName "to" Category
	 */
	public void changeChannelCategory(String channelName, String oldCategoryName, String newCategoryName) {
		if(!model.changeChannelCategory(channelName, oldCategoryName, newCategoryName)) {
			View.alertMessage("Wystąpił błąd przy zmianie kategorii, upewnij się, że kanał o takiej samej nazwie jak ten który przenosisz już nie istnieje.");
		}
	}
	
	/**
	 * Handles an event of clicking the Add new Feed button.
	 */
	public void addNewFeed() {
		NewFeedDialog dialog = new NewFeedDialog(this);
		dialog.setVisible(true);
	}
	
	/**
	 * Handles an event of clicking the Add new Category button.
	 */
	public void addNewCategory() {
		NewCategoryDialog dialog = new NewCategoryDialog(this);
		dialog.setVisible(true);
	}
	
	/**
	 * Handles event of choosing "Edit" option from Tree context menu. Dependant on the clicked node.
	 */
	public void editElement() {
		if(model.getRoot() == selectedNode.getParent()) {
			EditCategoryDialog dialog = new EditCategoryDialog(this);
			dialog.setTextFields(selectedNode.toString());
			dialog.setVisible(true);
		}
		else {
			EditFeedDialog dialog = new EditFeedDialog(this);
			dialog.setTextFields(getSelectedNodeChannelUrl(), getSelectedNodeChannelName(), getSelectedNodeChannelCategory());
			dialog.setVisible(true);
		}
	}
	
	/**
	 * Handles event of choosing "Delete" option from Tree context menu. Dependant on the clicked node.
	 */
	public void deleteElement() {
		if(model.getRoot() == selectedNode.getParent()) {
			if(model.removeCategory(getSelectedNode().toString()) == false) {
				View.alertMessage("Upewnij się, że kategoria nie zawiera żadnych kanałów.");
			}
		}
		else {
			if(model.removeChannel(getSelectedNodeChannelName(), getSelectedNodeChannelCategory()) == false) {
				View.alertMessage("Nie można było usunąć kanału.");
			}
		}
	}
	
	/**
	 * @return Last selected node.
	 */
	public DefaultMutableTreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode Sets new recently selected node.
	 */
	public void setSelectedNode(DefaultMutableTreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return If the selected node is a Channel, returns its name.
	 */
	public String getSelectedNodeChannelName() {
		return model.getCategoriesMap().get(selectedNode.getParent().toString()).getChannelsMap().get(selectedNode.toString()).getName();
	}
	
	/**
	 * @return If the selected node is a Channel, returns its URL.
	 */
	public String getSelectedNodeChannelUrl() {
		return model.getCategoriesMap().get(selectedNode.getParent().toString()).getChannelsMap().get(selectedNode.toString()).getUrl();
	}
	
	/**
	 * @return If the selected node is a Channel, returns the Category to which it belongs.
	 */
	public String getSelectedNodeChannelCategory() {
		return selectedNode.getParent().toString();
	}
} 