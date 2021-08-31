package enviroment.GUI;

import enviroment.func.compiler.Compiler;
import enviroment.metadata.MetadataObject;
import enviroment.metadata.MetadataType;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Toolbar {
	
	private GUI gui;
	private TextEditor textEditor;
	private Compiler compiler;
	
	private JToolBar toolbar;
	
	private JButton buildButton;
	private ImageIcon buildIcon;
	private ImageIcon activeBuildIcon;
	
	private JButton playButton;
	private ImageIcon playIcon;
	private ImageIcon activePlayIcon;
	
	private JButton stopButton;
	private ImageIcon stopIcon;
	private ImageIcon activeStopIcon;
	
	public Toolbar(GUI gui) {
		this.gui=gui;
		this.textEditor=gui.getTextEditor();
		this.compiler=gui.getMain().getCompiler();
		
		loadIcons();
		makeToolbar();
	}
	
	private void makeToolbar() {
		toolbar=new JToolBar();
		toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		AbstractAction buildAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files=getFiles();
				if(files==null) return;
				File ftc=files[0];
				boolean built=compiler.compile(ftc);
				if(built) {
					System.out.println("Build success");
					buildButton.setIcon(activeBuildIcon);
					playButton.setIcon(playIcon);
				}
			}
		};
		buildButton=new JButton(buildAction);
		buildButton.setIcon(buildIcon);
		toolbar.add(buildButton);
		AbstractAction playAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files=getFiles();
				if(files==null) return;
				File ftc=files[0];
				MetadataObject metadata=new MetadataObject(files[1]);
				boolean built=compiler.compile(ftc);
				if(built) {
					boolean running=compiler.run(new File(ftc, "SepdronIDE-Program-Build"), metadata.get(MetadataType.MAINCLASS));
					if(running) {
						System.out.println("Code executed");
						playButton.setIcon(activePlayIcon);
						buildButton.setIcon(buildIcon);
					}
				}
			}
		};
		playButton=new JButton(playAction);
		playButton.setIcon(playIcon);
		toolbar.add(playButton);
		AbstractAction stopAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				compiler.endProcess();
				buildButton.setIcon(buildIcon);
				playButton.setIcon(playIcon);
			}
		};
		stopButton=new JButton(stopAction);
		stopButton.setIcon(stopIcon);
		stopButton.setPressedIcon(activeStopIcon);
		toolbar.add(stopButton);
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
	
	private void loadIcons() {
		try {
			BufferedImage buttonIcons=ImageIO.read(gui.getMain().getClass().getResource("resource/toolbarIcons/ButtonIcons.png"));
			buildIcon      =new ImageIcon(buttonIcons.getSubimage(00, 32, 32, 32));
			activeBuildIcon=new ImageIcon(buttonIcons.getSubimage(00, 00, 32, 32));
			playIcon       =new ImageIcon(buttonIcons.getSubimage(32, 32, 32, 32));
			activePlayIcon =new ImageIcon(buttonIcons.getSubimage(32, 00, 32, 32));
			stopIcon       =new ImageIcon(buttonIcons.getSubimage(64, 32, 32, 32));
			activeStopIcon =new ImageIcon(buttonIcons.getSubimage(64, 00, 32, 32));
		} catch (IOException e) {
			System.err.println("failed to load toolbar build icons");
		}
	}
	
	public JToolBar getToolbar() {
		return toolbar;
	}
	
}