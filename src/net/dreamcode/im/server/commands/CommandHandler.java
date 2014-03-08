package net.dreamcode.im.server.commands;

import java.util.ArrayList;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.modules.User;

public class CommandHandler
{
	IMServer parent;
	ArrayList<Command> registeredListeners = new ArrayList<>();
	
	public CommandHandler(IMServer parent)
	{
		this.parent = parent;
	}
	
	public void addListener(Command listener)
	{
		this.registeredListeners.add(listener);
	}
	
	public void removeListener(Command listener)
	{
		this.registeredListeners.remove(listener);
	}
	
	public void handleCommand(String commandLine, User executor)
	{
		String actualCommand = commandLine.split(" ")[0];
		String[] args = new String[commandLine.split(" ").length];
		
		for (int i = 1; i < commandLine.split(" ").length; i++)
		{
			args[i - 1] = commandLine.split(" ")[i];
		}
		
		for (Command elem : this.registeredListeners)
		{
			if (actualCommand.equalsIgnoreCase(elem.getUsedCommand()))
			{
				elem.onCommand(args, executor, this.parent);
			}
		}
	}
}