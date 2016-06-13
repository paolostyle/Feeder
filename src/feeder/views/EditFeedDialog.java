package feeder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import feeder.controller.Controller;

/**
 * <b>EditFeedDialog</b> is a View displayed when using context menu Edit.
 * It's essentially NewFeedDialog, but performs different actions when clicking OK
 * and is filled with data related to clicked object.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-12
 * @version	1.0
 */
public class EditFeedDialog extends NewFeedDialog {
	private static final long serialVersionUID = 1L;
	private Controller delegate;
	
	/**
	 * Triggers the superclass constructor.
	 * 
	 * @param delegate Reference to Controller.
	 */
	public EditFeedDialog(Controller delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	/**
	 * Fills TextFields in the Dialog with data related to edited Channel.
	 * Executed by Controller.
	 * 
	 * @param url URL of edited Channel
	 * @param name Name of edited Channel.
	 * @param category Category to which edited Channel belongs.
	 */
	public void setTextFields(String url, String name, String category) {
		urlField.setText(url);
		nameField.setText(name);
		categoryCmb.getModel().setSelectedItem(category);
	}
	
	/* (non-Javadoc)
	 * @see feeder.views.NewFeedDialog#okButtonListener()
	 */
	@Override
	public void okButtonListener() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				try {
					if(!(urlField.getText().equals(delegate.getSelectedNodeChannelUrl()))) {
						delegate.changeChannelUrl(delegate.getSelectedNodeChannelCategory(), delegate.getSelectedNodeChannelName(),
								urlField.getText());
					}
					if(!(nameField.getText().equals(delegate.getSelectedNodeChannelName()))) {
						delegate.changeChannelName(delegate.getSelectedNodeChannelCategory(),
								delegate.getSelectedNodeChannelName(), nameField.getText());
					}
					if(!(((String) categoryCmb.getSelectedItem()).equals(delegate.getSelectedNodeChannelCategory()))) {
						delegate.changeChannelCategory(delegate.getSelectedNodeChannelName(), delegate.getSelectedNodeChannelCategory(),
								(String) categoryCmb.getSelectedItem());
					}
					dispose();
				}
				catch(IllegalArgumentException exception) {
					View.alertMessage(exception.getMessage());
				}
			}
		});
	}
}
