package core.client;


public interface ClientCallBack {

	/**
	 * handle online user list
	 * @param names a list of usernames who login
	 */
	public void online(String[] names);
	
	/**
	 * handle offline user list
	 * @param namesa list of usernames who logout
	 */
	public void offline(String[] names);
	
	/**
	 * handle public message
	 * @param from the speaker username
	 * @param content the content the speaker said
	 */
	public void talkPublic(String from, String content);
	
	/**
	 * handle private message
	 * @param from the speaker username
	 * @param content the content the speaker said
	 */
	public void talkPrivate(String from, String content);
	
	/**
	 * handle send-file request
	 * @param from the usename of who wants to send the file
	 * @param filename filename of the file to send
	 * @param filesize filesize of the file to send
	 */
	public void fileRequest(String from, String filename, long filesize);
	
	/**
	 * handle the response of send-file request
	 * @param from the username of who send the response
	 * @param accepted whether the user will receive the file
	 * @param port the port to connect if the user accept the file
	 */
	public void fileResponse(String from, boolean accepted, int port);
	
	/**
	 * handle file receive message from the server
	 * @param sender the username of who send the file
	 * @param port the port to connect
	 */
	public void fileReceive(String sender, int port); 
	
	/**
	 * handle error message
	 * @param message the error message from the server
	 */
	public void error(String message);
	
}
