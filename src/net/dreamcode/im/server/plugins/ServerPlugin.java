package net.dreamcode.im.server.plugins;

import net.dreamcode.im.server.commands.Command;

public interface ServerPlugin extends Command
{
	public void onLoad();
	public void onUnload();
}