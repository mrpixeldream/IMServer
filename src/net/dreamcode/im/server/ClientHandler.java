package net.dreamcode.im.server;

import net.dreamcode.im.server.io.Message;
import net.dreamcode.im.server.modules.User;

public class ClientHandler extends Thread
{
	private final Message LOGOUT = new Message("LOGOUT");
	private final Message IDLE = new Message("IDLE");
	
	User userToHandle;
	
	public ClientHandler(User user)
	{
		this.userToHandle = user;
	}
	
	@Override
	public void run()
	{
		Message incomingMessage = IDLE;
		Message response = null;
		
		while (incomingMessage != LOGOUT)
		{
			try
			{
				incomingMessage = (Message) this.userToHandle.getObjectIn().readObject();
				System.out.println(incomingMessage.getMessage());
				response = new Message(incomingMessage.getMessage());
				this.userToHandle.getObjectOut().writeObject(response);
				this.userToHandle.getObjectOut().flush();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		try
		{
			IMServer.getServer().unregisterUser(userToHandle, this);
			IMServer.getServer().getDBManager().setOnlineStatus(userToHandle.getUserName(), false);
			this.userToHandle.getClientSocket().close();
			this.userToHandle = null;
			IMServer.getServer().log("User " + userToHandle.getUserName() + " logged out!", LogLevel.INFO);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}