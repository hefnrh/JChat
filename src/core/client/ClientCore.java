package core.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * ClientCore has the core functions of the chat system client. It listens
 * messages from server.
 * <p>
 * This class implements <code>Messenger</code> to send messages get from GUI or
 * other source. It uses <code>ClientCallBack</code> to handle messages from
 * server.
 * 
 * @author guyifan
 * 
 */
public class ClientCore implements Messenger {

	private ClientCallBack clientCallBack;
	private Socket sock = null;
	private PrintWriter pw = null;
	private Thread listener = null;

	private Pattern p = Pattern.compile("[$\\s]+");

	public ClientCore(ClientCallBack ccb) {
		clientCallBack = ccb;
	}

	@Override
	public boolean login(String username, String host, int port) {
		if (sock != null && sock.isConnected()) {
			clientCallBack.error("you have already loged in!");
			return false;
		}
		// connect
		try {
			sock = new Socket();
			sock.connect(new InetSocketAddress(host, port), 5000);
		} catch (UnknownHostException e) {
			clientCallBack.error("unknown host!");
			return false;
		} catch (IOException e) {
			clientCallBack.error(e.getMessage());
			return false;
		}
		// login
		if (send("$login $" + username)) {
			initListener();
			return true;
		} else
			return false;

	}

	@Override
	public void logout() {
		send("$logout");
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sock = null;
	}

	@Override
	public boolean sendPublicMessage(String content) {
		return send("$talk $public $" + content);
	}

	@Override
	public boolean sendPrivateMessage(String messageReveiver, String content) {
		return send("$talk $private $" + messageReveiver + " $" + content);
	}

	@Override
	public boolean fileSendRequest(String fileReceiver, String filename,
			long filesize) {
		return send("$file $req $" + fileReceiver + " $" + filename + " $"
				+ filesize);
	}

	@Override
	public boolean fileRequestResponse(String fileSender, boolean accepted) {
		return send("$file $res $" + fileSender + " $" + accepted);
	}

	@Override
	public boolean sendFile(File file, int port) {
		Socket sendSock = new Socket();
		// connect
		try {
			sendSock.connect(new InetSocketAddress(sock.getInetAddress()
					.getHostAddress(), port), 5000);
		} catch (IOException e) {
			clientCallBack.error(e.getMessage());
			try {
				sendSock.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			return false;
		}
		// read from file and write to the socket
		try (InputStream is = new FileInputStream(file);
				OutputStream os = sendSock.getOutputStream();) {
			byte[] buf = new byte[8192];
			int read;
			while ((read = is.read(buf)) >= 0) {
				os.write(buf, 0, read);
			}
		} catch (IOException e) {
			clientCallBack.error(e.getMessage());
			try {
				sendSock.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			return false;
		}
		
		try {
			sendSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
		
	}

	@Override
	public boolean receiveFile(File file, int port) {
		Socket recvSock = new Socket();
		// connect
		try {
			recvSock.connect(new InetSocketAddress(sock.getInetAddress()
					.getHostAddress(), port), 5000);
		} catch (IOException e) {
			clientCallBack.error(e.getMessage());
			try {
				recvSock.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			return false;
		}
		// read from socket and write to file system
		try (OutputStream os = new FileOutputStream(file);
				InputStream is = recvSock.getInputStream();) {
			byte[] buf = new byte[8192];
			int read;
			while ((read = is.read(buf)) >= 0) {
				os.write(buf, 0, read);
			}
		} catch (IOException e) {
			clientCallBack.error(e.getMessage());
			try {
				recvSock.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			return false;
		}
		
		try {
			recvSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
		
	}

	/**
	 * send content to server
	 * 
	 * @param content
	 *            the content to send
	 * @return true if the content has been sent correctly
	 */
	private boolean send(String content) {
		if (sock == null || sock.isClosed() || sock.isOutputShutdown())
			return false;
		if (pw == null) {
			try {
				pw = new PrintWriter(sock.getOutputStream(), true);
			} catch (IOException e) {
				clientCallBack.error(e.getMessage());
				return false;
			}
		}
		pw.println(content);
		return true;
	}

	/**
	 * start listening to server command
	 */
	private void initListener() {
		if (listener != null)
			return;
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));) {
					String msg;
					while (sock != null && sock.isConnected()
							&& !sock.isInputShutdown()) {
						msg = br.readLine();
						if (msg == null)
							throw new IOException("connection is down!");
						exec(msg);
					}
				} catch (IOException e) {
					clientCallBack.error(e.getMessage());
				}
				listener = null;
			}
		});
		listener.setDaemon(true);
		listener.start();
	}

	/**
	 * analyze and execute command from server
	 * 
	 * @param command
	 *            the raw command from server
	 */
	private void exec(String command) {
		String prefix = command.substring(0, command.indexOf(" $"));
		command = command.substring(prefix.length() + 1);
		switch (prefix) {
		case "$online":
			clientCallBack.online(p.split(command));
			break;
		case "$offline":
			clientCallBack.offline(p.split(command));
			break;
		case "$talk":
			boolean pubtalk;
			if (command.startsWith("$public")) {
				command = command.substring(9);
				pubtalk = true;
			} else {
				command = command.substring(10);
				pubtalk = false;
			}
			String name = command.substring(0, command.indexOf(" $"));
			command = command.substring(name.length() + 2);
			if (pubtalk)
				clientCallBack.talkPublic(name, command);
			else
				clientCallBack.talkPrivate(name, command);
			break;
		case "$file":
			String[] params = p.split(command);
			if (command.startsWith("$req")) {
				clientCallBack.fileRequest(params[2], params[3],
						Long.parseLong(params[4]));
			} else if (command.startsWith("$res")) {
				clientCallBack.fileResponse(params[2],
						Boolean.parseBoolean(params[3]),
						Integer.parseInt(params[4]));
			} else {
				clientCallBack.fileReceive(params[1],
						Integer.parseInt(params[2]));
			}
			break;
		case "$error":
			clientCallBack.error(command.substring(1));
			break;
		default:
			// something to do?
			break;
		}
	}

}
