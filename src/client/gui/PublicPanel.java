package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import client.core.Messenger;

public class PublicPanel extends ChatPanel {
	private JList<String> list;
	private DefaultListModel<String> onlineModel;

	public PublicPanel(Messenger m, ConfigPanel configPanel, String name) {
		// TODO don't forget to change back
		super(m, new ConfigPanel(), name);
//		super(m, configPanel, name);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 579, 342);
		add(scrollPane);
		
		readPane = new JTextPane();
		scrollPane.setViewportView(readPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 579, 71);
		add(scrollPane_1);
		
		writePane = new JTextPane();
		scrollPane_1.setViewportView(writePane);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(598, 10, 166, 501);
		add(scrollPane_2);
		
		onlineModel = new DefaultListModel<>();
		list = new JList();
		list.setModel(onlineModel);
		scrollPane_2.setViewportView(list);
		
		JButton btnSend = new JButton("send");
		btnSend.setBounds(393, 488, 93, 23);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		add(btnSend);
		
		JButton btnClear = new JButton("clear");
		btnClear.setBounds(496, 488, 93, 23);
		add(btnClear);
	}
	
	
	private void sendMessage() {
		m.sendPublicMessage(getWriteText());
	}
	
	public void addUser(String[] user) {
		// TODO
	}
	
}
