package net.dreamcode.im.server.commands;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.modules.User;

public interface Command
{
	public void onCommand(String[] args, User executor, IMServer server);
	public String getUsedCommand();
}