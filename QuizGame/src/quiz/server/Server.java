package quiz.server;

import static quiz.net.NetworkKeys.SPLIT_SUB_SUB;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkKeys.TAG_ALREADY_LOGGED_IN;
import static quiz.net.NetworkKeys.TAG_ALREADY_REQUESTED;
import static quiz.net.NetworkKeys.TAG_INVALID_LOGIN_DETAILS;
import static quiz.net.NetworkKeys.TAG_INVALID_REGISTER_DETAILS;
import static quiz.net.NetworkKeys.TAG_LOGIN;
import static quiz.net.NetworkKeys.TAG_NO_OPPONENTS_AVAILABLE;
import static quiz.net.NetworkKeys.TAG_OPPONENT_NOT_AVAILABLE;
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
import static quiz.net.NetworkKeys.TAG_SET_QUESTION;
import static quiz.net.NetworkKeys.TAG_SET_REQUESTS;
import static quiz.net.NetworkKeys.TAG_SET_SENT_REQUESTS;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import lib.net.tcp.NetworkMessage;
import lib.net.tcp.server.AbstractTCPServer;
import lib.net.tcp.server.ClientThread;
import quiz.Constants;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;
import quiz.server.model.MatchStep;
import quiz.server.view.ServerView;

/**
 * @author Quirin, Stefan
 * @version 14.07.2016
 */
// TODO Server GUI, --> closing if exit
public final class Server extends AbstractTCPServer
{
	private final Random random;

	private final IDataManager dataManager;
	private final Hashtable<Integer, Integer> accountIDs;
	private final Hashtable<Integer, Integer> clientIDs;
	private final Hashtable<Integer, Match> requests;
	private final Hashtable<Integer, Match> matches;
	private final Hashtable<Integer, MatchStep> matchSteps;

	private int nextMatchID;

	/**
	 * Creates an instance of Server.
	 * 
	 * @param dataManager
	 * 
	 * @param port
	 *            the desired Server-port
	 */
	public Server(IDataManager dataManager, int port)
	{
		super(port);
		this.dataManager = dataManager;

		random = new Random();

		accountIDs = new Hashtable<Integer, Integer>();
		clientIDs = new Hashtable<Integer, Integer>();
		requests = new Hashtable<Integer, Match>();
		matches = new Hashtable<Integer, Match>();
		matchSteps = new Hashtable<Integer, MatchStep>();
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
		Integer accountID = accountIDs.remove(client.getID());
		if (accountID == null)
			return;
		clientIDs.remove(accountID);

		Account account = dataManager.getAccount(accountID);
		System.out.println("Account disconnected. (" + account.getName() + "[" + account.getID() + "])");

		sendOpponents();
	}

