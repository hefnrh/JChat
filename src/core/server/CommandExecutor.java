package core.server;

public interface CommandExecutor {

	/**
	 * analyze and repost client commands
	 * 
	 * @param command
	 *            command from client
	 */
	public void exec(String command, String username);

	/**
	 * add a new user to client list in the server
	 * 
	 * @param username
	 *            username of the new comer
	 * @param cl
	 *            listener listening to the client commend
	 * @return true if add user to client list successfully
	 */
	public boolean add(String username, ClientListener cl);

	/**
	 * remove a user from client list in the server
	 * 
	 * @param username
	 *            username of the user to be removed
	 */
	public void remove(String username);
}
