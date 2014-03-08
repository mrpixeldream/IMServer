package net.dreamcode.im.server.modules;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.io.Message;

import org.jasypt.util.text.StrongTextEncryptor;

public class User
{
	private String password;
	private String userID;
	private String userName;
	
	private ObjectOutputStream objectOut;
	private ObjectInputStream objectIn;
	private Socket clientSocket;
	
	private StrongTextEncryptor encryptor;
	
	public User(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
		this.encryptor = new StrongTextEncryptor();
		
		try
		{
			this.objectIn = new ObjectInputStream(this.clientSocket.getInputStream());
			this.objectOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			
			Message loginMessage = (Message) this.objectIn.readObject();
			String[] loginSplit = loginMessage.getMessage().split(" ");
			this.encryptor.setPassword(loginSplit[2]);
			
			this.userName = loginSplit[1];
			this.password = PasswordUtil.bytesToHex(MessageDigest.getInstance("SHA-1").digest(loginSplit[2].getBytes()));
			this.userID = IMServer.getServer().getDBManager().getUserID(this.userName);
			
			loginMessage = null;
			loginSplit = null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String getPassword()
	{
		return password;
	}

	public String getUserID()
	{
		return userID;
	}

	public String getUserName()
	{
		return userName;
	}

	public ObjectOutputStream getObjectOut()
	{
		return objectOut;
	}

	public ObjectInputStream getObjectIn()
	{
		return objectIn;
	}

	public Socket getClientSocket()
	{
		return clientSocket;
	}

	public StrongTextEncryptor getEncryptor()
	{
		return encryptor;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setObjectOut(ObjectOutputStream objectOut)
	{
		this.objectOut = objectOut;
	}

	public void setObjectIn(ObjectInputStream objectIn)
	{
		this.objectIn = objectIn;
	}

	public void setClientSocket(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}

	public void setEncryptor(StrongTextEncryptor encryptor)
	{
		this.encryptor = encryptor;
	}
}