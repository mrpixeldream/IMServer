package net.dreamcode.im.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.dreamcode.im.server.IMServer;
import net.dreamcode.im.server.LogLevel;

public class DBManager
{

	// The connection driver for the database
	SQLConnect	dbDriver	= new SQLConnect("db4free.net", "dreamcode", "Gummiball13", "dreamcode", 3306);

	IMServer	parent;

	public DBManager(IMServer parent)
	{
		this.parent = parent;
	}

	public boolean checkPassword(String user, String password)
	{
		String queryString = QueryLib.GET_USER_PW.replace("?", getMysqlRealScapeString(user));

		ResultSet result = dbDriver.query(queryString);

		try
		{
			result.next();

			return password.equals(result.getString("password"));
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public boolean isAdmin(String user)
	{
		String queryString = QueryLib.GET_USER_ADMIN.replace("?", getMysqlRealScapeString(user));

		ResultSet result = dbDriver.query(queryString);

		try
		{
			if (result != null && result.next())
			{
				return result.getBoolean("admin");
			}

			this.parent.log("User not found! Something went wrong!", LogLevel.ERROR);
			return false;
		}
		catch (SQLException e)
		{
			this.parent.log("User not found! Something went wrong!", LogLevel.ERROR);
			return false;
		}
	}

	public boolean isUserOnline(String name)
	{
		String queryString = QueryLib.GET_USER_ONLINE.replace("?", getMysqlRealScapeString(name));
		ResultSet result = dbDriver.query(queryString);

		try
		{
			result.next();
			boolean onlineStatus = result.getBoolean("online");
			return onlineStatus;
		}
		catch (SQLException e)
		{
			this.parent.log("Unexpected error occured while querying database: \n" + e.getMessage(), LogLevel.ERROR);
			return true;
		}
	}

	public void setOnlineStatus(String name, boolean online)
	{
		String targetStatus = "";
		if (online)
		{
			targetStatus += 1;
		}
		else
		{
			targetStatus += 0;
		}

		String queryString = QueryLib.SET_USER_LOGIN_STATUS.replace("?", getMysqlRealScapeString(targetStatus)).replace("2", getMysqlRealScapeString(name));

		dbDriver.query(queryString);

		this.parent.log("Successfully updated online status of user " + name, LogLevel.INFO);
	}
	
	public String getUserID(String username)
	{
		String queryString = QueryLib.GET_USER_ID.replace("?", username);
		
		ResultSet result = dbDriver.query(queryString);
		
		try
		{
			result.next();
			return result.getString("id");
		}
		catch (Exception ex)		
		{
			ex.printStackTrace();
		}
		
		return null;
	}

	private String getMysqlRealScapeString(String str)
	{
		String data = null;
		if (str != null && str.length() > 0)
		{
			str = str.replace("\\", "\\\\");
			str = str.replace("'", "\\'");
			str = str.replace("\0", "\\0");
			str = str.replace("\n", "\\n");
			str = str.replace("\r", "\\r");
			str = str.replace("\"", "\\\"");
			str = str.replace("\\x1a", "\\Z");
			data = str;
		}
		return data;
	}

	public void shutdown()
	{
		dbDriver.disconnect();
		// parent = null;
	}
}