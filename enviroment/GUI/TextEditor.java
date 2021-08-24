package enviroment.GUI;

import javax.swing.JTextArea;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
//import javax.swing.text.StyledEditorKit;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.awt.Component;
import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.IOException;

public class TextEditor {//add internal decoration class;
	
	private GUI gui;
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Font font;
	private LineNumbers lineNumbers;
	
	private File currentFile;
	
	protected TextEditor(GUI gui) {
		this.gui=gui;
		
		textArea=new JTextArea();
		//textArea.addKeyListener(this);
		font=new Font("Consolas", Font.PLAIN, 14);
		textArea.setFont(font);
		textArea.setTabSize(3);
		final int fontHeight=textArea.getFontMetrics(font).getHeight();
		textArea.setMargin(new Insets(2, 2, 2, 2));
		//CTRL+S for save, keyboard shortcut
		textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s', KeyEvent.CTRL_DOWN_MASK), "saveFile");
		textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK), "saveFile");
		textArea.getActionMap().put("saveFile", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		replaceChar('(', "()", true, false);
		replaceChar('[', "[]", true, false);
		replaceChar('{', "{}", true, false);
		replaceChar('"', "\"\"", true, false);
		replaceChar('\'', "''", true, false);
		indent();
		
		scrollPane=new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(fontHeight);
		scrollPane.setRowHeaderView(lineNumbers=new LineNumbers(this));
		scrollPane.setMinimumSize(new Dimension(300, 400));
		
		gui.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				save();
			}
		});
	}
	private void replaceChar(char toReplace, String replacement, boolean moveOne, boolean remove) {
		String actionKey="replace "+toReplace+" with "+replacement;
		textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(toReplace), actionKey);
		textArea.getActionMap().put(actionKey, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Document doc=textArea.getDocument();
				try {
					if(remove) {
						doc.remove(textArea.getCaretPosition()-1, 1);
					}
					doc.insertString(textArea.getCaretPosition(), replacement, null);
					if(moveOne) {
						textArea.setCaretPosition(textArea.getCaretPosition()-(replacement.length()-1));
					}
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	private void indent() {
		String actionKey="indent";
		textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('\n'), actionKey);
		textArea.getActionMap().put(actionKey, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Document doc=textArea.getDocument();
				try {
					doc.remove(textArea.getCaretPosition()-1, 1);
					int caretPos=textArea.getCaretPosition();
					int line=textArea.getLineOfOffset(caretPos);
					int lineStart=textArea.getLineStartOffset(line);
					int lineEnd=textArea.getLineEndOffset(line);
					String lineContent=textArea.getText(lineStart, lineEnd-lineStart);
					int lineCaratPos=caretPos-lineStart;
					String indent="\n";
					{
						boolean raiseIndent=false;
						if(lineCaratPos>0) {
							char prevChar=lineContent.charAt(lineCaratPos-1);
							if(prevChar=='{' || prevChar=='[' || prevChar=='(') raiseIndent=true;
						}
						if(lineCaratPos<lineContent.length()) {
							char nextChar=lineContent.charAt(lineCaratPos);
							if(nextChar=='}' || nextChar==']' || nextChar==')') raiseIndent=false;
						}
						if(raiseIndent) indent+="\t";
					}
					for(int i=0; i<lineContent.length(); i++) {
						if(lineContent.charAt(i)=='\t') indent+="\t";
						else break;
					}
					doc.insertString(caretPos, indent, null);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	protected JScrollPane getTextEditor() {
		return scrollPane;
	}
	protected JTextArea getTextArea() {
		return textArea;
	}
	public Font getFont() {
		return font;
	}
	public File getCurrentFile() {
		return currentFile;
	}
	
	protected void load(File file) {
		save();
		currentFile=file;
		try (BufferedReader br=new BufferedReader(new FileReader(file));) {
			String line;
			StringBuilder text=new StringBuilder();
			while((line=br.readLine())!=null) {
				text.append(line);
				text.append("\n");
			}
			textArea.setText(text.toString());
		} catch(IOException e) {
			System.out.println("failed to load: "+e.getMessage());
		}
	}
	
	protected void save() {
		if(currentFile==null) return;
		if(currentFile.getName().equals(".file metadata.METADATA")) return;
		try (PrintWriter saver=new PrintWriter(currentFile);) {
			for(String s : textArea.getText().split("\n")) {
				saver.println(s);
			}
			saver.flush();
			saver.close();
		} catch(FileNotFoundException e) {
			System.err.println("failed to save");
		}
	}
}