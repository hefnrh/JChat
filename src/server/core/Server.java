package server.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * chat system server. repost client messages and file data.
 * 
 * @author guyifan
 * 
 */
public class Server implements CommandExecutor {

	private Pattern p = Pattern.compile("[$\\s]+");
	private Map<String, ClientListener> clients = new HashMap<>();
	private Thread listener;
	// for synchronize
	private Object listLock = new Object();
	private Object portLock = new Object();

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
		if (command.length() < 5) {
			System.out.println("invalid command");
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
		default:
			// something to do?
			break;
		}
	}

	@Override
	public void remove(String username) {
		synchronized (listLock) {
			ClientListener cl = clients.remove(username);
			if (cl != null)
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
		}
		clients.get(listener).send(
				"$talk $private $" + speaker + " $" + content);
	}

	private void sendFileReq(String fileSender, String fileReceiver,
			String filename, long filesize) {
		if (!clients.containsKey(fileReceiver)) {
			clients.get(fileSender).send(
					"$error $no user called " + fileReceiver);
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
		} else {
			ServerSocket srcServer = null, dstServer = null;
			int srcPort, dstPort;
				synchronized (portLock) {
					while (true) {
						try {
							srcServer = new ServerSocket();
							dstServer = new ServerSocket();
							srcPort = portGenerate();
							dstPort = portGenerate();
							while (dstPort == srcPort)
								dstPort = portGenerate();
							srcServer.setReuseAddress(true);
							dstServer.setReuseAddress(true);
							srcServer.bind(new InetSocketAddress(srcPort));
							dstServer.bind(new InetSocketAddress(dstPort));
						} catch (BindException be) {
							continue;
						} catch (IOException e) {
							e.printStackTrace();
							clients.get(fileSender).send("$error $server error in file transmission");
							clients.get(fileReceiver).send("$error $server error in file transmission");
							return;
						} 
						break;
					}
					clients.get(fileSender).send(
							"$file $res $" + fileReceiver + " $true $"
									+ srcPort);
					clients.get(fileReceiver).send(
							"$file $" + fileSender + " $" + dstPort);
				}
				exchangeFile(srcServer, dstServer);
				try {
					srcServer.close();
					dstServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
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
	 * listen to two port and transform file from one client to another
	 * 
	 * @param srcServer
	 *            listen to sender connection
	 * @param dstServer
	 *            listen to receiver connection
	 */
	private void exchangeFile(ServerSocket srcServer, ServerSocket dstServer) {
		try {
			Socket src = srcServer.accept();
			Socket dst = dstServer.accept();
			try (InputStream is = src.getInputStream();
					OutputStream os = dst.getOutputStream();) {
				byte[] buf = new byte[8192];
				int read;
				while ((read = is.read(buf)) >= 0) {
					os.write(buf, 0, read);
				}
			}
			src.close();
			dst.close();
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
