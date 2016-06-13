package feeder.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import feeder.controller.Controller;

/**
 * <b>NewFeedDialog</b> is a View displayed when the user clicks "Add new Channel" button.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-06-02
 * @version	1.0
 */
public class NewFeedDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel urlLabel;
	protected JTextField urlField;
	protected JTextField nameField;
	protected JButton okButton;
	protected JButton cancelButton;
	protected JComboBox<String> categoryCmb;
	private Controller delegate;

	/**
	 * Constructor puts all elements to a JDialog.
	 * 
	 * @param delegate Reference to Controller.
	 */
	public NewFeedDialog(Controller delegate) {
		this.delegate = delegate;

		addComponents();
		cancelButtonListener();
		okButtonListener();
	}

	private void addComponents() {
		setTitle("Dodaj kanał RSS");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 308, 180);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		urlLabel = new JLabel("URL kanału");
		JLabel nameLabel = new JLabel("Nazwa kanału");

		JLabel categoryLabel = new JLabel("Kategoria");

		urlField = new JTextField();
		urlField.setColumns(10);

		nameField = new JTextField();
		nameField.setColumns(10);

		categoryCmb = new JComboBox<String>();
		categoryCmb.setModel(new DefaultComboBoxModel<String>(delegate.getCategories()));

		GroupLayout glContentPanel = new GroupLayout(contentPanel);
		glContentPanel.setHorizontalGroup(
				glContentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(glContentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(glContentPanel.createSequentialGroup()
										.addComponent(urlLabel)
										.addGap(18)
										.addComponent(urlField, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
								.addGroup(glContentPanel.createSequentialGroup()
										.addGroup(glContentPanel.createParallelGroup(Alignment.LEADING)
												.addComponent(nameLabel)
												.addComponent(categoryLabel))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(glContentPanel.createParallelGroup(Alignment.LEADING)
												.addComponent(categoryCmb, 0, 201, Short.MAX_VALUE)
												.addComponent(nameField, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))))
						.addContainerGap())
				);
		glContentPanel.setVerticalGroup(
				glContentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(glContentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(urlLabel)
								.addComponent(urlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(glContentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(nameLabel)
								.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(glContentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(categoryLabel)
								.addComponent(categoryCmb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);

		contentPanel.setLayout(glContentPanel);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Anuluj");
		buttonPane.add(cancelButton);
	}
	
	/**
	 * Listens to "Cancel" button actions.
	 */
	private void cancelButtonListener() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				dispose();
			}
		});
	}

	/**
	 * Listens to "OK" button actions.
	 */
	protected void okButtonListener() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				try {
					delegate.addNewFeedEvent(nameField.getText(), urlField.getText(), categoryCmb.getSelectedItem().toString());
					dispose();
				}
				catch(IllegalArgumentException exception) {
					View.alertMessage(exception.getMessage());
				}
			}
		});
	}
}
