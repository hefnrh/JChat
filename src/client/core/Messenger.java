package client.core;

import java.io.File;

/**
 * Used by Chatroom Client to send message.
 * @author guyifan
 */
public interface Messenger {
	
	/**
	 * send login request to server
	 * @param username the username used to login the server
	 * @param host host name of server
	 * @param port the port to connect
	 * @return true if login successfully
	 */
	public boolean login(String username, String host, int port);
	
	/**
	 * send logout message and logout
	 */
	public void logout();
	
	/**
	 * send broadcast message
	 * @param content the message to send
	 * @return true if the message has been sent correctly
	 */
	public boolean sendPublicMessage(String content);
	
	/**
	 * send private message to someone
	 * @param messageReveiver the username of who to talk with
	 * @param content the message to send
	 * @return true if the message has been sent correctly
	 */
	public boolean sendPrivateMessage(String messageReveiver, String content);
	
	
	/**
	 * send a send-file request to someone
	 * @param fileReceiver the username of who to send file to
	 * @param filename the full name of the file to send
	 * @param filesize the size of the file to send
	 * @return true if the request has been sent correctly
	 */
	public boolean fileSendRequest(String fileReceiver, String filename, long filesize);
	
	/**
	 * respond to send-file request
	 * @param fileSender the username of sender
	 * @param accepted true if accept the file
	 * @return true if the response has been sent correctly
	 */
	public boolean fileRequestResponse(String fileSender, boolean accepted);
	
	/**
	 * send file to someone
	 * @param file the file to send
	 * @param port port to connect
	 * @return true if the file has been sent correctly
	 */
	public boolean sendFile(File file, int port);
	
	/**
	 * receive file
	 * @param file the file to receive
	 * @param port port to connect
	 * @return true if the file has been downloaded correctly
	 */
	public boolean receiveFile(File file, int port);
}
