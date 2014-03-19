package net.dreamcode.im.server.commands;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.LogLevel;
import net.dreamcode.im.server.io.Message;
import net.dreamcode.im.server.modules.User;

public class CommandShow implements Command
{

	@Override
	public void onCommand(String[] args, User executor, IMServer server)
	{
		String userListStringRep = "";
		
		for (User onlineUser : server.getOnlineUsers())
		{
			userListStringRep += onlineUser.getUserID() + ";";
		}
		
		Message userList = new Message(userListStringRep);
		
		try
		{
			executor.getObjectOut().writeObject(userList);
			executor.getObjectOut().flush();
		}
		catch (Exception ex)
		{
			server.log("IO-Error while trying show online users!", LogLevel.ERROR);
			server.log(ex.getMessage(), LogLevel.ERROR);
		}
	}

	@Override
	public String getUsedCommand()
	{
		return "SHOW";
	}

}
