package net.dreamcode.im.server.io;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	String message;
	Date timestamp;
	
	public Message(String message)
	{
		this.message = message;
		this.timestamp = new Date(System.currentTimeMillis());
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
