package quiz.server;

import static quiz.net.NetworkMessage.SPLIT_SUB_SUB;
import static quiz.net.NetworkMessage.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkMessage.TAG_LOGIN;
import static quiz.net.NetworkMessage.TAG_REGISTER;
import static quiz.net.NetworkMessage.TAG_REQUEST;
import static quiz.net.NetworkMessage.TAG_REQUEST_ACCEPT;
import static quiz.net.NetworkMessage.TAG_REQUEST_DENY;
import static quiz.net.NetworkMessage.TAG_SET_ACCOUNT;
import static quiz.net.NetworkMessage.TAG_SET_ANSWER;
import static quiz.net.NetworkMessage.TAG_SET_MATCH;
import static quiz.net.NetworkMessage.TAG_SET_OPPONENTS;
import static quiz.net.NetworkMessage.TAG_SET_REQUESTS;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import lib.net.tcp.server.AbstractTCPServer;
import lib.net.tcp.server.ClientThread;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;
import quiz.net.NetworkMessage;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Quirin, Stefan
 * @version XX.XX.XXXX
 */
public class Server extends AbstractTCPServer
{
	private final Random random;

	private final IDataManager dataManager;
	private final Hashtable<Integer, Match> matches;
	private final Hashtable<Integer, Match> requests;
	private final Hashtable<Integer, Integer> accountIDs;
	private final Hashtable<Integer, Integer> clientIDs;

	private int matchID;

	/**
	 * Creates an instance of Server.
	 * 
	 * @param port
	 *            the desired Server-port
	 */
	public Server(int port)
	{
		super(port);

		random = new Random();

		dataManager = new DataManager();
		matches = new Hashtable<Integer, Match>();
		requests = new Hashtable<Integer, Match>();
		accountIDs = new Hashtable<Integer, Integer>();
		clientIDs = new Hashtable<Integer, Integer>();
	}

	@Override
	protected boolean register(ClientThread client)
	{
		sendOpponents();
		return true;
	}

	@Override
	protected void received(ClientThread client, byte[] message)
	{
		NetworkMessage msg = new NetworkMessage(message);

		switch (msg.getTag())
		{
		case TAG_REGISTER:
			workRegister(client, msg);
			break;
		case TAG_LOGIN:
			workLogin(client, msg);
			break;
		case TAG_REQUEST:
			workRequest(client, msg);
			break;
		case TAG_REQUEST_ACCEPT:
			workRequestAccept(client, msg);
			break;
		case TAG_REQUEST_DENY:
			workRequestDeny(client, msg);
			break;
		case TAG_SET_ANSWER:
			workSetAnswer(client, msg);
			break;
		}
	}

	@Override
	protected void closed(ClientThread client)
	{
		sendOpponents();
	}

	private void sendOpponents()
	{
		NetworkMessage msg = new NetworkMessage(TAG_SET_OPPONENTS, convertAccounts());
		for (ClientThread client : clients)
			client.send(msg.getBytes());
	}

