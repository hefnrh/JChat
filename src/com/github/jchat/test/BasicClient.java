package com.github.jchat.test;

import java.io.File;

import com.github.jchat.client.core.ClientCallBack;
import com.github.jchat.client.core.ClientCore;
import com.github.jchat.client.core.Messenger;
import com.github.jchat.client.gui.Startup;
import com.github.jchat.server.core.Server;

/**
 * this class is used to test basic chat system functions
 * 
 * @author guyifan
 * 
 */
public class BasicClient implements ClientCallBack {

	// for debug
	private static boolean debug = false;

	public static void usage() {
		System.out.println("usage: <client|server> <port>");
	}
	
	public static void main(String[] args) {
		if (!debug) {
			if (args.length < 1 || (!args[0].startsWith("server") && !args[0].equals("-h")))
				new Startup().setVisible(true);
			else if (args[0].equals("-h")) {
				usage();
			} else {
				int port = 12700;
				if (args.length >= 2) {
					try {
						port = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						System.out.println("invalid port. set to default(12700).");
					}
				}
				Server server = new Server(port);
				server.start();
				System.out.println("server start on port " + port);
				server.waitFor();
			}
		} else {
			testClientUI();
		}
	}
	
	public static void testClientUI() {
		Server server = new Server(12700);
		server.start();
		Startup start = new Startup();
		start.setVisible(true);
		server.waitFor();
	}
	public static void testCoreLogic() {
		Server server = new Server(12700);
		server.start();
		BasicClient bc1 = new BasicClient();
		bc1.setMessenger(new ClientCore(bc1));
		BasicClient bc2 = new BasicClient();
		bc2.setMessenger(new ClientCore(bc2));
		bc1.login("John");
		bc2.login("Bob");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bc1.fileReq("Bob");
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.waitFor();
	}

	public static void testStartup() {
		Startup start = new Startup();
		start.setVisible(true);
	}

	private Messenger m = null;

	public void setMessenger(Messenger m) {
		this.m = m;
	}

	@Override
	public void online(String[] names) {
		StringBuilder sb = new StringBuilder("online:");
		for (String s : names)
			sb.append(" " + s);
		System.out.println(sb);
	}

	@Override
	public void offline(String[] names) {
		StringBuilder sb = new StringBuilder("offline:");
		for (String s : names)
			sb.append(" " + s);
		System.out.println(sb);
	}

	@Override
	public void talkPublic(String from, String content) {
		System.out.println("public: " + from + ": " + content);
	}

	@Override
	public void talkPrivate(String from, String content) {
		System.out.println("private: " + from + ": " + content);
	}

	@Override
	public void fileRequest(String from, String filename, long filesize) {
		System.out.println("filereq: " + from + ": " + filename + ", size: "
				+ filesize);
		m.fileRequestResponse(from, true);
	}

	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		System.out.println("fileres: " + from + ": " + accepted + " port: "
				+ port);
		m.sendFile(new File("LICENSE"), port);
	}

	@Override
	public void fileReceive(String sender, int port) {
		System.out.println("file receive: " + sender + ": port:" + port);
		m.receiveFile(new File("recv.dat"), port, 0);
	}

	@Override
	public void error(String message) {
		System.out.println(message);
	}

	public void login(String name) {
		m.login(name, "127.0.0.1", 12700);
	}

	public void sendPub() {
		m.sendPublicMessage("send public test");
	}

	public void sendPri(String name) {
		m.sendPrivateMessage(name, "send private test");
	}

	public void logout() {
		m.logout();
	}

	public void fileReq(String name) {
		m.fileSendRequest(name, "test.zip", 123456);
	}

	public void sendFile(File file, int port) {
		m.sendFile(file, port);
	}

	public void recvFile(File file, int port) {
		m.receiveFile(file, port, 0);
	}

	@Override
	public void setSendProgress(long complete, long all) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRecvProgress(long complete, long all) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceRequest(String speaker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceResponse(String listener, boolean accepted, int outPort,
			int inPort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceRecv(String speaker, int outPort, int inPort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceOver() {
		// TODO Auto-generated method stub
		
	}

}
