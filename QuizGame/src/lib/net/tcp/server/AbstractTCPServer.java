package lib.net.tcp.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public abstract class AbstractTCPServer
{
	private final WelcomeThread welcome;

	/**
	 * The registered clients.
	 */
	protected final List<ClientThread> clients;

	/**
	 * Creates an instance of AbstractTCPServer.
	 * 
	 * @param port
	 *            the server-port
	 */
	public AbstractTCPServer(int port)
	{
		welcome = new WelcomeThread(this, port);
		clients = new ArrayList<ClientThread>();
	}

	/**
	 * Starts the AbstractTCPServer.
	 * 
	 * @return whether it was successful
	 */
	public final boolean start()
	{
		return welcome.start();
	}

	/**
	 * Closes the AbstractTCPServer.
	 *
	 * @return whether it was successful
	 */
	public final boolean close()
	{
		// closes all connections
		for (int i = 0; i < clients.size(); i++)
			clients.get(i).close();
		return welcome.close();
	}

	/**
	 * Sends a broadcast to all clients.
	 * 
	 * @param message
	 *            the message to send
	 */
	public final void broadcast(String message)
	{
		// sends a message to all clients
		for (ClientThread client : clients)
			client.send(message);
	}

	/**
	 * Adds a client.
	 * 
	 * @param client
	 *            the client
	 */
	void addClient(ClientThread client)
	{
		clients.add(client);
	}

	/**
	 * Removes a client by ID.
	 * 
	 * @param ID
	 *            the ID
	 * @return whether removing was successful
	 */
	final boolean removeClient(int ID)
	{
		// removes a client with a special ID
		for (int i = 0; i < clients.size(); i++)
		{
			ClientThread client = clients.get(i);
			if (ID == client.getID())
			{
				clients.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Kicks a client by ID.
	 * 
	 * @param ID
	 *            the ID
	 * @return whether kicking was successful
	 */
	public boolean kickClient(int ID)
	{
		// kicks a client with a special ID
		for (int i = 0; i < clients.size(); i++)
		{
			ClientThread client = clients.get(i);
			if (ID == client.getID())
				return client.close();
		}
		return false;
	}

	/**
	 * Returns a client by ID.
	 * 
	 * @param ID
	 *            the client-ID
	 * @return the required client
	 */
	public final ClientThread getClient(int ID)
	{
		for (ClientThread client : clients)
			if (ID == client.getID())
				return client;
		return null;
	}

	/**
	 * Allows/Denies a client to connect to server.
	 * 
	 * @param client
	 *            the client
	 * @return whether the client is allowed to connect
	 */
	protected abstract boolean register(ClientThread client);

	/**
	 * When the server receives a message.
	 * 
	 * @param client
	 *            the client
	 * @param message
	 *            the message
	 */
	protected abstract void received(ClientThread client, String message);

	/**
	 * When a client-connection was closed.
	 * 
	 * @param client
	 *            the client
	 */
	protected abstract void closed(ClientThread client);

	/**
	 * Getter.
	 * 
	 * @return the server-port
	 */
	public final int getPort()
	{
		return welcome.getPort();
	}
}