package lib.net.tcp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public class ClientThread implements Runnable
{
	private static int ID_NEXT;

	private final int ID;

	private final AbstractTCPServer server;
	private final Socket socket;
	private BufferedInputStream in;
	private BufferedOutputStream out;

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
			in = new BufferedInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
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
	 * Sends a message to the client.
	 * 
	 * @param message
	 *            the message to send
	 * @return whether it was successful
	 */
	public final boolean send(byte[] message)
	{
		try
		{
			// try to send some message
			out.write(message);
			out.flush();
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
				int size = in.available();
				// waiting for message
				if (size == 0)
					continue;
				byte[] message = new byte[size];
				in.read(message);
				if (running)// if client is connected and message is correct
					server.received(this, message);
			} catch (Exception e)
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