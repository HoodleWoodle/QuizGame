package quiz.server;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import lib.net.tcp.server.AbstractTCPServer;
import lib.net.tcp.server.ClientThread;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Quirin, Stefan
 * @version XX.XX.XXXX
 */
public class Server extends AbstractTCPServer
{
	private int matchID = 0;

	private final IDataManager dataManager;
	private final Hashtable<Integer, Match> matches;
	private final Hashtable<Integer, Match> requests;
	private final Hashtable<Integer, Integer> accountIDs;
	private final Hashtable<Integer, Integer> clientIDs;

	/**
	 * Creates an instance of Server.
	 * 
	 * @param port
	 *            the desired Server-port
	 */
	public Server(int port)
	{
		super(port);

		dataManager = new DataManager();
		matches = new Hashtable<Integer, Match>();
		requests = new Hashtable<Integer, Match>();
		accountIDs = new Hashtable<Integer, Integer>();
		clientIDs = new Hashtable<Integer, Integer>();
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
			{
				client.send(parseAccount(acc));

				accountIDs.put(client.getID(), acc.getID());
				clientIDs.put(acc.getID(), client.getID());
			}
			break;
		case "log":
			acc = dataManager.getAccount(msg[1], msg[2]);
			if (acc != null)
				client.send(parseAccount(acc));
			break;
		case "req":
			workRequest(client, msg);
			break;
		case "acc":
			Match match = matches.get(msg[1]);
			break;
		case "den":
			break;
		case "set":
			break;

		default:
			break;
		}
	}

	private void workRequest(ClientThread client, String[] msg)
	{
		int playerID = accountIDs.get(client);
		Category category = null;
		Account[] accounts = null;
		switch (msg[1])
		{
		case "1":
			category = getCategory(Integer.parseInt(msg[2]));
			int opponentID = Integer.parseInt(msg[3]);
			accounts = new Account[2];
			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case "2":
			category = getCategory(Integer.parseInt(msg[2]));
			accounts = new Account[2];
			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		case "3":
			category = getRandomCategory();
			opponentID = Integer.parseInt(msg[2]);
			accounts = new Account[2];
			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case "4":
			category = getRandomCategory();
			playerID = Integer.parseInt(msg[2]);
			accounts = new Account[2];
			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		default:
			break;
		}

		Match request = new Match(matchID++, category, accounts, new Question[0], new int[0][0]);
		requests.put(request.getID(), request);

		// getClient(clientIDs.get(request.getOpponents()[1])).send(/**/);
	}

	private Category getRandomCategory()
	{
		Category[] categories = Category.values();
		int rand = random.nextInt(categories.length);
		return categories[rand];
	}

	private final Random random = new Random();

	private Account getRandomAccount(int playerID, List<Account> possibles)
	{
		int rand = random.nextInt(possibles.size());
		Account account = possibles.remove(rand);
		if (account.getID() == playerID)
			return getRandomAccount(playerID, possibles);
		return account;
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

	// TODO
	public Category getCategory(int ID)
	{
		Category[] values = Category.values();
		if (ID > values.length)
			return null;

		return values[ID];
	}

	/**
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.println("Invalid args!");
			System.exit(1);
		}

		try
		{
			int port = Integer.parseInt(args[0]);
			Server server = new Server(port);
			if (!server.start())
				throw new Exception();
		} catch (Exception e)
		{
		}
	}
}