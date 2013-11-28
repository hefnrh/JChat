package core.server;

public interface CommandExecutor {
	
	/**
	 * analyze and repost client commands
	 * @param command command from client
	 */
	public void exec(String command, String username);
	
	public boolean add(String username, ClientListener cl);
	public void remove(String username);
}
