package enviroment.GUI;

import enviroment.GUI.dialogs.IDEDialog;
import enviroment.GUI.FileExplorer.Item;
import enviroment.metadata.MetadataObject;
import enviroment.metadata.MetadataType;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.JTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class FileExplorerPopupMenu {
	
	private GUI gui;
	private JTree tree;
	private JPopupMenu popupMenu;
	private FileExplorer fileExplorer;
	private File rootFile;
	private DefaultMutableTreeNode rootNode;
	private JMenuItem refreshItem, collapseAllItem, expandAllItem, deleteItem;
	private JMenu newMenu;
	private JMenuItem newProjectItem, newPackageItem, newClassItem;
	
	protected FileExplorerPopupMenu(GUI gui, FileExplorer fileExplorer, File rootFile, DefaultMutableTreeNode rootNode) {
		this.gui=gui;
		this.fileExplorer=fileExplorer;
		this.rootFile=rootFile;
		this.rootNode=rootNode;
		this.tree=fileExplorer.getTree();
		
		popupMenu=new JPopupMenu();
		makePopupMenu();
	}
	
	private void makePopupMenu() {
		PopupListener popupListener=new PopupListener();
		tree.addMouseListener(popupListener);
		
		AbstractAction refreshAction=new AbstractAction("Refresh") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.makeTree((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent());
			}
		};
		refreshItem=new JMenuItem(refreshAction);
		popupMenu.add(refreshItem);
		
		
		AbstractAction collapseAllAction=new AbstractAction("Collapse All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.collapseAll((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent());
			}
		};
		collapseAllItem=new JMenuItem(collapseAllAction);
		popupMenu.add(collapseAllItem);
		AbstractAction expandAllAction=new AbstractAction("Expand All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.expandAll((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent());
			}
		};
		expandAllItem=new JMenuItem(expandAllAction);
		popupMenu.add(expandAllItem);
		AbstractAction deleteAction=new AbstractAction("Delete") {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				File file=((Item) node.getUserObject()).getFile();
				if(file.equals(fileExplorer.getRootFile())) return;
				IDEDialog dialog=new IDEDialog(gui.getFrame(), "Delete file");
				dialog.addLabel("Delete "+file.getPath()+"?");
				dialog.setOKAction(()-> {
					applyToAllFile(file, f->f.delete());
					fileExplorer.makeTree((DefaultMutableTreeNode) node.getParent());
					dialog.dispose();
				});
				
				dialog.initiate();
			}
		};
		deleteItem=new JMenuItem(deleteAction);
		popupMenu.add(deleteItem);
		makeNewMenu();
	}
	private void makeNewMenu() {
		newMenu=new JMenu("New");
		popupMenu.add(newMenu);
		AbstractAction newProjectAction=new AbstractAction("New Project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path=tree.getSelectionPath();
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
				Item item=(Item) node.getUserObject();
				File file=item==null ? rootFile : item.getFile();
				
				IDEDialog dialog=new IDEDialog(gui.getFrame(), "New Project");
				dialog.addLabel("Make project in "+file.getPath());
				JTextField fileTF=dialog.addTextField("File name: ", "my project", 25);
				JTextField mainClassTF=dialog.addTextField("Main class name: ", "Main", 25);
				mainClassTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				dialog.setOKAction(()-> {
					boolean correctFileName=fileTF.getText().matches("[^/\\\\:;]+");
					boolean correctMClassName=mainClassTF.getText().matches("[a-zA-Z]{1}[0-9a-zA-Z]*(\\.[a-zA-Z]{1}[0-9a-zA-Z]*)*");
					if(!correctFileName) fileTF.setBorder(BorderFactory.createLineBorder(Color.RED));
					else fileTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if(!correctMClassName) mainClassTF.setBorder(BorderFactory.createLineBorder(Color.RED));
					else mainClassTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if(!(correctFileName && correctMClassName)) return;
					createNewProject(node, file, fileTF.getText(), mainClassTF.getText());
					dialog.dispose();
				});
				
				dialog.initiate();
			}
		};
		newProjectItem=new JMenuItem(newProjectAction);
		newMenu.add(newProjectItem);
		AbstractAction newPackageAction=new AbstractAction("New Package") {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				File file=((Item) node.getUserObject()).getFile();
				MetadataObject metadata=new MetadataObject(new File(file, ".file metadata.METADATA"));
				
				IDEDialog dialog=new IDEDialog(gui.getFrame(), "New Package");
				dialog.addLabel("Make package in "+file.getPath());
				JTextField packageTF=dialog.addTextField("Package name: ", "package", 25);
				packageTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				dialog.setOKAction(()-> {
					boolean correctPName=packageTF.getText().matches("[a-zA-Z]{1}[0-9a-zA-Z]*");
					if(!correctPName) packageTF.setBorder(BorderFactory.createLineBorder(Color.RED));
					else packageTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if(!correctPName) return;
					String prevPackageName=metadata.get(MetadataType.PACKAGE);
					String packageName=packageTF.getText();
					createNewPackage(node, file, (prevPackageName==null? "" : prevPackageName+".")+packageName, packageName);
					dialog.dispose();
				});
				dialog.initiate();
			}
		};
		newPackageItem=new JMenuItem(newPackageAction);
		newMenu.add(newPackageItem);
		AbstractAction newClassAction=new AbstractAction("New Class") {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				File file=((Item) node.getUserObject()).getFile();
				MetadataObject metadata=new MetadataObject(new File(file, ".file metadata.METADATA"));
				
				IDEDialog dialog=new IDEDialog(gui.getFrame(), "New Class");
				dialog.addLabel("Make class in "+file.getPath());
				JTextField classTF=dialog.addTextField("Class name: ", "class", 25);
				classTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				dialog.setOKAction(()-> {
					boolean correctCName=classTF.getText().matches("[a-zA-Z]{1}[0-9a-zA-Z]*");
					if(!correctCName) classTF.setBorder(BorderFactory.createLineBorder(Color.RED));
					else classTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if(!correctCName) return;
					String prevPackageName=metadata.get(MetadataType.PACKAGE);
					String packageName=classTF.getText();
					createNewClass(node, file, classTF.getText(), (prevPackageName==null? "" : prevPackageName), false);
					dialog.dispose();
				});
				dialog.initiate();
			}
		};
		newClassItem=new JMenuItem(newClassAction);
		newMenu.add(newClassItem);
	}
	public File createNewProject(DefaultMutableTreeNode node, File rootFile, String fileName, String mainClass) {
		File file=new File(rootFile, fileName);
		if(file.exists()) {
			IDEDialog dialog=new IDEDialog(gui.getFrame(), "error");
			dialog.setOKAction(() -> {dialog.dispose();});
			dialog.addLabel("File already exsists");
			dialog.initiate();
			return null;
		}
		file.mkdir();
		File metadataFile=new File(file, ".file metadata.METADATA");
		MetadataObject metadata=new MetadataObject()
			.put(MetadataType.TYPE, "project")
			.put(MetadataType.MAINCLASS, mainClass);
		metadata.save(metadataFile);
		try {
			Files.setAttribute(metadataFile.toPath(), "dos:hidden", true);
		} catch (IOException e) {}
		String[] packages=mainClass.split("[.]");
		if(packages.length>1) {
			File packageFile=file;
			String packageName=packages[0];
			packageFile=createNewPackage(node, packageFile, packageName, packages[0]);
			for(int i=1; i<packages.length-1; i++) {
				packageName+="."+packages[i];
				packageFile=createNewPackage(node, packageFile, packageName, packages[i]);
			}
			createNewClass(node, packageFile, packages[packages.length-1], packageName, true);
		} else {
			createNewClass(node, file, mainClass, "", true);
		}
		
		fileExplorer.makeTree(node);
		return file;
	}
	private File createNewPackage(DefaultMutableTreeNode node, File rootFile, String fullPackageName, String packageName) {
		File file=new File(rootFile, packageName);
		file.mkdir();
		File metadataFile=new File(file, ".file metadata.METADATA");
		MetadataObject metadata=new MetadataObject()
			.put(MetadataType.TYPE, "package")
			.put(MetadataType.PACKAGE, fullPackageName);
		metadata.save(metadataFile);
		try {
			Files.setAttribute(metadataFile.toPath(), "dos:hidden", true);
		} catch (IOException e) {}
		fileExplorer.makeTree(node);
		return file;
	}
	private File createNewClass(DefaultMutableTreeNode node, File rootFile, String className, String packageName, boolean mainClass) {
		File file=new File(rootFile, className+".java");
		final String upper=(packageName.equals("") ? "" : "package "+packageName+";\n")+
							"\n"+
							"public class "+className+" {\n"+
							"\t\n";
		try(BufferedWriter bw=new BufferedWriter(new FileWriter(file))) {
			if(mainClass) {
				bw.write(
					upper+
					"\tpublic static void main(String[] args) {\n"+
					"\t\t\n"+
					"\t}\n"+
					"}\n"
				);
			} else {
				bw.write(
					upper+
					"\t\n"+
					"}\n"
				);
			}
			bw.flush();
		} catch (IOException e) {}
		fileExplorer.makeTree(node);
		return file;
	}
	private void applyToAllFile(File root, Consumer<File> func) {
		if(root.isDirectory()) {
			for(File f:root.listFiles()) {
				applyToAllFile(f, func);
			}
		}
		func.accept(root);
	}
	
	private class PopupListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {
			if(popupMenu.isPopupTrigger(e)) {
				boolean hasSelection=tree.getSelectionCount()>=1;
				boolean isProject=hasSelection ? isProject() : false;
				refreshItem.setEnabled(hasSelection);
				collapseAllItem.setEnabled(hasSelection);
				expandAllItem.setEnabled(hasSelection);
				deleteItem.setEnabled(hasSelection);
				newMenu.setEnabled(hasSelection);
				newProjectItem.setEnabled(hasSelection);
				newPackageItem.setEnabled(hasSelection && isProject);
				newClassItem.setEnabled(hasSelection && isProject);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		private boolean isProject() {
			TreePath path=tree.getSelectionPath();
			DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
			Item item=(Item) node.getUserObject();
			if(item==null || !item.isFolder()) return false;
			File file=item.getFile();
			File metadataFile=new File(file, ".file metadata.METADATA");
			if(!metadataFile.exists()) return false;
			MetadataObject metadata=new MetadataObject(metadataFile);
			String type=metadata.get(MetadataType.TYPE);
			if(!type.equals("project") && !type.equals("package")) return false;
			return true;
		}
		public void mousePressed(MouseEvent e) {
			if(popupMenu.isPopupTrigger(e)) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
	}
}