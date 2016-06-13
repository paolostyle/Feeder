package feeder.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.rometools.rome.io.FeedException;

/**
 * <b>Model</b> is an element of MVC structure - using DefaultTreeModel,
 * DefaultMutableTreeNode and obviously Channel and Category classes it
 * represents business logic of this application and contains the tree
 * structure.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-02
 * @version	1.0
 */
public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Map<String, Category> categories;
	private final DefaultMutableTreeNode root;
	private final DefaultTreeModel treeModel;

	/**
	 * Model constructor - initializes whole TreeModel and makes a Map of Categories.
	 */
	public Model() {
		categories = new HashMap<String, Category>();
		root = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(root);
	}

	////////////////////////////////////////////
	///////// CATEGORY RELATED METHODS /////////
	////////////////////////////////////////////

	/**
	 * Adds a new Category.
	 * 
	 * @param name Name of the Category as String.
	 * @return True if added successfull, false otherwise.
	 */
	public boolean addNewCategory(String name) {
		if (categories.get(name) == null) {
			Category category = new Category(name);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
			categories.put(name, category);
			root.add(node);
			category.setIndex(root.getIndex(node));

			treeModel.reload();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Renames the Category.
	 * 
	 * @param oldName Name of category to rename.
	 * @param newName New name of the category.
	 * @return True if renamed successfully, false otherwise.
	 */
	public boolean setCategoryName(String oldName, String newName) {
		Category category = categories.get(oldName);
		if (category != null && categories.get(newName) == null) {
			categories.put(newName, categories.remove(oldName));
			category.setName(newName);
			DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) root.getChildAt(category.getIndex());
			categoryNode.setUserObject(newName);
			treeModel.reload();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Removes a category. Can only be performed when the category is empty 
	 * (as in, user have to move/delete all the categories before deleting
	 * the category.
	 * 
	 * @param categoryName Name of category to remove.
	 * @return True if successfully removed, false otherwise.
	 */
	public boolean removeCategory(String categoryName) {
		Category category = categories.get(categoryName);
		if (category.getChannelsMap().isEmpty()) {
			categories.remove(categoryName);
			treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(category.getIndex()));
			treeModel.reload();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return String array with names of categories.
	 */
	public String[] getCategoriesList() {
		List<String> categoriesList = new ArrayList<String>();
		for (Entry<String, Category> category : categories.entrySet()) {
			categoriesList.add(category.getKey());
		}		
		return categoriesList.toArray(new String[0]);
	}


	/**
	 * @param category Name of the category to get aggregated feed.
	 * @return Aggregated Channel object containing News from all channels belonging to the Category.
	 */
	public Channel getCategoryChannel(final String category) {
		return categories.get(category).getAggregatedFeed();
	}

	/////////////////////////////////////////////////////////
	//////////////// CHANNEL RELATED METHODS ////////////////
	/////////////////////////////////////////////////////////

	/**
	 * Creates a Channel and adds it to a selected Category.
	 * 
	 * @param channelName Name of channel to be created.
	 * @param channelUrl URL address of XML file on which the channel is supposed to be based on.
	 * @param categoryName Name of category to which the Channel should be added.
	 * @return True if added succesfully, false otherwise
	 * @throws IOException Thrown when the URL is corrupted.
	 * @throws FeedException Thrown when ROME couldn't create the SyndFeed.
	 * @throws IllegalArgumentException Thrown when the URL given content isn't in XML format.
	 */
	public boolean addNewChannel(final String channelName, final String channelUrl, final String categoryName) throws IllegalArgumentException, FeedException, IOException{
		Category category = categories.get(categoryName);
		Channel channel = new Channel(channelName, channelUrl);
		if (categories.get(categoryName).getChannelsMap().get(channelName) == null) {
			category.addChannel(channel);
			DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) root.getChildAt(category.getIndex());
			DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channelName);
			treeModel.insertNodeInto(channelNode, categoryNode, categoryNode.getChildCount());
			channel.setIndex(categoryNode.getIndex(channelNode));
			treeModel.reload();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets a specified Channel and its contents from selected Category.
	 * 
	 * @param category Name of category.
	 * @param channel Name of channel.
	 * @return Channel object.
	 */
	public Channel getChannel(final String category, final String channel) {
		return categories.get(category).getChannel(channel);
	}


	/**
	 * Renames the channel.
	 * 
	 * @param categoryName Category in which specific Channel exist.
	 * @param oldName Old name of the Category.
	 * @param newName New name for the Category.
	 * @return True if renamed succesfully, false otherwise.
	 */
	public boolean setChannelName(String categoryName, String oldName, String newName) {
		Category category = categories.get(categoryName);
		if (category != null) {
			Channel channel = category.getChannelsMap().get(oldName);
			if (channel != null && categories.get(categoryName).getChannelsMap().get(newName) == null) {
				category.getChannelsMap().put(newName, category.getChannelsMap().remove(oldName));
				channel.setName(newName);
				DefaultMutableTreeNode channelNode = (DefaultMutableTreeNode) root.getChildAt(category.getIndex()).getChildAt(channel.getIndex());
				channelNode.setUserObject(newName);
				treeModel.reload();
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * Changes the source RSS URL for a specific channel.
	 * 
	 * @param categoryName Category in which specific Channel exist.
	 * @param channelName Name of Channel to change the URL.
	 * @param newUrl New URL of RSS channel.
	 * @return True if changed the URL successfully, false otherwise.
	 */
	public boolean setChannelUrl(String categoryName, String channelName, String newUrl) {
		Category category = categories.get(categoryName);
		if (category != null) {
			Channel channel = category.getChannelsMap().get(channelName);
			if (channel != null) {
				try {
					channel.setUrl(newUrl);
					return true;
				} 
				catch (MalformedURLException e) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * Moves Channel from Category to the other.
	 * 
	 * @param channelName Name of Channel to move.
	 * @param oldCategoryName Name of old Category.
	 * @param newCategoryName Name of new Category.
	 * @return True if moved the Channel successfully, false otherwise.
	 */
	public boolean changeChannelCategory(String channelName, String oldCategoryName, String newCategoryName) {
		Category oldCategory = categories.get(oldCategoryName);
		Category newCategory = categories.get(newCategoryName);
		if (oldCategory != null && newCategory != null) {
			Channel channel = oldCategory.getChannelsMap().remove(channelName);
			if (channel != null && categories.get(newCategoryName).getChannelsMap().get(channelName) == null) {
				treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(oldCategory.getIndex()).getChildAt(channel.getIndex()));

				newCategory.addChannel(channel);
				DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) root.getChildAt(newCategory.getIndex());
				DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channelName);
				treeModel.insertNodeInto(channelNode, categoryNode, categoryNode.getChildCount());
				channel.setIndex(categoryNode.getIndex(channelNode));
				treeModel.reload();

				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * Removes the channel completely.
	 * 
	 * @param channelName Name of the channel to remove.
	 * @param categoryName Name of category containing specified channel.
	 * @return True if removed successfully, false otherwise.
	 */
	public boolean removeChannel(String channelName, String categoryName) {
		Category category = categories.get(categoryName);
		if (category != null) {
			Channel channel = category.getChannelsMap().remove(channelName);
			if (channel != null) {
				treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(category.getIndex()).getChildAt(channel.getIndex()));
				treeModel.reload();
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	////////////////////////////////////////////////////////
	/////////////////// FIELDS GETTERS /////////////////////
	////////////////////////////////////////////////////////

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
	 * @return Map of Categories.
	 */
	public Map<String, Category> getCategoriesMap() {
		return categories;
	}

}
