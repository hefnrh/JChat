package com.github.jchat.client.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.github.jchat.client.core.Messenger;

public class PublicPanel extends ChatPanel {
	private JList<User> list;
	private DefaultListModel<User> onlineModel;
	private JPopupMenu listPop;
	private JCheckBoxMenuItem pBlack;
	private MainFrame parent;
	private List<String> blackList = new ArrayList<>();

	public PublicPanel(MainFrame parent, Messenger m, ConfigPanel configPanel,
			String name) {
		super(m, configPanel, name);
		this.parent = parent;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 579, 342);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		add(scrollPane);

		scrollPane.setViewportView(readPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 579, 71);
		scrollPane_1.setOpaque(false);
		scrollPane_1.getViewport().setOpaque(false);
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
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		add(scrollPane_2);

		onlineModel = new DefaultListModel<>();
		list = new JList<>();
		list.setModel(onlineModel);
		list.setOpaque(false);
		list.setCellRenderer(new ListRenderer());
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(new Point(e.getX(), e.getY()));
				list.setSelectedIndex(index);
				if (e.getButton() == MouseEvent.BUTTON3) {
					User u = list.getSelectedValue();
					if (u.isBlack())
						pBlack.setSelected(true);
					else
						pBlack.setSelected(false);
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
						PublicPanel.this.configPanel, list.getSelectedValue().name));
			}
		});
		listPop.add(pChat);
		pBlack = new JCheckBoxMenuItem("black list");
		pBlack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User u = list.getSelectedValue();
				if (u.isBlack()) {
					blackList.remove(u.name);
					u.setBlack(false);
				} else {
					blackList.add(u.name);
					u.setBlack(true);
				}
			}
		});
		listPop.add(pBlack);
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
		StringBuilder sb = new StringBuilder();
		for (String name : user) {
			if (name.length() == 0)
				continue;
			addSingleUser(name);
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("enter.");
		appendSystemMessage(sb.toString());
	}

	private void addSingleUser(String user) {
		User u = new User(user);
		int index = 0;
		for (int j = onlineModel.getSize(); index < j; ++index) {
			if (onlineModel.elementAt(index).name.compareTo(user) > 0)
				break;
		}
		onlineModel.add(index, u);
	}
	public void removeUser(String[] user) {
		StringBuilder sb = new StringBuilder();
		for (String name : user) {
			if (name.length() == 0)
				continue;
			onlineModel.removeElement(new User(name));
			sb.append(name);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("leave.");
		appendSystemMessage(sb.toString());
	}
	
	public static class User {
		public final String name;
		private boolean black = false;
		public User(String name) {
			this.name = name;
		}
		public boolean isBlack() {
			return black;
		}
		public void setBlack(boolean b) {
			black = b;
		}
		@Override
		public String toString() {
			return name;
		}
		@Override
		public boolean equals(Object o) {
			boolean res = false;
			if (o instanceof User) {
				res = ((User) o).name.equals(name);
			}
			return name.equals(o) || res;
		}
		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	public boolean isBlack(String name) {
		return blackList.contains(name);
	}
	
}
