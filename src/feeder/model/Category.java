package feeder.model;

import java.io.IOException;
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
import com.rometools.rome.io.FeedException;

/**
 * <b>Category</b> represents folder/parent of Channel or multiple Channels.
 * Used mostly to organize Channels, generates aggregated feed.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-01
 * @version	0.7
 */
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private Integer index = null;
	private Map<String, Channel> channels;

	/**
	 * Constructor. Creates List object containing related {@link Channel}s.
	 * 
	 * @param name Name of the category visible in the tree.
	 */
	public Category(String name) {
		channels = new HashMap<String, Channel>();
		this.name = name;
	}

	/**
	 * Simply adds {@link Channel} to the list of channels.
	 * 
	 * @param channel Channel object to add
	 */
	public void addChannel(Channel channel) {
		channels.put(channel.getName(), channel);
	}

	/**
	 * Aggregates feeds from all {@link Channel}s belonging to the {@link Category} and
	 * sorts them by date of publication.
	 * 
	 * @return HTML-formatted String with data like in {@link Channel#convertToHTML(SyndFeed, boolean)}
	 * @throws FeedException
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public Channel getAggregatedFeed() throws FeedException, IllegalArgumentException, IOException {
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

	public Channel getChannelContent(String name) throws IllegalArgumentException, FeedException, IOException {
		return channels.get(name);
	}
	
	/**
	 * @return Name of the {@link Category}.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the channels
	 */
	public Map<String, Channel> getChannelsMap() {
		return channels;
	}
	
	/**
	 * @param index
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return
	 */
	public Integer getIndex() {
		return index;
	}
}
