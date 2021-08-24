package enviroment.GUI;

import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.PrintStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Console {
	
	private GUI gui;
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Font font;
	
	private PrintStream errPS;
	private PrintStream outPS;
	private PipedOutputStream errPOS;
	private PipedOutputStream outPOS;
	private PipedInputStream errPIS;
	private PipedInputStream outPIS;
	
	private static int maxChar=10000;
	
	protected Console(GUI gui) {
		this.gui=gui;
		
		textArea=new JTextArea();
		textArea.setText("console\n");
		font=new Font("Consolas", Font.PLAIN, 14);
		textArea.setFont(font);
		textArea.setEditable(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		
		scrollPane=new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(textArea.getFontMetrics(font).getHeight());
		scrollPane.setMinimumSize(new Dimension(400, 100));
		
		try {
			errPOS=new PipedOutputStream();
			outPOS=new PipedOutputStream();
			errPIS=new PipedInputStream(errPOS);
			outPIS=new PipedInputStream(outPOS);
			errPS=new PrintStream(errPOS);
			outPS=new PrintStream(errPOS);
			System.setOut(outPS);
			System.setErr(errPS);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Document document=textArea.getDocument();
				try {
					if(document.getLength()>maxChar) {
						document.remove(0, document.getLength()-maxChar);
					}
					int errAvailable=errPIS.available();
					if(errAvailable>0) {
						byte[] bytes=new byte[errAvailable];
						errPIS.read(bytes);
						append(bytes);
					}
					int outAvailable=errPIS.available();
					if(outPIS.available()>0) {
						byte[] bytes=new byte[outAvailable];
						outPIS.read(bytes);
						append(bytes);
					}
				} catch(BadLocationException | IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 500);
		gui.getMain().getCompiler().setConsole(this);
	}
	
	public void append(byte[] bytes) {
		try {
			Document document=textArea.getDocument();
			if(document.getLength()>maxChar) {
				document.remove(0, document.getLength()-maxChar);
			}
			textArea.append(new String(bytes));
			textArea.setCaretPosition(document.getLength());
		} catch(BadLocationException e) {
			e.printStackTrace();
		}
	}
	public void clear() {
		Document document=textArea.getDocument();
		try {
			document.remove(0, document.getLength());
		} catch(BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	protected JScrollPane getConsole() {
		return scrollPane;
	}
}