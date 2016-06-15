package lib.net.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class ReceivingThread implements Runnable
{
	private final AbstractTCPClient client;
	private final BufferedReader in;

	private boolean running;

	/**
	 * Creates an instance of ReceivingThread.
	 * 
	 * @param client
	 *            the client
	 * @param in
	 *            the input
	 */
	ReceivingThread(AbstractTCPClient client, BufferedReader in)
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
			try
			{
				// waiting for message
				byte[] message = in.readLine().getBytes();
				if (message != null)
					// if message is correct
					client.received(message);
				else
					throw new IOException();
			} catch (IOException e)
			{
				// some Exception
				client.close();
				client.closed();
				// e.printStackTrace();
			}
	}
}