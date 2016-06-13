package feeder;

import feeder.controller.Controller;
import feeder.views.View;

/**
 * <b>Feeder</b> is a simple RSS reader written in Java using Swing GUI widget toolkit.
 * This is a main class of the application. Initializes the View and Controller.
 * 
 * @author	Paweł Dąbrowski
 * @version	1.0
 * @since	2016-05-31
 */
public class Feeder {
	/**
	 * Main method of the application.
	 * 
	 * @param args	Not used.
	 */
	public static void main(String[] args) {
		new View(new Controller());
	}
}