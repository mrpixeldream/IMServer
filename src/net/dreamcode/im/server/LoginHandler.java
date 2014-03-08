package net.dreamcode.im.server;

import java.net.Socket;

import net.dreamcode.im.server.io.Message;
import net.dreamcode.im.server.modules.User;

public class LoginHandler extends Thread
{
	IMServer parent;
	User targetUser;
	
	public LoginHandler(IMServer parent, Socket client)
	{
		this.parent = parent;
		this.targetUser = new User(client);
	}
	
	@Override
	public void run()
	{
		// Check user password
		if (IMServer.getServer().getDBManager().checkPassword(this.targetUser.getUserName(), this.targetUser.getPassword()))
		{
			// Check if user is already logged in
			if (!IMServer.getServer().getDBManager().isUserOnline(this.targetUser.getUserName()))
			{
				ClientHandler handler = new ClientHandler(targetUser);
				handler.start();
				IMServer.getServer().registerUser(targetUser, handler);
				IMServer.getServer().getDBManager().setOnlineStatus(this.targetUser.getUserName(), true);
				Message loginResponse = new Message("succ_login");
				try
				{
					this.targetUser.getObjectOut().writeObject(loginResponse);
					this.targetUser.getObjectOut().flush();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				IMServer.getServer().log("User " + targetUser.getUserName() + " logged in!", LogLevel.INFO);
			}
			else
			{
				try
				{
					Message errorCode = new Message("err_login_user");
					targetUser.getObjectOut().writeObject(errorCode);
					targetUser.getObjectOut().flush();
					IMServer.getServer().log("User " + targetUser.getUserName() + " is already logged in, rejecting!", LogLevel.INFO);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				Message errorCode = new Message("err_login_credentials");
				targetUser.getObjectOut().writeObject(errorCode);
				targetUser.getObjectOut().flush();
				IMServer.getServer().log("Password for user " + targetUser + " is wrong, rejecting!", LogLevel.INFO);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}