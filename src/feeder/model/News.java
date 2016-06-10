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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * @return the category
	 */
	public String getChannel() {
		return channel;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}