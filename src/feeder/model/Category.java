package feeder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

/**
 * <b>Category</b> represents folder/parent of Channel or multiple Channels.
 * Used mostly to organize Channels, generates aggregated feed.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-01
 * @version	1.0
 */
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer index = null;
	private Map<String, Channel> channels;

	/**
	 * Constructor. Creates Map object containing related Channels.
	 * 
	 * @param name Name of the category visible in the tree.
	 */
	public Category(String name) {
		channels = new HashMap<String, Channel>();
		this.name = name;
	}

	/**
	 * Simply adds Channel to the map of channels.
	 * 
	 * @param channel Channel object to add
	 */
	public void addChannel(Channel channel) {
		channels.put(channel.getName(), channel);
	}

	/**
	 * Aggregates feeds from all Channels belonging to the Category and
	 * sorts them by date of publication.
	 * 
	 * @return Channel object with isAggregated field set to true containing news from all belonging channels.
	 */
	public Channel getAggregatedFeed() {
		SyndFeed feed = new SyndFeedImpl();
		List<SyndEntry> entries = new ArrayList<SyndEntry>();

		feed.setFeedType("rss_2.0");
		feed.setTitle(getName());
		feed.setDescription(getName() + " - wszystkie nagłówki");
		feed.setEntries(entries);

		for(Entry<String, Channel> channelEntry : channels.entrySet()) {
			Channel channel = channelEntry.getValue();
			SyndFeed subFeed = channel.getFeedInput();

			// adds note from which category each entry comes
			for (SyndEntry entry : subFeed.getEntries()) {
				Module dcModule = entry.getModule(DCModule.URI);
				((DCModule)dcModule).setCreator(channel.getName());
				entries.add(entry);
			}
		}

		// custom Comparator - sorts entries by date (descending)
		Collections.sort(entries, new Comparator<SyndEntry> () {
			public int compare(SyndEntry firstEntry, SyndEntry secondEntry) {
				return secondEntry.getPublishedDate().compareTo(firstEntry.getPublishedDate());
			}
		});

		return new Channel(feed);
	}

	/**
	 * @param name Name of the channel to get.
	 * @return Requested Channel object.
	 */
	public Channel getChannel(String name) {
		return channels.get(name);
	}

	/**
	 * @return The Map of belonging Channels.
	 */
	public Map<String, Channel> getChannelsMap() {
		return channels;
	}
	
	/**
	 * @return Name of the Category.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param newName New name for the Category to set.
	 */
	public void setName(String newName) {
		name = newName;
	}

	/**
	 * This getter is used to manage items in JTree.
	 * 
	 * @return Index number in Map of Categories in Model.
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * This setter is used to manage items in JTree.
	 * 
	 * @param index Index number to set in Map of Categories in Model.
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
}
