package net.dreamcode.im.server.commands;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.LogLevel;
import net.dreamcode.im.server.io.Message;
import net.dreamcode.im.server.modules.User;

public class CommandBroadcast implements Command
{

	@Override
	public void onCommand(String[] args, User executor, IMServer server)
	{
		String send = executor.getUserName() + ":";
		
		for (String subString : args)
		{
			send += " " + subString;
		}
		
		Message broadcastMessage = new Message(send);
		for (User elem : server.getOnlineUsers())
		{
			try
			{
				elem.getObjectOut().writeObject(broadcastMessage);
				elem.getObjectOut().flush();
			}
			catch (Exception ex)
			{
				server.log("IO-Error while trying to broadcast a message!", LogLevel.ERROR);
				server.log(ex.getMessage(), LogLevel.ERROR);
			}
		}
	}

	@Override
	public String getUsedCommand()
	{
		return "BROADCAST";
	}

}
