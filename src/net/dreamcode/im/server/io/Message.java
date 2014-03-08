package net.dreamcode.im.server.io;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	String message;
	
	public Message(String message)
	{
		this.message = message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
