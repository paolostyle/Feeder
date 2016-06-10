package feeder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.rometools.rome.io.FeedException;

import feeder.model.Channel;
import feeder.model.News;
import feeder.model.Model;
import feeder.views.View;

public class Controller {
	private Model model;

	public Controller() {
		model = loadModel();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				saveModel();
			}
		}));
	}

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

	public void addNewFeedEvent(String name, String url, String category) throws IllegalArgumentException, FeedException, IOException {
		if(model.getCategoriesMap().get(category).getChannelsMap().get(name) != null) {
			model.addNewChannel(name, url, category);
		}
		else {
			View.alertMessage("Taki kanał w tej kategorii już istnieje!");
		}
	}

	public void addNewCategoryEvent(String name) {
		if(model.getCategoriesMap().get(name) == null) {
			model.addNewCategory(name);
		}
		else {
			View.alertMessage("Kategoria już istnieje!");
		}
	}

	public String[] getCategories() {
		return model.getCategoriesList();
	}

	// maybe should've used something like Gagawa
	// but I think it's not worth the hassle
	public String convertNewsToHTML(Channel channel) {
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

	public DefaultTreeModel getTreeModel() {
		return model.getTreeModel();
	}

	public String elementInTreeFocusedEvent(JTree tree) throws FeedException, NoSuchElementException, IllegalArgumentException, IOException {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null) return null;
		Object nodeInfo = node.getUserObject();
		DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();

		String content;
		if(category.equals(model.getRoot())) {
			// parameter is nodeInfo, NOT category, category contains Root
			content = convertNewsToHTML(model.getCategoryFeed(nodeInfo.toString()));
		}
		else { 
			content = convertNewsToHTML(model.getChannelFeed(category.toString(), nodeInfo.toString()));
		}
		return content;
	}
} 