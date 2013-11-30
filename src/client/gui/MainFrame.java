package client.gui;

import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import client.core.ClientCallBack;
import client.core.Messenger;

public class MainFrame extends JFrame implements ClientCallBack {
	private JTabbedPane tabbedPane;
	private PublicPanel publicPanel;
	private Map<String, PrivatePanel> privatePanel;
	private Messenger m;
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(null);
		setSize(800, 600);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 10, 763, 541);
		publicPanel = new PublicPanel(m);
		getContentPane().add(tabbedPane);
	}
	
	public void setMessenger(Messenger m) {
		this.m = m;
	}
	@Override
	public void online(String[] names) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void offline(String[] names) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void talkPublic(String from, String content) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void talkPrivate(String from, String content) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fileRequest(String from, String filename, long filesize) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fileReceive(String sender, int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void error(String message) {
		// TODO Auto-generated method stub
		
	}

}
