package enviroment.GUI;

import enviroment.Main;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI {
	
	private Main main;
	
	private JFrame frame;
	private JPanel mainPanel;
	
	private Menu menu;
	private FileExplorer fileExplorer;
	private TextEditor textEditor;
	private Console console;
	private Toolbar toolbar;
	
	public GUI(Main main) {
		this.main=main;
	}
	
	public void initialize() {
		frame=new JFrame("Sepdron's stuf");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		mainPanel=new JPanel();
		//mainPanel.setBorder(BorderFactory.createEmptyBorder());
		mainPanel.setLayout(new BorderLayout());
		
		menu=new Menu(this);
		console=new Console(this);
		fileExplorer=new FileExplorer(this);
		textEditor=new TextEditor(this);
		toolbar=new Toolbar(this);
		
		makeSplitPanes();
		mainPanel.add(toolbar.getToolbar(), BorderLayout.NORTH);
		
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				main.getCompiler().endProcess();
			}
		});
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setSize(800, 550);
	}
	
	private void makeSplitPanes() {
		JComponent fileExplorerC=fileExplorer.getFileExplorer();
		JComponent textEditorC=textEditor.getTextEditor();
		JComponent consoleC=console.getConsole();
		JSplitPane eNfe=new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			true,
			fileExplorerC,
			textEditorC
		);//editor and file explorer
		
		JSplitPane eNfe_c=new JSplitPane(
			JSplitPane.VERTICAL_SPLIT,
			true,
			eNfe,
			consoleC
		);//(editor and file explorer) and console
		
		mainPanel.add(eNfe_c, BorderLayout.CENTER);
	}
	
	public Main getMain() {
		return main;
	}
	public JFrame getFrame() {
		return frame;
	}
	public JPanel getMainPanel() {
		return mainPanel;
	}
	public TextEditor getTextEditor() {
		return textEditor;
	}
	public FileExplorer getFileExplorer() {
		return fileExplorer;
	}
	public Console getConsole() {
		return console;
	}
	public Menu getMenu() {
		return menu;
	}
}