	private void workRegister(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.addAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			accountIDs.put(client.getID(), account.getID());
			clientIDs.put(account.getID(), client.getID());

			NetworkMessage msg = new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account));
			client.send(msg.getBytes());
		}
	}

	private void workLogin(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.getAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			NetworkMessage msg = new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account));
			client.send(msg.getBytes());
		}
	}

	private void workRequest(ClientThread client, NetworkMessage message)
	{
		int playerID = accountIDs.get(client);
		Category category = null;
		Account[] accounts = new Account[2];

		switch (message.getParameter(0))
		{
		case NetworkMessage.TAG_REQUEST_0:
			category = getCategory(Integer.parseInt(message.getParameter(1)));
			int opponentID = Integer.parseInt(message.getParameter(2));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case NetworkMessage.TAG_REQUEST_1:
			category = getCategory(Integer.parseInt(message.getParameter(1)));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		case NetworkMessage.TAG_REQUEST_2:
			category = getRandomCategory();
			opponentID = Integer.parseInt(message.getParameter(1));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case NetworkMessage.TAG_REQUEST_3:
			category = getRandomCategory();
			playerID = Integer.parseInt(message.getParameter(1));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		default:
			break;
		}

		Match request = new Match(matchID++, category, accounts, new Question[0], new int[0][0]);
		requests.put(request.getID(), request);

		int otherID = request.getOpponents()[1].getID();
		NetworkMessage msg = new NetworkMessage(TAG_SET_REQUESTS, convertMatches(otherID, requests));
		ClientThread otherClient = getClient(clientIDs.get(otherID));
		otherClient.send(msg.getBytes());
	}

	private void workRequestAccept(ClientThread client, NetworkMessage message)
	{
		int matchID = Integer.parseInt(message.getParameter(0));
		Match match = requests.get(matchID);

		int playerID = accountIDs.get(client.getID());
		int otherID = -1;
		for (Account account : match.getOpponents())
		{
			int accountID = account.getID();
			if (accountID != playerID)
				otherID = accountID;
		}

		if (!hasMatch(playerID) && !hasMatch(otherID))
		{
			requests.remove(matchID);
			matches.put(match.getID(), match);
		}

		NetworkMessage msg = new NetworkMessage(TAG_SET_MATCH, convertMatch(match));
		client.send(msg.getBytes());
		ClientThread otherClient = getClient(clientIDs.get(otherID));
		otherClient.send(msg.getBytes());
	}

	private void workRequestDeny(ClientThread client, NetworkMessage message)
	{
		int matchID = Integer.parseInt(message.getParameter(0));
		requests.remove(matchID);
	}

	private void workSetAnswer(ClientThread client, NetworkMessage message)
	{
		// TODO
	}

	private String[] convertAccounts()
	{
		List<Account> accounts = dataManager.getAccounts();

		String[] result = new String[accounts.size()];

		for (int i = 0; i < accounts.size(); i++)
		{
			Account account = accounts.get(i);

			account.setOnline(isOnline(account.getID()));
			if (!hasMatch(account.getID()))
				account.setAvailable(true);

			result[i] = convertAccount(account);
		}

		return result;
	}

	private String[] convertMatches(int ID, Hashtable<Integer, Match> matches)
	{
		List<Match> ms = new ArrayList<Match>();
		for (Integer key : matches.keySet())
		{
			Match match = matches.get(key);

			Account[] opponents = matches.get(key).getOpponents();
			for (int i = 0; i < opponents.length; i++)
				if (opponents[i].getID() == ID)
					ms.add(match);
		}

		String[] result = new String[ms.size()];

		for (int i = 0; i < result.length; i++)
			result[i] = convertMatch(matches.get(i));

		return result;
	}

	private String convertAccount(Account account)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(account.getID());
		builder.append(SPLIT_SUB_SUB);
		builder.append(account.getName());
		builder.append(SPLIT_SUB_SUB);
		builder.append(account.getScore());
		builder.append(SPLIT_SUB_SUB);
		builder.append(account.isOnline());
		builder.append(SPLIT_SUB_SUB);
		builder.append(account.isAvailable());

		return builder.toString();
	}

	private String convertMatch(Match match)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(match.getID());
		builder.append(SPLIT_SUB_SUB);
		builder.append(match.getCategory());
		builder.append(SPLIT_SUB_SUB);

		StringBuilder subBuilder = new StringBuilder();
		Account[] opponents = match.getOpponents();
		for (int i = 0; i < opponents.length; i++)
		{
			subBuilder.append(opponents[i].getID());
			if (i < opponents.length - 1)
				subBuilder.append(SPLIT_SUB_SUB);
		}
		builder.append(subBuilder.toString());

		subBuilder = new StringBuilder();
		Question[] questions = match.getQuestions();
		for (int i = 0; i < questions.length; i++)
		{
			subBuilder.append(convertQuestion(questions[i]));
			if (i < questions.length - 1)
				subBuilder.append(SPLIT_SUB_SUB);
		}
		builder.append(subBuilder.toString());

		subBuilder = new StringBuilder();
		int[][] answers = match.getAnswers();
		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
			{
				subBuilder.append(answers[i][j]);
				if (i * j < answers.length - 1)
					subBuilder.append(SPLIT_SUB_SUB);
			}
		builder.append(subBuilder.toString());

		return builder.toString();
	}

	private String convertQuestion(Question question)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(question.getCategory().ordinal());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(question.getQuestion());
		builder.append(SPLIT_SUB_SUB_SUB);

		String[] answers = question.getAnswers();
		for (int i = 0; i < answers.length; i++)
		{
			builder.append(answers[i]);
			if (i < answers.length - 1)
				builder.append(SPLIT_SUB_SUB_SUB);
		}

		return builder.toString();
	}

	private boolean isOnline(int ID)
	{
		if (clientIDs.get(ID) == null)
			return false;
		return getClient(clientIDs.get(ID)) != null;
	}

	private boolean hasMatch(int ID)
	{
		for (Integer key : matches.keySet())
		{
			Account[] opponents = matches.get(key).getOpponents();
			for (int i = 0; i < opponents.length; i++)
				if (opponents[i].getID() == ID)
					return true;
		}

		return false;
	}

	private Category getRandomCategory()
	{
		Category[] categories = Category.values();
		int rand = random.nextInt(categories.length);
		return categories[rand];
	}

	private Account getRandomAccount(int playerID, List<Account> possibles)
	{
		int rand = random.nextInt(possibles.size());
		Account account = possibles.remove(rand);
		if (account.getID() == playerID)
			return getRandomAccount(playerID, possibles);
		return account;
	}

	private Category getCategory(int ID)
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
		// if (args.length != 1)
		// {
		// System.err.println("Invalid args!");
		// System.exit(1);
		// }

		try
		{
			int port = 5555;// Integer.parseInt(args[0]);
			Server server = new Server(port);
			System.out.println("Starting Server!");
			if (!server.start())
				throw new Exception();
		} catch (Exception e)
		{
			System.err.println("Cannot start Server!");
		}
	}
}