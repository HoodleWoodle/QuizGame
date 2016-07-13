package lib.net.tcp.client;

import java.io.DataInputStream;

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
			try
			{
				int size = in.readInt();
				byte[] message = new byte[size];
				in.read(message);
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