package lib.net.tcp.client;

import java.io.DataInputStream;
import java.util.ArrayList;

import lib.net.tcp.NetworkMessage;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class ReceivingThread implements Runnable
{
	private final AbstractTCPClient client;
	private final DataInputStream in;

	private boolean running;

	/**
	 * Creates an instance of ReceivingThread.
	 * 
	 * @param client
	 *            the client
	 * @param in
	 *            the input
	 */
	ReceivingThread(AbstractTCPClient client, DataInputStream in)
	{
		this.client = client;
		this.in = in;
		// start thread
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * Stops the thread.
	 */
	void stop()
	{
		running = false;
	}

	@Override
	public void run()
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
				client.received(message);
			} catch (Exception e)
			{
				// some Exception
				System.err.println("An exception has occured!");
				client.close();
				client.closed();
				e.printStackTrace(); // TODO
			}
		}
	}
}