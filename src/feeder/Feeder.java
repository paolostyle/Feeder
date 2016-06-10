package feeder;

import feeder.controller.Controller;
import feeder.views.View;

public class Feeder {
	public static void main(String[] args) {
		new View(new Controller());
	}
}