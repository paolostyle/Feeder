package feeder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import feeder.controller.Controller;

/**
 * <b>EditCategoryDialog</b> is a View displayed when using context menu Edit.
 * It's essentially NewCategoryDialog, but performs different actions when clicking OK
 * and is filled with data related to clicked object. 
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-12
 * @version	1.0
 */
public class EditCategoryDialog extends NewCategoryDialog {
	private static final long serialVersionUID = 1L;
	private Controller delegate;
	
	/**
	 * Triggers the superclass constructor.
	 * 
	 * @param delegate Reference to Controller.
	 */
	public EditCategoryDialog(Controller delegate) {
		super(delegate);
		this.delegate = delegate;
	}
	
	/**
	 * Fills TextFields in the Dialog with data related to edited Category.
	 * Executed by Controller.
	 * 
	 * @param oldCategoryName Current name of edited category.
	 */
	public void setTextFields(String oldCategoryName) {
		catNameField.setText(oldCategoryName);
	}
	
	/* (non-Javadoc)
	 * @see feeder.views.NewCategoryDialog#okButtonListener()
	 */
	@Override
	public void okButtonListener() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				try {
					dispose();
					delegate.changeCategoryName(delegate.getSelectedNode().toString(), catNameField.getText());
				}
				catch(IllegalArgumentException exception) {
					View.alertMessage(exception.getMessage());
				}
			}
		});
	}
}
