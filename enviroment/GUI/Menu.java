package enviroment.GUI;

import enviroment.GUI.menus.*;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.util.EnumMap;

class Menu {
	
	private GUI gui;
	
	private JMenuBar menuBar;
	
	protected Menu(GUI gui) {
		this.gui=gui;
		
		menuBar=new JMenuBar();
		gui.getFrame().setJMenuBar(menuBar);
		
		menuBar.add(new FileMenu(gui).getMenu());
	}
	
	protected JMenuBar getMenuBar() {
		return menuBar;
	}
}