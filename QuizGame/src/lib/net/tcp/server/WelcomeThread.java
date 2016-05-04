package lib.net.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class WelcomeThread implements Runnable
{
	/**
	 * The AbstractTCPServer.
	 */
	private final AbstractTCPServer server;
	/**
	 * The server-port.
	 */
	private final int port;
	/**
	 * The server-socket.
	 */
	private ServerSocket socket;
	/**
	 * Whether the thread is running.
	 */
	private boolean running;

	/**
	 * Creates an instance of WelcomeThread.
	 * 
	 * @param server
	 *            the server
	 * @param port
	 *            the server-port
	 */
	WelcomeThread(AbstractTCPServer server, int port)
	{
		this.server = server;
		this.port = port;
	}

	/**
	 * Starts the thread.
	 * 
	 * @return whether it was successful
	 */
	boolean start()
	{
		try
		{
			// try to open connection
			socket = new ServerSocket(port);
			Thread t = new Thread(this);
			t.start();
			return true;
		} catch (IOException e)
		{
			// some Exception
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the thread.
	 * 
	 * @return whether it was successful
	 */
	boolean close()
	{
		try
		{
			// try to close connection
			running = false;
			socket.close();
			return true;
		} catch (IOException e)
		{
			// some Exception
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Getter.
	 * 
	 * @return the server-port
	 */
	public int getPort()
	{
		return port;
	}

	@Override
	public void run()
	{
		running = true;
		while (running)
			try
			{
				// waiting for new clients
				ClientThread client = new ClientThread(server, socket.accept());
				if (server.register(client))
					server.addClient(client);
				else
					client.close();
			} catch (IOException e)
			{
				// some Exception
				if (running)
					System.err.println("Error while running!");
				// e.printStackTrace();
			}
	}
}