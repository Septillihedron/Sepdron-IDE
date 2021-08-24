package enviroment.GUI.dialogs;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class IDEDialog {
	
	private JDialog dialog;
	private JPanel mainPanel;
	private Runnable OKAction=() -> {dispose();};
	
	public IDEDialog(Frame frame, String title) {
		dialog=new JDialog(frame, title);
		
		mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		dialog.add(mainPanel, BorderLayout.CENTER);
	}
	public void initiate() {
		addButtons();
		dialog.setResizable(false);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public void setOKAction(Runnable r) {
		OKAction=r;
	}
	
	public JTextField addTextField(String labelS, String initialValue, int initialCollum) {
		JPanel panel=new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
		panel.setBackground(Color.DARK_GRAY);
		
		JLabel label=new JLabel(labelS);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setForeground(Color.WHITE);
		panel.add(label);
		
		JTextField textField=new JTextField(initialValue, initialCollum);
		textField.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(textField);
		
		mainPanel.add(panel);
		
		return textField;
	}
	public JLabel addLabel(String labelS) {
		JPanel panel=new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
		panel.setBackground(Color.DARK_GRAY);
		
		JLabel label=new JLabel(labelS);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setForeground(Color.WHITE);
		panel.add(label);
		
		mainPanel.add(panel);
		
		return label;
	}
	private void addButtons() {
		JPanel panel=new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
		panel.setBackground(Color.DARK_GRAY);
		
		AbstractAction okAction=new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (OKAction!=null) OKAction.run();
			}
		};
		JButton okButton=new JButton(okAction);
		okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "ok");
		okButton.getActionMap().put("ok", okAction);
		panel.add(okButton);
		
		AbstractAction cancelAction=new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		JButton cancelButton=new JButton(cancelAction);
		cancelButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(cancelButton);
		
		mainPanel.add(panel);
	}
	public void dispose() {
		dialog.dispose();
	}
	
	public static class IDEString {
		String s="";
		public IDEString() {}
		public IDEString(String s) {this.s=s;}
		public void setString(String s) {this.s=s;}
		public String getString() {return s;}
	}
}