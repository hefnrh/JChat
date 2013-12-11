package com.github.jchat.server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private ExecutorService pool = Executors.newCachedThreadPool();
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
			String loginMsg = br.readLine();
			final String name = loginMsg.substring(loginMsg.indexOf(" $") + 2);
			this.name = name;
			if (name.length() <= 0) {
				send("$error $username can't be null!");
				return;
			}
			if (name.contains(" ") || name.contains("\t")) {
				send("$error $username can't contain space or tab!");
				return;
			}
			if (!ce.add(name, this)) {
				send("$error $username already in use!");
				return;
			}
			loginMsg = null;
			while (sock != null && sock.isConnected()
					&& !sock.isInputShutdown()) {
				final String msg = br.readLine();
				if (msg == null) {
					System.out.println("read null from " + name);
					throw new IOException();
				}
				System.out.println(name + ": " + msg);
				pool.execute(new Runnable() {
					@Override
					public void run() {
						ce.exec(msg, name);
					}
				});
			}
			ce.remove(name);
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
