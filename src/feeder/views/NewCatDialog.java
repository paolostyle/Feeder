package feeder.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import feeder.controller.Controller;

public class NewCatDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JTextField catNameField;
	private Controller delegate;

	public NewCatDialog(Controller delegate) {
		this.delegate = delegate;
		
		setTitle("Dodaj kategorię");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 308, 118);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel catNameLabel = new JLabel("Nazwa kategorii");
		
		catNameField = new JTextField();
		catNameField.setColumns(10);
		GroupLayout glContentPanel = new GroupLayout(contentPanel);
		glContentPanel.setHorizontalGroup(
			glContentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPanel.createSequentialGroup()
					.addGap(16)
					.addComponent(catNameLabel)
					.addGap(18)
					.addComponent(catNameField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(22, Short.MAX_VALUE))
		);
		glContentPanel.setVerticalGroup(
			glContentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(glContentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(glContentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(catNameLabel)
						.addComponent(catNameField, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(21, Short.MAX_VALUE))
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
		
		eventListeners();
	}
	
	public void eventListeners() {
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent argument) {
				dispose();
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				try {
					dispose();
					delegate.addNewCategoryEvent(catNameField.getText());
				}
				catch(IllegalArgumentException exception) {
					View.alertMessage(exception.getMessage());
				}
			}
		});
	}
}
