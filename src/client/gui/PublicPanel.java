package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import client.core.Messenger;

public class PublicPanel extends ChatPanel {
	private JList<String> list;
	private DefaultListModel<String> onlineModel;

	public PublicPanel(Messenger m, ConfigPanel configPanel, String name) {
		// TODO don't forget to change back
//		super(m, new ConfigPanel(), name);
		super(m, configPanel, name);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 579, 342);
		add(scrollPane);
		
		readPane = new JTextPane();
		readPane.setEditable(false);
		scrollPane.setViewportView(readPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 579, 71);
		add(scrollPane_1);
		
		writePane = new JTextPane();
		writePane.setToolTipText("press enter to send");
		writePane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
					writePane.setText("");
				}
			}
		});
		scrollPane_1.setViewportView(writePane);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(598, 10, 166, 501);
		add(scrollPane_2);
		
		onlineModel = new DefaultListModel<>();
		list = new JList<>();
		list.setModel(onlineModel);
		scrollPane_2.setViewportView(list);
		
		JButton btnSend = new JButton("send");
		btnSend.setBounds(393, 488, 93, 23);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				writePane.setText("");
			}
		});
		add(btnSend);
		
		JButton btnClear = new JButton("clear");
		btnClear.setBounds(496, 488, 93, 23);
		add(btnClear);
	}
	
	
	private void sendMessage() {
		String toSend = getWriteText();
		if (toSend == null) {
			JOptionPane.showMessageDialog(this, "can't send null message");
			return;
		}
		m.sendPublicMessage(toSend);
	}
	
	public void addUser(String[] user) {
		// TODO sort
		StringBuilder sb = new StringBuilder();
		for (String name : user) {
			onlineModel.addElement(name);
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("enter.");
	}
	
	public void removeUser(String[] user) {
		StringBuilder sb = new StringBuilder();
		for (String name : user) {
			onlineModel.removeElement(name);
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("leave.");
	}
	
	@Override
	public void appendMessage(String speaker, String message) {
		super.appendMessage(speaker, message);
		readPane.setCaretPosition(readPane.getText().length());
	}
}
