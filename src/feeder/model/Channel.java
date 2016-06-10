package feeder.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * <b>Channel</b> is a class representing an RSS feed (or RSS channel).
 * It's lowest in the model hierarchy and does the most important thing
 * which is parsing the XML file using ROME library and generating
 * List of News which can be then easily formatted and displayed.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-01
 * @version 0.7
 */
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private URL url;
	private SyndFeed feed;
	private boolean isAggregated;

	/**
	 * Class constructor for URL-based feeds (so the ones that user is adding).
	 * Creates the FeedSynd which is later used to generate the content;
	 * 
	 * @param name	Name of the Channel.
	 * @param url	URL of the XML file.
	 * @throws IOException Thrown when the URL is corrupted.
	 * @throws FeedException Thrown when ROME couldn't create the SyndFeed.
	 * @throws IllegalArgumentException Thrown when the URL given content isn't in XML format.
	 */
	public Channel(final String name, final String url) throws IllegalArgumentException, FeedException, IOException {
		this.name = name;
		this.url = new URL(url);
		feed = new SyndFeedInput().build(new XmlReader(this.url));
		isAggregated = false;
	}

	/**
	 * Class constructor for aggregated feeds (so the one for whole Category).
	 * 
	 * @param feed	SyndFeed aggregated by Category class method.
	 * @see Category#getAggregatedFeed()
	 */
	public Channel(final SyndFeed feed) {
		this.feed = feed;
		this.name = feed.getTitle();
		isAggregated = true;
	}
	
	/**
	 * Cleans up all the entries from SyndFeed feed, packs each
	 * of them to the News container class and then makes a List of them.
	 * 
	 * @return List of News.
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws IOException
	 */
	public List<News> getChannelContent() throws IllegalArgumentException, FeedException, IOException {
		if(isAggregated == false) {
			feed = new SyndFeedInput().build(new XmlReader(this.url));
		}
		
		List<News> content = new ArrayList<News>();
		
		for(SyndEntry entry : feed.getEntries()) {
			News news = new News();
			
			news.setTitle(entry.getTitle());
			news.setLink(entry.getLink());
			if(entry.getPublishedDate() != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
				news.setDate(dateFormat.format(entry.getPublishedDate()));
			}
			if(entry.getDescription() != null) {
				news.setDescription(entry.getDescription().getValue().trim().replaceAll("\\<.*?\\>", ""));
			}
			if(isAggregated == true) {
				Module dcModule = entry.getModule(DCModule.URI);
				news.setChannel(((DCModule)dcModule).getCreator());
			}
			
			content.add(news);
		}
		
		return content;
	}
	
	/**
	 * @return SyndFeed object.
	 */
	public SyndFeed getFeedInput() {
		return feed;
	}

	/**
	 * @return {@link Channel} name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return URL as String.
	 */
	public String getUrl() {
		return url.toString();
	}
	
	public boolean isAggregated() {
		return isAggregated;
	}
}
