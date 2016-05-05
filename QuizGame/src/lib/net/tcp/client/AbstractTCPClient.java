package lib.net.tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public abstract class AbstractTCPClient
{
	private final String server;
	private final int port;

	private ReceivingThread receiver;
	private Socket socket;
	private DataOutputStream out;

	/**
	 * Creates an instance of AbstractTCPClient.
	 * 
	 * @param server
	 *            the server-tag
	 * @param port
	 *            the server-port
	 */
	public AbstractTCPClient(String server, int port)
	{
		this.server = server;
		this.port = port;
	}

	/**
	 * Connects to the server.
	 * 
	 * @return whether it was successful
	 */
	public final boolean connect()
	{
		try
		{
			// try to connect to server
			socket = new Socket(server, port);
			receiver = new ReceivingThread(this, new BufferedReader(new InputStreamReader(socket.getInputStream())));
			out = new DataOutputStream(socket.getOutputStream());
			return true;
		} catch (IOException e)
		{
			// some Exception
			System.err.println("Cannot connect to server: " + server + ":" + port + "!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the AbstractTCPClient.
	 * 
	 * @return whether it was successful
	 */
	public final boolean close()
	{
		try
		{
			// try to close connection
			receiver.stop();
			socket.close();
			return true;
		} catch (IOException e)
		{
			// some Exception
			System.err.println("Cannot close client!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sends to the server.
	 * 
	 * @param message
	 *            the message to send
	 * @return whether it was successful
	 */
	public final boolean send(String message)
	{
		try
		{
			// try to send a message
			out.writeBytes(message + "\n");
			return true;
		} catch (IOException e)
		{
			// some Exception
			System.err.println("Cannot send to server!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * When the client receives a message.
	 * 
	 * @param message
	 *            the received message
	 */
	protected abstract void received(String message);

	/**
	 * When the client loses connection to the server.
	 */
	protected abstract void closed();

	/**
	 * Getter.
	 * 
	 * @return the server-tag
	 */
	public final String getServer()
	{
		return server;
	}

	/**
	 * Getter.
	 * 
	 * @return the server-port
	 */
	public final int getPort()
	{
		return port;
	}
}