	private void workRegister(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.addAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			addAccount(account, client);

			System.out.println("Account registered. (" + account.getName() + "[" + account.getID() + "])");
		} else
			client.send(new NetworkMessage(TAG_INVALID_REGISTER_DETAILS, new String[0]).getBytes());
	}

	private void workLogin(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.getAccount(message.getParameter(0), message.getParameter(1));
		if (account != null)
		{
			addAccount(account, client);

			System.out.println("Account logged in. (" + account.getName() + "[" + account.getID() + "])");
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

		if (accounts[1] == null)
		{
			client.send(new NetworkMessage(TAG_NO_OPPONENTS_AVAILABLE, new String[0]).getBytes());
			return;
		}

		if (existsRequest(playerID, accounts[1].getID()))
		{
			client.send(new NetworkMessage(TAG_ALREADY_REQUESTED, new String[0]).getBytes());
			return;
		}

		Match request = new Match(nextMatchID++, category, accounts, new Question[0], new int[0][0]);
		requests.put(request.getID(), request);

		sendSentRequests(playerID);

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

		if (!isOnline(otherID) || isInMatch(playerID) || isInMatch(otherID))
		{
			client.send(new NetworkMessage(TAG_OPPONENT_NOT_AVAILABLE, new String[0]).getBytes());
			return;
		}

		requests.remove(matchID);
		matches.put(match.getID(), match);

		sendSentRequests(otherID);
		sendRequests(playerID);

		sendOpponents();

		sendQuestion(match);
		sendMatch(match);
	}

	private void workRequestDeny(ClientThread client, NetworkMessage message)
	{
		int matchID = Integer.parseInt(message.getParameter(0));
		Match request = requests.remove(matchID);

		sendSentRequests(request.getOpponents()[0].getID());
		sendRequests(request.getOpponents()[1].getID());
	}

	private void workSetAnswer(ClientThread client, NetworkMessage message)
	{
		int answer = Integer.parseInt(message.getParameter(0));

		int accountID = accountIDs.get(client.getID());
		Match match = getMatch(accountID);
		MatchStep matchStep = matchSteps.get(match.getID());

		matchStep.setAnswer(dataManager.getAccount(accountID), answer);

		if (matchStep.isDone())
		{
			if (match.getQuestions().length < Constants.QUESTION_COUNT)
			{
				// TODO
			} else
				sendQuestion(match);
			sendMatch(match);
		}
	}

	private void addAccount(Account account, ClientThread client)
	{
		int accountID = account.getID();
		int clientID = client.getID();

		if (isOnline(accountID))
		{
			NetworkMessage msg = new NetworkMessage(TAG_ALREADY_LOGGED_IN, new String[0]);
			client.send(msg.getBytes());
			return;
		}

		accountIDs.put(clientID, accountID);
		clientIDs.put(accountID, clientID);

		NetworkMessage msg = new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account));
		client.send(msg.getBytes());
		sendOpponents();
		sendRequests(accountID);
	}

	private void sendMatch(Match match)
	{
		NetworkMessage msg = new NetworkMessage(TAG_SET_MATCH, convertMatch(match));

		sendToOpponents(match, msg);
	}

	private void sendQuestion(Match match)
	{
		List<Question> questions = dataManager.getQuestions(match.getCategory());
		questions.remove(match.getQuestions());
		Question question = questions.get(random.nextInt(questions.size()));

		matchSteps.put(match.getID(), new MatchStep(question));

		NetworkMessage msg = new NetworkMessage(TAG_SET_QUESTION, convertQuestion(question));

		sendToOpponents(match, msg);
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
		NetworkMessage msg = new NetworkMessage(TAG_SET_REQUESTS, convertMatches(accountID, false));
		ClientThread client = getClient(clientIDs.get(accountID));
		client.send(msg.getBytes());
	}

	private void sendSentRequests(int accountID)
	{
		NetworkMessage msg = new NetworkMessage(TAG_SET_SENT_REQUESTS, convertMatches(accountID, true));
		ClientThread client = getClient(clientIDs.get(accountID));
		client.send(msg.getBytes());
	}

	private void sendToOpponents(Match match, NetworkMessage msg)
	{
		Account[] opponents = match.getOpponents();
		ClientThread client = getClient(clientIDs.get(opponents[0].getID()));
		client.send(msg.getBytes());
		ClientThread otherClient = getClient(clientIDs.get(opponents[1].getID()));
		otherClient.send(msg.getBytes());
	}

	private boolean isOnline(int accountID)
	{
		if (clientIDs.get(accountID) == null)
			return false;
		return true;
	}

	private boolean existsRequest(int accountID_0, int accountID_1)
	{
		for (Integer key : requests.keySet())
		{
			Account[] opponents = requests.get(key).getOpponents();

			boolean first = opponents[0].getID() == accountID_0 || opponents[1].getID() == accountID_0;
			boolean second = opponents[0].getID() == accountID_1 || opponents[1].getID() == accountID_1;

			if (first && second)
				return true;
		}

		return false;
	}

	private boolean isInMatch(int accountID)
	{
		return getMatch(accountID) != null;
	}

	private Match getMatch(int accountID)
	{
		for (Integer key : matches.keySet())
		{
			Match match = matches.get(key);
			Account[] opponents = match.getOpponents();
			for (int i = 0; i < opponents.length; i++)
				if (opponents[i].getID() == accountID)
					return match;
		}

		return null;
	}

	private Account getRandomAccount(int accountID, List<Account> possibles)
	{
		int size = possibles.size();
		if (size == 0)
			return null;

		int rand = random.nextInt(size);
		Account account = possibles.remove(rand);

		int otherID = account.getID();
		if (otherID == accountID || existsRequest(otherID, accountID))
			return getRandomAccount(accountID, possibles);

		return account;
	}

	private Category getRandomCategory()
	{
		Category[] categories = Category.values();
		int rand = random.nextInt(categories.length);
		return categories[rand];
	}

	private Category getCategory(int categoryID)
	{
		Category[] values = Category.values();
		if (categoryID > values.length)
			return null;

		return values[categoryID];
	}

	private String[] convertAccounts(int accountID)
	{
		List<Account> accounts = dataManager.getAccounts();

		String[] result = new String[accounts.size() - 1];

		boolean bool = false;
		for (int i = 0; i < accounts.size(); i++)
		{
			Account account = accounts.get(i);
			if (account.getID() == accountID)
			{
				bool = true;
				continue;
			}

			result[i - (bool ? 1 : 0)] = convertAccount(account);
		}

		return result;
	}

	private String[] convertMatches(int accountID, boolean sender)
	{
		List<Match> rs = new ArrayList<Match>();
		for (Integer key : requests.keySet())
		{
			Match request = requests.get(key);

			Account[] opponents = request.getOpponents();
			if (opponents[sender ? 0 : 1].getID() == accountID)
			{
				rs.add(request);
				break;
			}
		}

		String[] result = new String[rs.size()];

		for (int i = 0; i < result.length; i++)
			result[i] = convertMatch(rs.get(i));

		return result;
	}

	private String convertAccount(Account account)
	{
		account.setOnline(isOnline(account.getID()));
		if (!isInMatch(account.getID()))
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

	/**
	 * Starts the Server.
	 * 
	 * @param dataManager
	 *            the IDataManager instance
	 * @param port
	 *            the port of the Server
	 * @return the created Server instance
	 */
	public static Server start(IDataManager dataManager, int port)
	{
		Server server = new Server(dataManager, port);
		System.out.println("Starting Server!");

		server.accountIDs.clear();
		server.clientIDs.clear();
		server.matches.clear();
		server.matchSteps.clear();
		server.requests.clear();

		try
		{
			if (!server.start())
				throw new Exception();
		} catch (Exception e)
		{
			System.out.println("Cannot start Server!");
			return null;
		}

		return server;
	}

	/**
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO TEMP
		new File(Constants.DB_FILE).delete();
		// TODO TEMP

		ServerView serverView = new ServerView();
		IDataManager dataManager = new DataManager();
		serverView.open(dataManager);

		// TODO TEMP
		for (Category c : Category.values())
			for (int i = 0; i < Constants.QUESTION_COUNT + 1; i++)
				dataManager.addQuestion(new Question(c, c.toString() + "-question-" + i, new String[] { "correct", "incorrect-0", "incorrect-1", "incorrect-2" }));

		dataManager.addAccount("1", "1");
		dataManager.addAccount("2", "2");
		// TODO TEMP

		for (Category category : Category.values())
			if (dataManager.getQuestions(category).size() < Constants.QUESTION_COUNT)
			{
				System.out.println("Invalid question count ('" + category.toString() + "')!");
				return;
			}
	}
}