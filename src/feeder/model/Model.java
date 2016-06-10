package feeder.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.rometools.rome.io.FeedException;

/**
 * <b>Model</b> is an element of MVC structure - using DefaultTreeModel,
 * DefaultMutableTreeNode and obviously Channel and Category classes it
 * represents business logic of this application.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-02
 * @version	0.8
 */
public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Category> categories;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;

	public Model() {
		categories = new HashMap<String, Category>();
		root = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(root);
	}

	/**
	 * Adds a new Category to the tree.
	 * 
	 * @param name Name of the Category as String.
	 */
	public void addNewCategory(String name) {
		Category category = new Category(name);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
		categories.put(name, category);
		root.add(node);
		category.setIndex(root.getIndex(node));
		treeModel.reload();
	}

	/**
	 * Creates a Channel and adds it to the Category.
	 * Category must already exist.
	 * 
	 * @param chanName as String
	 * @param chanUrl as String
	 * @param category as String
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws IOException
	 */
	public void addNewChannel(String channelName, String channelUrl, String categoryName) throws IllegalArgumentException, FeedException, IOException {
		Channel channel = new Channel(channelName, channelUrl);
		Category category = categories.get(categoryName);
		category.addChannel(channel);
		
		DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) root.getChildAt(category.getIndex());
		treeModel.insertNodeInto(new DefaultMutableTreeNode(channelName), categoryNode, categoryNode.getChildCount());
		treeModel.reload();
	}

	/**
	 * @param category 
	 * @param channel 
	 * @return Formatted feed as String.
	 * @throws NoSuchElementException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 * @see Channel#convertToHTML(com.rometools.rome.feed.synd.SyndFeed, boolean)
	 */
	public Channel getChannelFeed(String category, String channel) throws IllegalArgumentException, IOException, FeedException {
		return categories.get(category).getChannelContent(channel);
	}

	/**
	 * @param Category name of the category to get aggregated feed.
	 * @return Aggregated, formatted feed as String.
	 * @throws FeedException
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 * @see Category#getAggregateFeed()
	 * @see Channel#convertToHTML(com.rometools.rome.feed.synd.SyndFeed, boolean)
	 */
	public Channel getCategoryFeed(String category) throws FeedException, IllegalArgumentException, IOException {
		return categories.get(category).getAggregatedFeed();
	}

	/**
	 * @return List of categories names.
	 */
	public String[] getCategoriesList() {
		List<String> categoriesList = new ArrayList<String>();
		for(Entry<String, Category> category : categories.entrySet()) {
			categoriesList.add(category.getKey());
		}		
		return categoriesList.toArray(new String[0]);
	}
	
	/**
	 * @return TreeModel root.
	 */
	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	/**
	 * @return DefaultTreeModel Whole tree model.
	 */
	public DefaultTreeModel getTreeModel() {
		treeModel.reload();
		return treeModel;
	}

	/**
	 * @return the categories
	 */
	public Map<String, Category> getCategoriesMap() {
		return categories;
	}
}
