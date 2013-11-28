package core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
	// for synchronize
	private Object lock = new Object();

	private int serverPort;
	private int portToUse;

	public Server(int port) {
		serverPort = port;
		portToUse = port + 1;
	}

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
		Thread listener = new Thread(new Runnable() {
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
		System.out.println(username + ": " + command); // for debug
		String prefix = command.substring(0, command.indexOf(" $"));
		command = command.substring(prefix.length() + 1);
		switch (prefix) {
		case "$logout":
			userLogout(username);
			break;
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
				sendFileReq(username, params[1], params[2],
						Long.parseLong(params[3]));
			} else {
				sendFileRes(params[1], username,
						Boolean.parseBoolean(params[2]));
			}
			break;
		default:
			// something to do?
			break;
		}
	}

	@Override
	public void remove(String username) {
		synchronized (lock) {
			clients.remove(username);
			for (ClientListener client : clients.values())
				client.send("$offline $" + username);
		}
	}

	@Override
	public boolean add(String username, ClientListener cl) {
		StringBuilder sb = null;
		synchronized (lock) {
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

	private void userLogout(String name) {
		remove(name);
		for (ClientListener cl : clients.values())
			cl.send("$offline $" + name);
	}

	private void sendPublicMessage(String speaker, String content) {
		for (ClientListener cl : clients.values())
			cl.send("$talk $public $" + speaker + " $" + content);
	}

	private void sendPrivateMessage(String speaker, String listener,
			String content) {
		clients.get(listener).send(
				"$talk $private $" + speaker + " $" + content);
	}

	private void sendFileReq(String fileSender, String fileReceiver,
			String filename, long filesize) {
		clients.get(fileReceiver)
				.send("$file $req $" + fileSender + " $" + filename + " $"
						+ filesize);
	}

	private void sendFileRes(String fileSender, String fileReceiver,
			boolean accepted) {
		// TODO
	}
}
