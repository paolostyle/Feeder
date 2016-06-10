package feeder.views;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import javax.swing.tree.TreeSelectionModel;

import com.rometools.rome.io.FeedException;

import feeder.controller.Controller;

public class View extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton btnAddFeed;
	private JButton btnAddCat;
	private JPanel ctpMain;
	private JTree tree;
	private JEditorPane feedPresenter;
	private Controller delegate;
	
	public View(Controller delegate) {
		this.delegate = delegate;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		initFrame();
		eventListeners();
	}

	private void initFrame() {
		//JFrame
		setTitle("Feeder v0.4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 600);

		//JPanel - background
		ctpMain = new JPanel();
		ctpMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(ctpMain);

		//Scrollable panels for content and tree
		JScrollPane scrPaneTree = new JScrollPane();
		JScrollPane scrPaneContent = new JScrollPane();

		//Buttons
		btnAddCat = new JButton("Dodaj kategori\u0119");
		btnAddFeed = new JButton("Dodaj nowy kana\u0142");

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
						.addComponent(scrPaneContent, GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE))
				);
		glCtpMain.setVerticalGroup(
				glCtpMain.createParallelGroup(Alignment.LEADING)
				.addComponent(scrPaneContent, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
				.addGroup(Alignment.TRAILING, glCtpMain.createSequentialGroup()
						.addComponent(scrPaneTree, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnAddCat)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnAddFeed)
						.addContainerGap())
				);

		feedPresenter = new JEditorPane();
		feedPresenter.setEditable(false);
		feedPresenter.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		feedPresenter.setCaretPosition(0);

		tree = new JTree();
		tree.setModel(delegate.getTreeModel());
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;
			private Icon folderIcon = new ImageIcon("img/folder.png");
            private Icon rssIcon = new ImageIcon("img/feed.png");
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
            boolean expanded, boolean isLeaf, int row, boolean focused) {
                Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (tree.getModel().getRoot() == node.getParent())
                    setIcon(folderIcon);
                else
                    setIcon(rssIcon);
                return component;
            }
        });
		
		scrPaneContent.setViewportView(feedPresenter);
		scrPaneTree.setViewportView(tree);
		ctpMain.setLayout(glCtpMain);
		
		setVisible(true);
	}

	public static void alertMessage(String text) {
		JOptionPane.showMessageDialog(null, text);
	}
	
	public void eventListeners() {
		btnAddCat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				NewCatDialog dialog = new NewCatDialog(delegate);
				dialog.setVisible(true);
			}
		});
		
		btnAddFeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent argument) {
				NewFeedDialog dialog = new NewFeedDialog(delegate);
				dialog.setVisible(true);
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
				try {
					String content = delegate.elementInTreeFocusedEvent(tree);
					feedPresenter.setText(content);
					feedPresenter.setCaretPosition(0);
				} catch (NoSuchElementException | IllegalArgumentException | FeedException | IOException exception) {
					alertMessage(exception.getMessage());
				}
			}
		});
	}
}
