package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;

import client.core.Messenger;

public class VoiceDialog extends JDialog {
	private Messenger m;
	public VoiceDialog(Messenger m, String name) {
		this.m = m;
		setSize(201, 100);
		setTitle("voice chat with " + name);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				stopChat();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				stopChat();
			}
		});
		
		JButton btnStopChat = new JButton("stop chat");
		btnStopChat.setBounds(41, 22, 117, 25);
		btnStopChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopChat();
			}
		});
		getContentPane().add(btnStopChat);
		
	}
	
	private void stopChat() {
		m.stopVoiceChat();
	}
}
