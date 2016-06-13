package feeder.model;

import java.io.Serializable;

/**
 * <b>News</b> is a container class for a single news item.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-10
 * @version 1.0
 */
public class News implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String link;
	private String date;
	private String channel;
	private String description;

	/**
	 * @return The News title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return Hyperlink to the news as a String.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link Hyperlink to set.
	 */
	public void setLink(final String link) {
		this.link = link;
	}

	/**
	 * @return News' publication date.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date Publication date to set.
	 */
	public void setDate(final String date) {
		this.date = date;
	}

	/**
	 * @return Source (Channel) of the news. Used in aggregated feeds.
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel Source Channel name to set.
	 */
	public void setChannel(final String channel) {
		this.channel = channel;
	}

	/**
	 * @return The news' description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}