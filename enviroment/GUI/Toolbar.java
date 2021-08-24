package enviroment.GUI;

import enviroment.func.compiler.Compiler;
import enviroment.metadata.MetadataObject;
import enviroment.metadata.MetadataType;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.io.File;

public class Toolbar {
	
	private GUI gui;
	private TextEditor textEditor;
	private Compiler compiler;
	
	private JToolBar toolbar;
	
	public Toolbar(GUI gui) {
		this.gui=gui;
		this.textEditor=gui.getTextEditor();
		this.compiler=gui.getMain().getCompiler();
		
		makeToolbar();
	}
	
	private void makeToolbar() {
		toolbar=new JToolBar();
		
		AbstractAction buildAction=new AbstractAction("Build") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files=getFiles();
				if(files==null) return;
				File ftc=files[0];
				boolean built=compiler.compile(ftc);
				if(built) System.out.println("Build success");
			}
		};
		JButton buildButton=new JButton(buildAction);
		toolbar.add(buildButton);
		AbstractAction buildNrunAction=new AbstractAction("Build & Run") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files=getFiles();
				if(files==null) return;
				File ftc=files[0];
				MetadataObject metadata=new MetadataObject(files[1]);
				boolean built=compiler.compile(ftc);
				if(built) {
					boolean running=compiler.run(new File(ftc, "SepdronIDE-Program-Build"), metadata.get(MetadataType.MAINCLASS));
					if(running) System.out.println("Code executed");
				}
			}
		};
		JButton buildNrunButton=new JButton(buildNrunAction);
		toolbar.add(buildNrunButton);
		AbstractAction runAction=new AbstractAction("Run") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files=getFiles();
				if(files==null) return;
				File ftc=files[0];
				MetadataObject metadata=new MetadataObject(files[1]);
				boolean running=compiler.run(new File(ftc, "SepdronIDE-Program-Build"), metadata.get(MetadataType.MAINCLASS));
				if(running) System.out.println("Code executed");
			}
		};
		JButton runButton=new JButton(runAction);
		toolbar.add(runButton);
	}
	
	private File[] getFiles() {
		File ftc=textEditor.getCurrentFile();
		if(ftc==null) {
			System.err.println("failed to find project file");
			return null;
		}
		File metadataFile=new File(ftc, ".file metadata.METADATA");
		MetadataObject metadata;
		while(!ftc.isDirectory() ||
			!metadataFile.exists() ||
			!(metadata=new MetadataObject(metadataFile)).get(MetadataType.TYPE).equals("project")) {
				ftc=ftc.getParentFile();
				if(ftc==null) {
					System.err.println("failed to find project file");
					return null;
				}
				metadataFile=new File(ftc, ".file metadata.METADATA");
		}
		return new File[]{ftc, metadataFile};
	}
	
	public JToolBar getToolbar() {
		return toolbar;
	}
	
}