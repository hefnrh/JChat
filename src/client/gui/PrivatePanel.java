package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import client.core.Messenger;

public class PrivatePanel extends ChatPanel {
	private MainFrame parent;
	private File fileToSend = null;

	public PrivatePanel(MainFrame parent, Messenger m, ConfigPanel configPanel,
			String name) {
		super(m, configPanel, name);

		this.parent = parent;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 754, 342);
		add(scrollPane);

		scrollPane.setViewportView(readPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 754, 71);
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

		btnClear.setBounds(645, 490, 117, 25);
		add(btnClear);

		btnSend.setBounds(516, 490, 117, 25);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				writePane.setText("");
			}
		});
		add(btnSend);

		JButton btnSendFile = new JButton("send file");
		btnSendFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSendFileClicked();
			}
		});
		btnSendFile.setBounds(10, 490, 117, 25);
		add(btnSendFile);
		
		JButton btnVoiceChat = new JButton("voice chat");
		btnVoiceChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnVoiceChatclicked();
			}
		});
		btnVoiceChat.setBounds(137, 490, 117, 25);
		add(btnVoiceChat);
	}

	private void btnSendFileClicked() {
		if (fileToSend != null) {
			JOptionPane.showMessageDialog(this,
					"wait until last file trans over", "WAIT",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(this) == JFileChooser.CANCEL_OPTION)
			return;
		fileToSend = jfc.getSelectedFile();
		long filesize = fileToSend.length() >> 10;
		String filename = fileToSend.getName();
		m.fileSendRequest(name, filename, filesize);
	}
	
	private void btnVoiceChatclicked() {
		// TODO 
	}
	
	@Override
	protected void sendMessage() {
		String toSend = getWriteText();
		if (toSend == null) {
			JOptionPane.showMessageDialog(this, "can't send null message");
			return;
		}
		m.sendPrivateMessage(name, toSend);
		appendMessage(parent.getName(), toSend);
	}
	
	public File getFile() {
		return fileToSend;
	}
	public void fileSendOver() {
		fileToSend = null;
	}
}
