package client.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import client.core.Messenger;

public class PublicPanel extends ChatPanel {
	private JList<String> list;
	private DefaultListModel<String> onlineModel;
	private JPopupMenu listPop;
	private MainFrame parent;

	public PublicPanel(MainFrame parent, Messenger m, ConfigPanel configPanel,
			String name) {
		super(m, configPanel, name);
		this.parent = parent;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 579, 342);
		add(scrollPane);

		scrollPane.setViewportView(readPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 579, 71);
		add(scrollPane_1);

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
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(new Point(e.getX(), e.getY()));
				list.setSelectedIndex(index);
				if (e.getButton() == MouseEvent.BUTTON3) {
					listPop.show(list, e.getX(), e.getY());
				}
			}
		});
		scrollPane_2.setViewportView(list);

		btnSend.setBounds(393, 488, 93, 23);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				writePane.setText("");
			}
		});
		add(btnSend);

		btnClear.setBounds(496, 488, 93, 23);
		add(btnClear);

		listPop = new JPopupMenu();
		JMenuItem pChat = new JMenuItem("private chat");
		pChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PublicPanel.this.parent.addPrivatePanel(new PrivatePanel(
						PublicPanel.this.parent.getName(), PublicPanel.this.m,
						PublicPanel.this.configPanel, list.getSelectedValue()));
			}
		});
		listPop.add(pChat);
	}

	@Override
	protected void sendMessage() {
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
			if (name.length() == 0)
				continue;
			onlineModel.addElement(name);
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("enter.");
		appendSystemMessage(sb.toString());
	}

	public void removeUser(String[] user) {
		StringBuilder sb = new StringBuilder();
		for (String name : user) {
			if (name.length() == 0)
				continue;
			onlineModel.removeElement(name);
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("leave.");
		appendSystemMessage(sb.toString());
	}

}
