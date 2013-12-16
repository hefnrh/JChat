package com.github.jchat.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * chat system server. repost client messages and file data.
 * 
 * @author guyifan
 * 
 */
public class Server implements CommandExecutor {

	private Pattern p = Pattern.compile("^[$]|[ ]+[$]");
	private Map<String, ClientListener> clients = new HashMap<>();
	private Thread listener;
	// for synchronize
	private Object listLock = new Object();
	private ExecutorService pool = Executors.newCachedThreadPool();

	private int serverPort;
	private int portToUse;

	public Server(int port) {
		serverPort = port;
		portToUse = port + 1;
	}

	/**
	 * open a socket listen to the default port
	 * 
	 * @return true if server open successfully
	 */
	public boolean start() {
		return start(serverPort);
	}

	/**
	 * open a socket listen to a port
	 * 
	 * @param port
	 *            the port to listen
	 * @return true if server open successfully
	 */
	public boolean start(int port) {
		serverPort = port;
		portToUse = port + 1;
		try {
			ServerSocket server = new ServerSocket();
			server.bind(new InetSocketAddress(port));
			server.setReuseAddress(true);
			initListenThread(server);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void initListenThread(final ServerSocket server) {
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Socket sock = server.accept();
						new ClientListener(sock, Server.this).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		listener.setDaemon(true);
		listener.start();
	}

	@Override
	public void exec(String command, String username) {
		if (command.length() < 4) {
			System.out.println("invalid command: \"" + command + "\"");
			return;
		}
		if (command.startsWith("$logout")) {
			remove(username);
			return;
		}
		String prefix = command.substring(0, command.indexOf(" $"));
		command = command.substring(prefix.length() + 1);
		switch (prefix) {
		case "$talk":
			if (command.startsWith("$public")) {
				sendPublicMessage(username, command.substring(9));
			} else {
				command = command.substring(10);
				String listener = command.substring(0, command.indexOf(" $"));
				sendPrivateMessage(username, listener,
						command.substring(listener.length() + 2));
			}
			break;
		case "$file":
			String[] params = p.split(command);
			if (command.startsWith("$req")) {
				sendFileReq(username, params[2], params[3],
						Long.parseLong(params[4]));
			} else {
				sendFileRes(params[2], username,
						Boolean.parseBoolean(params[3]));
			}
			break;
		case "$voice":
			params = p.split(command);
			if (command.startsWith("$req")) {
				sendVoiceReq(username, params[2]);
			} else {
				sendVoiceRes(username, params[2],
						Boolean.parseBoolean(params[3]));
			}
			break;
		default:
			// something to do?
			break;
		}
	}

	private void sendVoiceReq(String speaker, String listener) {
		if (!clients.containsKey(listener)) {
			clients.get(speaker).send("no user called " + listener);
			return;
		}
		clients.get(listener).send("$voice $req $" + speaker);
	}

	private ServerSocket generateServerSocket() {
		ServerSocket ret = null;
		while (true) {
			try {
				ret = new ServerSocket();
				ret.setReuseAddress(true);
				ret.bind(new InetSocketAddress(portGenerate()));
			} catch (BindException be) {
				try {
					ret.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			break;
		}
		return ret;
	}

	private void sendVoiceRes(String listener, String speaker, boolean accepted) {
		if (!accepted) {
			clients.get(speaker).send(
					"$voice $res $" + listener + " $false $-1 $-1");
			return;
		}
		// generate 4 server socket
		final ServerSocket src1 = generateServerSocket();
		if (src1 == null) {
			clients.get(listener).send(
					"$error $server error in voice transmission");
			clients.get(speaker).send(
					"$error $server error in voice transmission");
			return;
		}
		final ServerSocket src2 = generateServerSocket();
		if (src2 == null) {
			try {
				src1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clients.get(listener).send(
					"$error $server error in voice transmission");
			clients.get(speaker).send(
					"$error $server error in voice transmission");
			return;
		}
		final ServerSocket dst1 = generateServerSocket();
		if (dst1 == null) {
			try {
				src1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				src2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clients.get(listener).send(
					"$error $server error in voice transmission");
			clients.get(speaker).send(
					"$error $server error in voice transmission");
			return;
		}
		final ServerSocket dst2 = generateServerSocket();
		if (dst2 == null) {
			try {
				src1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				src2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dst1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clients.get(listener).send(
					"$error $server error in voice transmission");
			clients.get(speaker).send(
					"$error $server error in voice transmission");
			return;
		}
		clients.get(speaker).send(
				"$voice $res $" + listener + " $true $" + src1.getLocalPort()
						+ " $" + dst2.getLocalPort());
		clients.get(listener).send(
				"$voice $send $" + speaker + " $" + src2.getLocalPort() + " $"
						+ dst1.getLocalPort());
		pool.execute(new Runnable() {
			@Override
			public void run() {
				exchangeData(src1, dst1);
			}
		});
		pool.execute(new Runnable() {
			@Override
			public void run() {
				exchangeData(src2, dst2);
			}
		});
	}

	@Override
	public void remove(String username) {
		synchronized (listLock) {
			ClientListener cl = clients.remove(username);
			if (cl == null)
				return;
			cl.close();
			for (ClientListener client : clients.values())
				client.send("$offline $" + username);
		}
	}

	@Override
	public boolean add(String username, ClientListener cl) {
		StringBuilder sb = null;
		synchronized (listLock) {
			if (clients.containsKey(username))
				return false;
			for (ClientListener client : clients.values())
				client.send("$online $" + username);
			sb = new StringBuilder("$online");
			clients.put(username, cl);
			for (String user : clients.keySet())
				sb.append(" $" + user);
		}
		cl.send(sb.toString());
		return true;
	}

	private void sendPublicMessage(String speaker, String content) {
		for (ClientListener cl : clients.values())
			cl.send("$talk $public $" + speaker + " $" + content);
	}

	private void sendPrivateMessage(String speaker, String listener,
			String content) {
		if (!clients.containsKey(listener)) {
			clients.get(speaker).send("$error $no user called " + listener);
			return;
		}
		clients.get(listener).send(
				"$talk $private $" + speaker + " $" + content);
	}

	private void sendFileReq(String fileSender, String fileReceiver,
			String filename, long filesize) {
		if (!clients.containsKey(fileReceiver)) {
			clients.get(fileSender).send(
					"$error $no user called " + fileReceiver);
			return;
		}
		clients.get(fileReceiver)
				.send("$file $req $" + fileSender + " $" + filename + " $"
						+ filesize);
	}

	/**
	 * handle response to file transform request. repost message if not
	 * accepted. call <code>exchangeFile</code> to handle data transform.
	 * 
	 * @param fileSender
	 *            username of fileSender
	 * @param fileReceiver
	 *            username of fileReceiver
	 * @param accepted
	 *            whether fileReceiver want to receive the file
	 */
	private void sendFileRes(String fileSender, String fileReceiver,
			boolean accepted) {
		if (!accepted) {
			clients.get(fileSender).send(
					"$file $res $" + fileReceiver + " $false $-1");
			return;
		}
		ServerSocket srcServer = generateServerSocket();
		if (srcServer == null) {
			clients.get(fileReceiver).send(
					"$error $server error in file transmission");
			clients.get(fileSender).send(
					"$error $server error in file transmission");
			return;
		}
		ServerSocket dstServer = generateServerSocket();
		if (dstServer == null) {
			try {
				srcServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clients.get(fileReceiver).send(
					"$error $server error in file transmission");
			clients.get(fileSender).send(
					"$error $server error in file transmission");
			return;
		}

		clients.get(fileSender).send(
				"$file $res $" + fileReceiver + " $true $"
						+ srcServer.getLocalPort());
		clients.get(fileReceiver).send(
				"$file $send $" + fileSender + " $" + dstServer.getLocalPort());
		exchangeData(srcServer, dstServer);
	}

	/**
	 * random generate a new port to use
	 * 
	 * @return new value of portToUse
	 */
	private int portGenerate() {
		portToUse = new java.util.Random().nextInt(65535 - serverPort);
		portToUse += serverPort + 1;
		return portToUse;
	}

	/**
	 * listen to two port and transform data from one client to another
	 * 
	 * @param srcServer
	 *            listen to sender connection
	 * @param dstServer
	 *            listen to receiver connection
	 */
	private void exchangeData(ServerSocket srcServer, ServerSocket dstServer) {

		try (Socket src = srcServer.accept();
				Socket dst = dstServer.accept();
				InputStream is = src.getInputStream();
				OutputStream os = dst.getOutputStream();) {
			try {
				srcServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dstServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] buf = new byte[4096];
			int read;
			while ((read = is.read(buf)) >= 0) {
				os.write(buf, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * wait for the server socket to die naturally
	 */
	public void waitFor() {
		try {
			listener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
