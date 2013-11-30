package client.gui;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import client.core.Messenger;
import javax.swing.JButton;

public class PrivatePanel extends ChatPanel {

	private JTextPane textPane;
	private JTextPane textPane_1;
	
	public PrivatePanel(Messenger m, ConfigPanel configPanel, String name) {
		// TODO don't forget to change back
		super(m, new ConfigPanel(), name);
//		super(m, configPanel, name);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 723, 342);
		add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 723, 71);
		add(scrollPane_1);
		
		textPane_1 = new JTextPane();
		scrollPane_1.setViewportView(textPane_1);
		
		JButton btnClear = new JButton("clear");
		btnClear.setBounds(614, 490, 117, 25);
		add(btnClear);
		
		JButton btnSend = new JButton("send");
		btnSend.setBounds(485, 490, 117, 25);
		add(btnSend);
		
		JButton btnSendFile = new JButton("send file");
		btnSendFile.setBounds(10, 490, 117, 25);
		add(btnSendFile);
	}

	@Override
	public void appendMessage(String speaker, String content) {
		// TODO Auto-generated method stub
		
	}
}
