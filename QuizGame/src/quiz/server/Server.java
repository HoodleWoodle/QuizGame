package quiz.server;

import java.util.Hashtable;

import lib.net.tcp.server.AbstractTCPServer;
import lib.net.tcp.server.ClientThread;
import quiz.model.Account;
import quiz.model.Match;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

public class Server extends AbstractTCPServer
{
	private final IDataManager dataManager;
	private final Hashtable<Integer, Match> matches;

	public Server(int port)
	{
		super(port);

		dataManager = new DataManager();
		matches = new Hashtable<Integer, Match>();
	}

	@Override
	protected boolean register(ClientThread client)
	{
		return true;
	}

	@Override
	protected void received(ClientThread client, String message)
	{
		String[] msg = message.split(",");

		switch (msg[0])
		{
		case "reg":
			Account acc = dataManager.addAccount(msg[1], msg[2]);
			if (acc != null)
				client.send(parseAccount(acc));
			break;
		case "log":
			acc = dataManager.getAccount(msg[1], msg[2]);
			if (acc != null)
				client.send(parseAccount(acc));
			break;
		case "req":
			break;
		case "accept":
			Match match = matches.get(msg[1]);
			break;
		case "deny":
			break;
		case "setAnsw":
			break;

		default:
			break;
		}
	}

	private String parseAccount(Account acc)
	{
		String msg = acc.getID() + "," + acc.getName() + "," + acc.getScore() + "," + acc.isOnline() + "," + acc.isAvailable();
		return msg;
	}

	@Override
	protected void closed(ClientThread client)
	{

	}
}