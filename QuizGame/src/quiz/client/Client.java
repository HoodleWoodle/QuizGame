package quiz.client;

import lib.net.tcp.client.AbstractTCPClient;

public class Client extends AbstractTCPClient
{

	public Client(String server, int port)
	{
		super(server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void received(String message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void closed()
	{
		// TODO Auto-generated method stub

	}

}
