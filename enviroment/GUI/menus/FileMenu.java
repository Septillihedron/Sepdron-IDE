package enviroment.GUI.menus;

import enviroment.GUI.GUI;
import enviroment.GUI.FileExplorer;
import enviroment.GUI.dialogs.IDEDialog;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileMenu {
	
	private JMenu menu;
	private FileExplorer fileExplorer;
	
	public FileMenu(GUI gui) {
		fileExplorer=gui.getFileExplorer();
		
		menu=new JMenu("File");
		
		AbstractAction refreshAction=new AbstractAction("Refresh") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.makeTree(fileExplorer.getRootNode());
			}
		};
		JMenuItem refreshItem=new JMenuItem(refreshAction);
		refreshItem.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_DOWN_MASK));
		menu.add(refreshItem);
		AbstractAction collapseAllAction=new AbstractAction("Collapse All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.collapseAll(fileExplorer.getRootNode());
			}
		};
		menu.add(collapseAllAction);
		AbstractAction expandAllAction=new AbstractAction("Expand All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileExplorer.expandAll(fileExplorer.getRootNode());
			}
		};
		menu.add(expandAllAction);
		AbstractAction newProjectAction=new AbstractAction("New Project") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file=fileExplorer.getRootFile();
				
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
					fileExplorer.getPopupMenu().createNewProject(fileExplorer.getRootNode(), file, fileTF.getText(), mainClassTF.getText());
					dialog.dispose();
				});
				
				dialog.initiate();
			}
		};
		menu.add(newProjectAction);
	}
	
	public JMenu getMenu() {
		return menu;
	}
	
}