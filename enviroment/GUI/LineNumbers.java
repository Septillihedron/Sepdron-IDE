package enviroment.GUI;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

public class LineNumbers extends JComponent {
	
	private TextEditor textEditor;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	public LineNumbers(TextEditor textEditor) {
		this.textEditor=textEditor;
		this.textArea=textEditor.getTextArea();
		this.scrollPane=textEditor.getTextEditor();
		setPreferredSize(new Dimension(50, 400));
		
		textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('\n'), "repaint");
		textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke('\b'), "repaint");
		textArea.getActionMap().put("repaint", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaint();
			}
		});
	}
	
	protected void paintComponent(Graphics g) {
		final Font font=textEditor.getFont();
		final FontMetrics fontMetrics=textArea.getFontMetrics(font);
		
		final int spacing=fontMetrics.getHeight();
		final int lineAmt=textArea.getLineCount();
		
		final int scrollValue=scrollPane.getVerticalScrollBar().getValue();
		
		final Rectangle clipBounds=g.getClipBounds();
		final int gwidth=clipBounds.width;
		final int gheight=clipBounds.height;
		
		g.setFont(font);
		g.setColor(new Color(5, 5, 50));
		g.fillRect(0, 0, gwidth, gheight);
		g.setColor(Color.WHITE);
		
		int firstVisibleNum=(int) Math.floor(scrollValue/spacing);
		
		for(int i=firstVisibleNum-1; i<lineAmt+(lineAmt<24?0:1); i++) {
			String s=i+1+"";
			g.drawString(s, gwidth-fontMetrics.stringWidth(s)-10, spacing/2+i*spacing-scrollValue+5);
		}
	}
	
}