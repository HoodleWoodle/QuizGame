package quiz.server;

import static quiz.net.NetworkKeys.SPLIT_SUB_SUB;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkKeys.TAG_ALREADY_IN_MATCH;
import static quiz.net.NetworkKeys.TAG_INVALID_LOGIN_DETAILS;
import static quiz.net.NetworkKeys.TAG_INVALID_REGISTER_DETAILS;
import static quiz.net.NetworkKeys.TAG_LOGIN;
import static quiz.net.NetworkKeys.TAG_REGISTER;
import static quiz.net.NetworkKeys.TAG_REQUEST;
import static quiz.net.NetworkKeys.TAG_REQUEST_0;
import static quiz.net.NetworkKeys.TAG_REQUEST_1;
import static quiz.net.NetworkKeys.TAG_REQUEST_2;
import static quiz.net.NetworkKeys.TAG_REQUEST_3;
import static quiz.net.NetworkKeys.TAG_REQUEST_ACCEPT;
import static quiz.net.NetworkKeys.TAG_REQUEST_DENY;
import static quiz.net.NetworkKeys.TAG_SET_ACCOUNT;
import static quiz.net.NetworkKeys.TAG_SET_ANSWER;
import static quiz.net.NetworkKeys.TAG_SET_MATCH;
import static quiz.net.NetworkKeys.TAG_SET_OPPONENTS;
import static quiz.net.NetworkKeys.TAG_SET_REQUESTS;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import lib.net.tcp.NetworkMessage;
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
public class Server extends AbstractTCPServer // TODO closing if exit
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
		int accountID = accountIDs.remove(client.getID());
		clientIDs.remove(accountID);
		sendOpponents();
	}

	private void workRegister(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.addAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			int accountID = account.getID();
			int clientID = client.getID();
			accountIDs.put(clientID, accountID);
			clientIDs.put(accountID, clientID);

			NetworkMessage msg = new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account));
			client.send(msg.getBytes());
			sendOpponents();
			sendRequests(accountID);
		} else
			client.send(new NetworkMessage(TAG_INVALID_REGISTER_DETAILS, new String[0]).getBytes());
	}

	private void workLogin(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.getAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			int accountID = account.getID();
			int clientID = client.getID();
			accountIDs.put(clientID, accountID);
			clientIDs.put(accountID, clientID);

			NetworkMessage msg = new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account));
			client.send(msg.getBytes());
			sendOpponents();
			sendRequests(accountID);
		} else
			client.send(new NetworkMessage(TAG_INVALID_LOGIN_DETAILS, new String[0]).getBytes());
	}

	private void workRequest(ClientThread client, NetworkMessage message)
	{
		int playerID = accountIDs.get(client.getID());
		Category category = null;
		Account[] accounts = new Account[2];

		switch (message.getParameter(0))
		{
		case TAG_REQUEST_0:
			category = getCategory(Integer.parseInt(message.getParameter(1)));
			int opponentID = Integer.parseInt(message.getParameter(2));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case TAG_REQUEST_1:
			category = getCategory(Integer.parseInt(message.getParameter(1)));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		case TAG_REQUEST_2:
			category = getRandomCategory();
			opponentID = Integer.parseInt(message.getParameter(1));

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = dataManager.getAccount(opponentID);
			break;
		case TAG_REQUEST_3:
			category = getRandomCategory();

			accounts[0] = dataManager.getAccount(playerID);
			accounts[1] = getRandomAccount(playerID, dataManager.getAccounts());
			break;
		}

		Match request = new Match(matchID++, category, accounts, new Question[0], new int[0][0]);
		requests.put(request.getID(), request);

		int otherID = request.getOpponents()[1].getID();
		if (isOnline(otherID))
			sendRequests(otherID);
	}

	private void workRequestAccept(ClientThread client, NetworkMessage message)
	{
		int matchID = Integer.parseInt(message.getParameter(0));
		Match match = requests.get(matchID);

		int playerID = accountIDs.get(client.getID());
		int otherID = match.getOpponents()[0].getID();

		if (hasMatch(playerID) || hasMatch(otherID))
		{
			client.send(new NetworkMessage(TAG_ALREADY_IN_MATCH, new String[0]).getBytes());
			return;
		}

		requests.remove(matchID);
		matches.put(match.getID(), match);

		NetworkMessage msg = new NetworkMessage(TAG_SET_MATCH, convertMatch(match));
		client.send(msg.getBytes());
		ClientThread otherClient = getClient(clientIDs.get(otherID));
		otherClient.send(msg.getBytes());
	}

	private void workRequestDeny(ClientThread client, NetworkMessage message)
	{
		int matchID = Integer.parseInt(message.getParameter(0));
		requests.remove(matchID);

		// TODO
	}

	private void workSetAnswer(ClientThread client, NetworkMessage message)
	{
		// TODO
	}

	private void sendOpponents()
	{
		for (int i = 0; i < clients.size(); i++)
		{
			ClientThread client = clients.get(i);
			Integer accountID = accountIDs.get(client.getID());
			if (accountID == null)
				continue;
			NetworkMessage msg = new NetworkMessage(TAG_SET_OPPONENTS, convertAccounts(accountID));
			client.send(msg.getBytes());
		}
	}

	private void sendRequests(int accountID)
	{
		NetworkMessage msg = new NetworkMessage(TAG_SET_REQUESTS, convertMatches(accountID, requests));
		ClientThread client = getClient(clientIDs.get(accountID));
		client.send(msg.getBytes());
	}

	private String[] convertAccounts(int ID)
	{
		List<Account> accounts = dataManager.getAccounts();

		String[] result = new String[accounts.size() - 1];

		boolean bool = false;
		for (int i = 0; i < accounts.size(); i++)
		{
			Account account = accounts.get(i);
			if (account.getID() == ID)
			{
				bool = true;
				continue;
			}

			result[i - (bool ? 1 : 0)] = convertAccount(account); // TODO there is always the ME-Account (why checking?)
		}

		return result;
	}

	private String[] convertMatches(int ID, Hashtable<Integer, Match> matches)
	{
		List<Match> ms = new ArrayList<Match>();
		for (Integer key : matches.keySet())
		{
			Match match = matches.get(key);

			Account[] opponents = match.getOpponents(); // TODO online, available
			for (int i = 0; i < opponents.length; i++)
				if (opponents[i].getID() == ID)
				{
					ms.add(match);
					break;
				}
		}

		String[] result = new String[ms.size()];

		for (int i = 0; i < result.length; i++)
			result[i] = convertMatch(matches.get(i));

		return result;
	}

	private String convertAccount(Account account)
	{
		account.setOnline(isOnline(account.getID()));
		if (!hasMatch(account.getID()))
			account.setAvailable(true);

		StringBuilder builder = new StringBuilder();

		builder.append(account.getID());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(account.getName());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(account.getScore());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(account.isOnline());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(account.isAvailable());

		return builder.toString();
	}

	private String convertMatch(Match match)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(match.getID());
		builder.append(SPLIT_SUB_SUB);
		builder.append(match.getCategory().ordinal());
		builder.append(SPLIT_SUB_SUB);

		Question[] questions = match.getQuestions();

		Account[] opponents = match.getOpponents();
		for (int i = 0; i < opponents.length; i++)
		{
			builder.append(convertAccount(opponents[i]));
			if (i < opponents.length - 1 || questions.length > 0)
				builder.append(SPLIT_SUB_SUB);
		}

		int[][] answers = match.getAnswers();

		for (int i = 0; i < questions.length; i++)
		{
			builder.append(convertQuestion(questions[i]));
			if (i < questions.length - 1 || answers[0].length > 0)
				builder.append(SPLIT_SUB_SUB);
		}

		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
			{
				builder.append(answers[i][j]);
				if (i * j < answers.length - 1)
					builder.append(SPLIT_SUB_SUB);
			}

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
		return true;
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
		// TODO
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