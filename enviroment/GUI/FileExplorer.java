package enviroment.GUI;

import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.io.File;

public class FileExplorer implements MouseListener {
	
	private GUI gui;
	
	private JTree tree;
	private JScrollPane scrollPane;
	private File rootFile;
	private DefaultMutableTreeNode rootNode;
	
	private FileExplorerPopupMenu popupMenu;
	
	protected FileExplorer(GUI gui) {
		this.gui=gui;
		
		Path path=FileSystems.getDefault().getPath("projects");
		rootFile=path.toFile();
		rootFile.mkdir();
		
		rootNode=new DefaultMutableTreeNode(new Item(rootFile));
		tree=new JTree(rootNode);
		tree.addMouseListener(this);
		
		makeTree(rootNode);
		popupMenu=new FileExplorerPopupMenu(gui, this, rootFile, rootNode);
		scrollPane=new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setMinimumSize(new Dimension(200, 400));
	}
	
	public JTree getTree() {
		return tree;
	}
	public JScrollPane getFileExplorer() {
		return scrollPane;
	}
	public FileExplorerPopupMenu getPopupMenu() {
		return popupMenu;
	}
	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}
	public File getRootFile() {
		return rootFile;
	}
	
	public void expandAll(DefaultMutableTreeNode node) {
		if(node.equals(rootNode)) {
			for(int i=0; i<tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
			return;
		}
		DefaultMutableTreeNode nextNode=node.getNextSibling();
		DefaultMutableTreeNode parent=(DefaultMutableTreeNode) node.getParent();
		while(nextNode==null) {
			if(parent==null) {
				break;
			}
			nextNode=parent.getNextSibling();
			parent=(DefaultMutableTreeNode) parent.getParent();
		}
		TreePath begining=new TreePath(node.getPath());
		if(nextNode==null) {
			for(int i=tree.getRowForPath(begining); i<tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		} else {
			TreePath ending=new TreePath(nextNode.getPath());
			for(int i=tree.getRowForPath(begining); i<tree.getRowForPath(ending); i++) {
				tree.expandRow(i);
			}
		}
	}
	public void collapseAll(DefaultMutableTreeNode node) {
		if(node.equals(rootNode)) {
			for(int i=tree.getRowCount()-1; i>=0; i--) {
				tree.collapseRow(i);
			}
			return;
		}
		DefaultMutableTreeNode nextNode=node.getNextSibling();
		DefaultMutableTreeNode parent=(DefaultMutableTreeNode) node.getParent();
		while(nextNode==null) {
			if(parent==null) {
				break;
			}
			nextNode=parent.getNextSibling();
			parent=(DefaultMutableTreeNode) parent.getParent();
		}
		TreePath begining=new TreePath(node.getPath());
		if(nextNode==null) {
			for(int i=tree.getRowCount()-1; i>=tree.getRowForPath(begining); i--) {
				tree.collapseRow(i);
			}
		} else {
			TreePath ending=new TreePath(nextNode.getPath());
			for(int i=tree.getRowForPath(ending)-1; i>=tree.getRowForPath(begining); i--) {
				tree.collapseRow(i);
			}
		}
	}
	
	public void makeTree(DefaultMutableTreeNode node) {
		node.removeAllChildren();
		File file=((Item) node.getUserObject()).getFile();
		if(file.isDirectory()) recMakeTree(file, node);
		tree.updateUI();
	}
	private void recMakeTree(File rootFile, DefaultMutableTreeNode rootNode) {
		if(rootFile==null) return;
		for(File f:rootFile.listFiles()) {
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(new Item(f));
			rootNode.add(node);
			if(f.isDirectory()) {
				recMakeTree(f, node);
			}
		}
	}
	private Item getItem(TreePath path) {
		if(path==null) return null;
		DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
		return (Item) node.getUserObject();
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount()<2) return;
			TreePath path=tree.getPathForLocation(e.getX(), e.getY());
			Item item=getItem(path);
			if(item==null || item.isFolder()) return;
			item.open(gui);
		}
	}
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			TreePath path=tree.getPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(path);
		}
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	
	public class Item {
		
		private File file;
		
		public Item(File file) {
			this.file=file;
		}
		
		public String toString() {
			return file.getName();
		}
		public File getFile() {
			return file;
		}
		public boolean isFolder() {
			return file.isDirectory();
		}
		public void open(GUI gui) {
			gui.getTextEditor().load(file);
		}
	}
}