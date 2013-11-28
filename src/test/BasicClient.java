package test;

import java.io.File;

import core.client.ClientCallBack;
import core.client.ClientCore;
import core.client.Messenger;
import core.server.Server;

/**
 * this class is used to test basic chat system functions
 * @author guyifan
 *
 */
public class BasicClient implements ClientCallBack {
	
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
		System.out.println("filereq: " + from + ": " + filename + ", size: " + filesize);
		m.fileRequestResponse(from, true);
	}

	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		System.out.println("fileres: " + from + ": " + accepted + " port: " + port);
		m.sendFile(new File("LICENSE"), port);
	}

	@Override
	public void fileReceive(String sender, int port) {
		System.out.println("file receive: " + sender + ": port:" + port);
		m.receiveFile(new File("recv.dat"), port);
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
		m.receiveFile(file, port);
	}
	public static void main(String[] args) {
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

}
