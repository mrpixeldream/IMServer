package net.dreamcode.im.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import net.dreamcode.im.server.commands.CommandBroadcast;
import net.dreamcode.im.server.commands.CommandHandler;
import net.dreamcode.im.server.commands.CommandSend;
import net.dreamcode.im.server.commands.CommandShow;
import net.dreamcode.im.server.db.DBManager;
import net.dreamcode.im.server.modules.User;
import net.dreamcode.im.server.plugins.PluginManager;
import net.dreamcode.im.server.plugins.ServerPlugin;

public class IMServer
{
	static IMServer					serverInstance;
	
	int								port;

	FileWriter						logger;

	DBManager						dbManager		= new DBManager(this);

	HashMap<String, User>			users;
	ArrayList<ClientHandler>		clientHandlers;

	CommandHandler					commandHandler = new CommandHandler(this);
	ArrayList<ServerPlugin>			plugins;

	PluginManager					pluginManager;

	Random							idGenerator;

	ServerSocket					server;

	boolean							logEnabled		= false;
	boolean							isRunning		= true;

	public static void main(String[] args)
	{
		// Setting standard port
		int port = 22558;

		// Check whether a custom port is given or not
		if (args.length == 1 && !args[0].isEmpty())
		{
			try
			{
				// Set the port to the custom port
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException ex)
			{
				System.err.println("An error occured while parsing port number, must be a number");
			}
		}

		// Create server instance and start the server
		IMServer server = new IMServer(port);
		IMServer.serverInstance = server;
		server.startServer();
	}

	public IMServer(int port)
	{
		this.port = port;
		return;
	}

	public void startServer()
	{
		if (this.port != 0)
		{
			// Initialization code
			File logfile = new File("chatbase.log");

			try
			{
				this.logger = new FileWriter(logfile, true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			this.logEnabled = true;

			log("Setting up variables...", LogLevel.INFO);
			users = new HashMap<>();
			clientHandlers = new ArrayList<>();
			pluginManager = new PluginManager(this);

			log("Creating socket...", LogLevel.INFO);
			try
			{
				log("Adding default commands...", LogLevel.INFO);
				CommandSend cmdSend = new CommandSend();
				CommandBroadcast cmdBroadcast = new CommandBroadcast();
				CommandShow cmdShow = new CommandShow();
				
				commandHandler.addListener(cmdSend);
				commandHandler.addListener(cmdBroadcast);
				commandHandler.addListener(cmdShow);

				log("Loading custom features...", LogLevel.INFO);

				this.plugins = this.pluginManager.loadPlugins();

				for (ServerPlugin plugin : this.plugins)
				{
					commandHandler.addListener(plugin);
				}

				server = new ServerSocket(port);

				log("Server now listening for new connections...", LogLevel.INFO);

				while (isRunning)
				{
					Socket client;
					client = server.accept();
					log("Connected from " + client.getInetAddress(), LogLevel.INFO);
					LoginHandler loginHandler = new LoginHandler(this, client);
					loginHandler.start();
				}

				log("Received shutdown signal. Bye!", LogLevel.INFO);
			}
			catch (IOException e)
			{
				log("Got error while creating socket:", LogLevel.ERROR);
				log(e.getMessage(), LogLevel.ERROR);
			}
		}
	}

	public void log(String message, LogLevel level)
	{
		if (this.logEnabled)
		{
			try
			{
				if (level == LogLevel.INFO)
				{
					System.out.println("INFO: " + message);
					logger.append("[INFO] " + this.datePrefix() + message + "\n");
				}
				else if (level == LogLevel.ERROR)
				{
					System.err.println("ERROR: " + message);
					logger.append("[ERROR] " + this.datePrefix() + message + "\n");
				}
				logger.flush();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.err.println("Logging is not enabled yet!");
		}
	}

	private String datePrefix()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
		Date currentTime = new Date(System.currentTimeMillis());
		return "[" + format.format(currentTime) + "] ";
	}
	
	public User getUserByID(String uid)
	{
		return this.users.get(uid);
	}
	
	public ArrayList<User> getOnlineUsers()
	{
		return new ArrayList<>(this.users.values());
	}
	
	public void registerUser(User user, ClientHandler handler)
	{
		this.users.put(user.getUserID(), user);
		this.clientHandlers.add(handler);
	}
	
	public void unregisterUser(User user, ClientHandler handler)
	{
		this.users.remove(user.getUserID());
		this.clientHandlers.remove(this.clientHandlers.indexOf(handler));
	}
	
	public DBManager getDBManager()
	{
		return this.dbManager;
	}
	
	public static IMServer getServer()
	{
		return IMServer.serverInstance;
	}
}