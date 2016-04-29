package lib.net.tcp.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public class ClientThread implements Runnable
{
	/**
	 * The ID of the next client.
	 */
	private static int ID_NEXT;

	/**
	 * The ID.
	 */
	private final int ID;

	/**
	 * The AbstractTCPServer.
	 */
	private final AbstractTCPServer server;
	/**
	 * The connection-socket.
	 */
	private final Socket socket;
	/**
	 * The input.
	 */
	private BufferedReader in;
	/**
	 * The output.
	 */
	private DataOutputStream out;
	/**
	 * Whether the thread is running.
	 */
	private boolean running;

	/**
	 * Creates an instance of ClientThread.
	 * 
	 * @param server
	 *            the server
	 * @param socket
	 *            the connection-socket.
	 */
	ClientThread(AbstractTCPServer server, Socket socket)
	{
		this.server = server;
		this.socket = socket;
		this.ID = ID_NEXT++;
		try
		{
			// try to open connection
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			// start thread
			Thread t = new Thread(this);
			t.start();
		} catch (IOException e)
		{
			// some Exception
			close();
			server.closed(this);
			// e.printStackTrace();
		}
	}

	/**
	 * Sends to the client.
	 * 
	 * @param message
	 *            the message to send
	 * @return whether it was successful
	 */
	public final boolean send(String message)
	{
		try
		{
			// try to send some message
			out.writeBytes(message + "\n");
			return true;
		} catch (IOException e)
		{
			// some Exception
			close();
			server.closed(this);
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the client.
	 * 
	 * @return whether closing was successful
	 */
	final boolean close()
	{
		try
		{
			// try to close
			running = false;
			server.removeClient(ID);
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
	 * @return the client-ID
	 */
	public final int getID()
	{
		return ID;
	}

	@Override
	public final void run()
	{
		running = true;
		while (running)
			try
			{
				// waiting for messge
				String message = in.readLine();
				if (running)
					if (message != null)
						// if client is connected and message is correct
						server.received(this, message);
					else
						throw new IOException();
			} catch (IOException e)
			{
				// some Exception
				if (running)
				{
					close();
					server.closed(this);
				}
				// e.printStackTrace();
			}
	}
}