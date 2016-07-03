package lib.net.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import lib.net.tcp.NetworkMessage;

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
	private DataInputStream in;
	private DataOutputStream out;

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
			in = new DataInputStream(socket.getInputStream());
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
			out.write(NetworkMessage.EOF);
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
		{
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			try
			{
				bytes.clear();
				byte b = 0;
				while ((b = (byte) in.read()) != NetworkMessage.EOF)
				{
					if (b == -1)
						throw new Exception();
					bytes.add(b);
				}
				byte[] message = new byte[bytes.size()];
				for (int i = 0; i < message.length; i++)
					message[i] = bytes.get(i);
				// if a message received
				if (running)
					server.received(this, message);
			} catch (Exception e)
			{
				// some Exception
				// TODO System.err.println("An exception has occured!");
				if (running)
				{
					close();
					server.closed(this);
				}
				// e.printStackTrace();
			}
		}
	}
}