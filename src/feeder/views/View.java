package feeder.views;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import feeder.controller.Controller;

/**
 * <b>View</b> is the main frame of this application. 
 * Initializes GUI and displays it to user, all events are listened by 
 * listeners in View and are handled in Controller.
 * 
 * @author	Paweł Dąbrowski
 * @since	2016-05-31
 * @version	1.0
 */
public class View {
	private JFrame mainFrame;
	private JButton btnAddFeed;
	private JButton btnAddCat;
	private JPanel ctpMain;
	private JTree tree;
	private JPopupMenu menu;
	private JMenuItem menuEdit;
	private JMenuItem menuDelete;
	private JEditorPane feedPresenter;
	private Controller delegate;

	/**
	 * Initializes system Look&Feel, main frame and event listeners.
	 * 
	 * @param delegate Reference to Controller.
	 */
	public View(Controller delegate) {
		this.delegate = delegate;
		mainFrame = new JFrame();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException exception) {
			View.alertMessage(exception.getMessage());
		}

		initFrame();
		eventListeners();
	}

	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
		private static final long serialVersionUID = 1L;
		private Icon folderIcon = new ImageIcon("img/folder.png");
		private Icon rssIcon = new ImageIcon("img/feed.png");

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
				boolean isExpanded, boolean isLeaf, int row, boolean isFocused) {
			Component component = super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row, isFocused);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (tree.getModel().getRoot() == node.getParent())
				setIcon(folderIcon);
			else
				setIcon(rssIcon);
			return component;
		}
	}

	/**
	 * Puts Swing widgets onto the frame.
	 */
	private void initFrame() {
		// JFrame
		mainFrame.setTitle("Feeder v1.0");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(100, 100, 1024, 600);

		// JPanel - background
		ctpMain = new JPanel();
		ctpMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainFrame.setContentPane(ctpMain);

		// Scrollable panels for content and tree
		JScrollPane scrPaneTree = new JScrollPane();
		JScrollPane scrPaneContent = new JScrollPane();

		// Buttons
		btnAddCat = new JButton("Dodaj kategori\u0119");
		btnAddFeed = new JButton("Dodaj nowy kana\u0142");

		// Context menu for tree items
		menu = new JPopupMenu();
		menuEdit = new JMenuItem("Edytuj");
		menuEdit.setIcon(new ImageIcon("img/edit.png"));
		menuDelete = new JMenuItem("Usuń");
		menuDelete.setIcon(new ImageIcon("img/delete.png"));
		menu.add(menuEdit);
		menu.add(menuDelete);

		//setting GroupLayout
		GroupLayout glCtpMain = new GroupLayout(ctpMain);
		glCtpMain.setHorizontalGroup(
				glCtpMain.createParallelGroup(Alignment.LEADING)
				.addGroup(glCtpMain.createSequentialGroup()
						.addGroup(glCtpMain.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnAddFeed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnAddCat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrPaneTree, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrPaneContent, GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE))
				);
		glCtpMain.setVerticalGroup(
				glCtpMain.createParallelGroup(Alignment.TRAILING)
				.addGroup(glCtpMain.createSequentialGroup()
						.addGroup(glCtpMain.createParallelGroup(Alignment.LEADING)
								.addGroup(glCtpMain.createSequentialGroup()
										.addComponent(scrPaneTree, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnAddCat)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnAddFeed))
								.addComponent(scrPaneContent, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
						.addContainerGap())
				);

		// JPane to display the channel content
		feedPresenter = new JEditorPane();
		feedPresenter.setEditable(false);
		feedPresenter.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		feedPresenter.setCaretPosition(0);

		// JTree which displays main tree model of the app
		tree = new JTree();
		tree.setModel(delegate.getTreeModel());
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setComponentPopupMenu(menu);

		// setting up custom renderer so we have nice icons
		DefaultTreeCellRenderer customRenderer = new CustomTreeCellRenderer();
		tree.setCellRenderer(customRenderer);

		// initializing stuff
		scrPaneContent.setViewportView(feedPresenter);
		scrPaneTree.setViewportView(tree);
		ctpMain.setLayout(glCtpMain);

		mainFrame.setVisible(true);
	}

	/**
	 * Static, simple function to display all kinds of alerts.
	 * 
	 * @param text Text to display in alert.
	 */
	public static void alertMessage(String text) {
		JOptionPane.showMessageDialog(null, text);
	}

	/**
	 * Adds listeners to all possible events and sends handling of the events to Controller.
	 */
	private void eventListeners() {
		btnAddCat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				delegate.addNewCategory();
			}
		});

		btnAddFeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				delegate.addNewFeed();
			}
		});

		menuEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				delegate.editElement();
			}
		});

		menuDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				delegate.deleteElement();
			}
		});

		feedPresenter.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(event.getURL().toURI());
						} catch (IOException | URISyntaxException exception) {
							alertMessage(exception.getMessage());
						}
					}
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				delegate.elementInTreeFocused(tree, feedPresenter);
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent location) {
				if (location.getButton() == MouseEvent.BUTTON3) {
					TreePath pathForLocation = tree.getPathForLocation(location.getPoint().x, location.getPoint().y);
					if (pathForLocation != null) {
						tree.setSelectionPath(pathForLocation);
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
						delegate.setSelectedNode(selectedNode);
					} 
					else {
						DefaultMutableTreeNode selectedNode = null;
						delegate.setSelectedNode(selectedNode);
					}
				}
				super.mousePressed(location);
			}
		});
	}
}
