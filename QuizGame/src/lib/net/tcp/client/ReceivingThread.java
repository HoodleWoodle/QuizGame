package lib.net.tcp.client;

import java.io.BufferedInputStream;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class ReceivingThread implements Runnable
{
	private final AbstractTCPClient client;
	private final BufferedInputStream in;

	private boolean running;

	/**
	 * Creates an instance of ReceivingThread.
	 * 
	 * @param client
	 *            the client
	 * @param in
	 *            the input
	 */
	ReceivingThread(AbstractTCPClient client, BufferedInputStream in)
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
				int size = in.available();
				// waiting for message
				if (size == 0)
					continue;
				byte[] message = new byte[size];
				in.read(message);
				// if message is correct
				client.received(message);
			} catch (Exception e)
			{
				// some Exception
				client.close();
				client.closed();
				// e.printStackTrace();
			}
	}
}