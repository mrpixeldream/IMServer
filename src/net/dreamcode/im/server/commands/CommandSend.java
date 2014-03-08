package net.dreamcode.im.server.commands;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.io.Message;
import net.dreamcode.im.server.modules.User;

public class CommandSend implements Command
{
	IMServer parent;
	
	public CommandSend(IMServer parent)
	{
		this.parent = parent;
	}
	
	@Override
	public void onCommand(String[] args, User executor, IMServer server)
	{
		if (args.length >= 2)
		{
			String message = "";
			String targetID = args[1];
			
			for (int i = 2; i < args.length; i++)
			{
				message += args[i];
			}
			
			User targetUser = this.parent.getUserByID(targetID);
			
			try
			{
				Message messageObj = new Message(targetUser.getEncryptor().encrypt(message));
				targetUser.getObjectOut().writeObject(messageObj);
				targetUser.getObjectOut().flush();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			try
			{
				Message errorCode = new Message(executor.getEncryptor().encrypt("cmd_send_args"));
				executor.getObjectOut().writeObject(errorCode);
				executor.getObjectOut().flush();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getUsedCommand()
	{
		return "SEND";
	}

}
