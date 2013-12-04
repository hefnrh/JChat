package client.core;


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
	
	/**
	 * used to notify the progress of the transmission
	 * @param complete bytes that have been transmitted
	 * @param all size of the file
	 */
	public void setSendProgress(long complete, long all);
	
	/**
	 * used to notify the progress of the transmission
	 * @param complete bytes that have been transmitted
	 * @param all size of the file
	 */
	public void setRecvProgress(long complete, long all);
	
	/**
	 * handle voice chat request
	 * @param speaker name of speaker
	 */
	public void voiceRequest(String speaker);
	
	/**
	 * the response of voice chat request
	 * @param listener name of listener
	 * @param accepted whether accept the request
	 * @param outPort port to send voice data
	 * @param inPort port to receive voice data
	 */
	public void voiceResponse(String listener, boolean accepted, int outPort, int inPort);
	
	/**
	 * handle voice receive message from the server
	 * @param speaker name of speaker
	 * @param outPort port to send voice data
	 * @param inPort port to receive voice data
	 */
	public void voiceRecv(String speaker, int outPort, int inPort);
}
