package core.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This client listen to client message and perform basic message sending
 * function.
 * 
 * @author guyifan
 * 
 */
public class ClientListener extends Thread {

	private Socket sock;
	private PrintWriter pw = null;
	private CommandExecutor ce;
	private String name;

	public ClientListener(Socket socket, CommandExecutor ce) {
		sock = socket;
		this.ce = ce;
		setDaemon(true);
	}

	/**
	 * send message without any process
	 * 
	 * @param content
	 *            the message to send
	 * @return true if the content has been sent correctly
	 */
	public boolean send(String content) {
		if (sock == null || sock.isClosed() || sock.isOutputShutdown())
			return false;
		if (pw == null) {
			try {
				pw = new PrintWriter(sock.getOutputStream(), true);
			} catch (IOException e) {
				ce.remove(name);
				return false;
			}
		}
		pw.println(content);
		return true;
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));) {
			// first message is login command
			String msg = br.readLine();
			String name = msg.substring(msg.indexOf(" $") + 2);
			this.name = name;
			ce.add(name, this);
			while (sock != null && sock.isConnected()
					&& !sock.isInputShutdown()) {
				msg = br.readLine();
				if (msg == null) {
					System.out.println("read null from " + name);
					throw new IOException();
				}
				ce.exec(msg, name);
			}
		} catch (IOException e) {
			ce.remove(name);
		}
	}

	public void close() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
