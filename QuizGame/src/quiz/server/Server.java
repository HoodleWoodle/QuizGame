package quiz.server;

import static quiz.Constants.DATA;
import static quiz.Constants.DB_FILE;
import static quiz.Constants.SCORE_DISTANCE;
import static quiz.Constants.SCORE_FOOL;
import static quiz.Constants.SCORE_LOSE;
import static quiz.Constants.SCORE_TIE;
import static quiz.Constants.SCORE_WIN;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkKeys.TAG_ALREADY_LOGGED_IN;
import static quiz.net.NetworkKeys.TAG_ALREADY_REQUESTED;
import static quiz.net.NetworkKeys.TAG_INVALID_LOGIN_DETAILS;
import static quiz.net.NetworkKeys.TAG_INVALID_REGISTER_DETAILS;
import static quiz.net.NetworkKeys.TAG_LOGIN;
import static quiz.net.NetworkKeys.TAG_NO_OPPONENTS_AVAILABLE;
import static quiz.net.NetworkKeys.TAG_OPPONENT_DISCONNECTED;
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

import javax.swing.JOptionPane;

import lib.net.tcp.NetworkMessage;
import lib.net.tcp.server.AbstractTCPServer;
import lib.net.tcp.server.ClientThread;
import quiz.Constants;
import quiz.Utils;
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

		// initializing
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
		// there is no white-list or max-client border
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
		// remove from intern data
		Integer accountID = accountIDs.remove(client.getID());
		if (accountID == null) return;
		clientIDs.remove(accountID);

		// if this fool is in a match
		if (isInMatch(accountID))
		{
			Match match = getMatch(accountID);

			endMatch(match, false);

			Account[] opponents = match.getOpponents();
			for (int i = 0; i < opponents.length; i++)
			{
				int otherID = opponents[i].getID();
				if (otherID != accountID) sendTo(otherID, createEmptyMessage(TAG_OPPONENT_DISCONNECTED));
			}
		}

		System.out.println(Utils.replaceWithAccount(Utils.MSG_CLOSED, dataManager.getAccount(accountID)));

		// send opponents to all clients
		sendOpponents();
	}

	private void workRegister(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.addAccount(message.getParameter(0), message.getParameter(1));
		// if this account is available
		if (account != null)
		{
			// add account to intern data
			addAccount(account, client);

			System.out.println(Utils.replaceWithAccount(Utils.MSG_REGISTERED, account));
		}
		// if it is not available
		else client.send(createEmptyMessage(TAG_INVALID_REGISTER_DETAILS).getBytes());
	}

	private void workLogin(ClientThread client, NetworkMessage message)
	{
		Account account = dataManager.getAccount(message.getParameter(0), message.getParameter(1));
		// if account is available
		if (account != null)
		{
			// for (int i = 0; i < 12; i++) // TODO
			// requests.put(i, new Match(i, Category.ENTERTAINMENT, new Account[] { dataManager.getAccount(2), account }, new Question[0], new int[2][0]));

			// add account to intern data
			addAccount(account, client);

			System.out.println(Utils.replaceWithAccount(Utils.MSG_LOGGED_IN, account));
		}
		// if it is not available
		else client.send(createEmptyMessage(TAG_INVALID_LOGIN_DETAILS).getBytes());
	}

	private void workRequest(ClientThread client, NetworkMessage message)
	{
		// get relevant information
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

		// if there cant assigned an opponent
		if (accounts[1] == null)
		{
			client.send(createEmptyMessage(TAG_NO_OPPONENTS_AVAILABLE).getBytes());
			return;
		}

		// if this request is already requested
		if (existsRequest(playerID, accounts[1].getID()))
		{
			client.send(createEmptyMessage(TAG_ALREADY_REQUESTED).getBytes());
			return;
		}

		// create new request
		Match request = new Match(nextMatchID++, category, accounts, new Question[0], new int[2][0]);
		requests.put(request.getID(), request);

		// send sent requests
		sendSentRequests(playerID);

		// send request if possible
		int otherID = request.getOpponents()[1].getID();
		if (isOnline(otherID)) sendRequests(otherID);
	}

	private void workRequestAccept(ClientThread client, NetworkMessage message)
	{
		// get relevant information
		int matchID = Integer.parseInt(message.getParameter(0));
		Match match = requests.get(matchID);

		int playerID = accountIDs.get(client.getID());
		int otherID = match.getOpponents()[0].getID();

		// if the opponent is not available for a match
		if (!isOnline(otherID) || isInMatch(playerID) || isInMatch(otherID))
		{
			client.send(createEmptyMessage(TAG_OPPONENT_NOT_AVAILABLE).getBytes());
			return;
		}

		// create new match
		requests.remove(matchID);
		matches.put(match.getID(), match);

		// send updates
		sendSentRequests(otherID);
		sendRequests(playerID);

		sendOpponents();

		sendMatch(match);
		sendQuestion(match);
	}

	private void workRequestDeny(ClientThread client, NetworkMessage message)
	{
		// get relevant information
		int matchID = Integer.parseInt(message.getParameter(0));
		Match request = requests.remove(matchID);

		// send sent requests an requests
		sendSentRequests(request.getOpponents()[0].getID());
		sendRequests(request.getOpponents()[1].getID());
	}

	private void workSetAnswer(ClientThread client, NetworkMessage message)
	{
		// get relevant information
		int answer = Integer.parseInt(message.getParameter(0));

		int accountID = accountIDs.get(client.getID());
		Match match = getMatch(accountID);
		int matchID = match.getID();
		MatchStep matchStep = matchSteps.get(matchID);

		// set answer to match-step
		matchStep.setAnswer(dataManager.getAccount(accountID), answer);

		// if both opponents has answered
		if (matchStep.isDone())
		{
			// create advanced match
			match = matchStep.editMatch(match);
			matches.put(matchID, match);

			// if match is ended
			if (match.getQuestions().length == Constants.QUESTION_COUNT)
			{
				// end match
				endMatch(match, true);
				// send opponents to all clients
				sendOpponents();
			}
			// send new question
			else sendQuestion(match);

			// send new match
			sendMatch(match);
		}
	}

	private void addAccount(Account account, ClientThread client)
	{
		// get relevant information
		int accountID = account.getID();
		int clientID = client.getID();

		// if account is already logged in
		if (isOnline(accountID))
		{
			client.send(createEmptyMessage(TAG_ALREADY_LOGGED_IN).getBytes());
			return;
		}

		// set intern information
		accountIDs.put(clientID, accountID);
		clientIDs.put(accountID, clientID);

		// send new account
		client.send(new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(account)).getBytes());

		// update account
		sendOpponents();
		sendRequests(accountID);
		sendSentRequests(accountID);
	}

	private void sendMatch(Match match)
	{
		// send a match to the match-opponents
		sendToOpponents(match, new NetworkMessage(TAG_SET_MATCH, convertMatch(match)));
	}

	private void sendQuestion(Match match)
	{
		// add new questions to match
		List<Question> questions = dataManager.getQuestions(match.getCategory());
		Question[] doneQuestions = match.getQuestions();
		for (int i = 0; i < doneQuestions.length; i++)
			questions.remove(doneQuestions[i]);
		Question question = questions.get(random.nextInt(questions.size()));

		// create new match-step
		matchSteps.put(match.getID(), new MatchStep(question));

		// send the new question to the match-opponents
		sendToOpponents(match, new NetworkMessage(TAG_SET_QUESTION, convertQuestion(question)));
	}

	private void sendOpponents()
	{
		// send all opponents to all registerd clients
		for (int i = 0; i < clients.size(); i++)
		{
			ClientThread client = clients.get(i);
			Integer accountID = accountIDs.get(client.getID());
			if (accountID == null) continue;
			client.send(new NetworkMessage(TAG_SET_OPPONENTS, convertAccounts(accountID)).getBytes());
		}
	}

	private void sendRequests(int accountID)
	{
		// send all requests to an account
		sendTo(accountID, new NetworkMessage(TAG_SET_REQUESTS, convertRequests(accountID, false)));
	}

	private void sendSentRequests(int accountID)
	{
		// send all sent requests to an account
		sendTo(accountID, new NetworkMessage(TAG_SET_SENT_REQUESTS, convertRequests(accountID, true)));
	}

	private void sendToOpponents(Match match, NetworkMessage msg)
	{
		// send an message to match-opponents
		Account[] opponents = match.getOpponents();
		sendTo(opponents[0].getID(), msg);
		sendTo(opponents[1].getID(), msg);
	}

	private void sendTo(int accountID, NetworkMessage msg)
	{
		// send a message to an account
		Integer clientID = clientIDs.get(accountID);
		if (clientID != null)
		{
			ClientThread client = getClient(clientIDs.get(accountID));
			if (client != null) client.send(msg.getBytes());
		}
	}

	private NetworkMessage createEmptyMessage(byte tag)
	{
		// creates an empty message
		return new NetworkMessage(tag, new String[0]);
	}

	private boolean isOnline(int accountID)
	{
		// check whether an account is online
		if (clientIDs.get(accountID) == null) return false;
		return true;
	}

	private boolean isInMatch(int accountID)
	{
		// check whether an account is in a match
		return getMatch(accountID) != null;
	}

	private boolean existsRequest(int accountID_0, int accountID_1)
	{
		// check whether an request between two accounts exists
		for (Integer key : requests.keySet())
		{
			Account[] opponents = requests.get(key).getOpponents();

			boolean first = opponents[0].getID() == accountID_0 || opponents[1].getID() == accountID_0;
			boolean second = opponents[0].getID() == accountID_1 || opponents[1].getID() == accountID_1;

			if (first && second) return true;
		}

		return false;
	}

	private void endMatch(Match match, boolean ready)
	{
		// get relevant information
		int matchID = match.getID();
		matches.remove(matchID);
		matchSteps.remove(matchID);
		Account[] opponents = match.getOpponents();
		int accountID = opponents[0].getID();
		int otherID = opponents[1].getID();

		// set the new score
		if (ready)
		{
			updateAccounts(match);
			opponents[0] = dataManager.getAccount(accountID);
			opponents[1] = dataManager.getAccount(otherID);
		}
		else
		{
			opponents[0] = dataManager.updateAccount(opponents[0], opponents[0].getScore() + SCORE_WIN);
			opponents[1] = dataManager.updateAccount(opponents[1], opponents[1].getScore() + SCORE_FOOL);
		}

		// update accounts
		sendTo(accountID, new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(opponents[0])));
		sendTo(otherID, new NetworkMessage(TAG_SET_ACCOUNT, convertAccount(opponents[1])));
	}

	private void updateAccounts(Match match)
	{
		// update accounts score
		Account[] opponents = match.getOpponents();
		int[][] answers = match.getAnswers();
		int[] wins = new int[opponents.length];

		// evaluate win-steps
		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
				wins[i] += answers[i][j] == 0 ? 1 : 0;

		// calculate new score
		for (int i = 0; i < opponents.length; i++)
		{
			int j = (i + 1) % opponents.length;
			if (wins[i] > wins[j]) dataManager.updateAccount(opponents[i], opponents[i].getScore() + SCORE_WIN + (wins[i] - wins[j]) * SCORE_DISTANCE);
			if (wins[i] < wins[j]) dataManager.updateAccount(opponents[i], opponents[i].getScore() + SCORE_LOSE);
			if (wins[i] == wins[j]) dataManager.updateAccount(opponents[i], opponents[i].getScore() + SCORE_TIE);
		}
	}

	private Match getMatch(int accountID)
	{
		// returns the match of an account
		for (Integer key : matches.keySet())
		{
			Match match = matches.get(key);
			Account[] opponents = match.getOpponents();
			for (int i = 0; i < opponents.length; i++)
				if (opponents[i].getID() == accountID) return match;
		}

		return null;
	}

	private Account getRandomAccount(int accountID, List<Account> possibles)
	{
		// return random available account
		int size = possibles.size();
		if (size == 0) return null;

		int rand = random.nextInt(size);
		Account account = possibles.remove(rand);

		int otherID = account.getID();
		if (otherID == accountID || existsRequest(otherID, accountID) || !isOnline(otherID)) return getRandomAccount(accountID, possibles);

		return account;
	}

	private Category getRandomCategory()
	{
		// return random category
		Category[] categories = Category.values();
		int rand = random.nextInt(categories.length);
		return categories[rand];
	}

	private Category getCategory(int categoryID)
	{
		// return category by ID
		Category[] values = Category.values();
		if (categoryID > values.length) return null;

		return values[categoryID];
	}

	private String[] convertAccounts(int accountID)
	{
		// convert some accounts
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

	private String[] convertRequests(int accountID, boolean sender)
	{
		// convert all relevant requests
		List<Match> rs = new ArrayList<Match>();
		for (Integer key : requests.keySet())
		{
			Match request = requests.get(key);

			Account[] opponents = request.getOpponents();
			if (opponents[sender ? 0 : 1].getID() == accountID) rs.add(request);
		}

		String[] result = new String[rs.size()];

		for (int i = 0; i < result.length; i++)
			result[i] = convertMatch(rs.get(i));

		return result;
	}

	private String convertAccount(Account account)
	{
		// convert an account
		account.setOnline(isOnline(account.getID()));
		account.setAvailable(!isInMatch(account.getID()));

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
		// convert a match
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
			if (i < opponents.length - 1 || questions.length > 0) builder.append(SPLIT_SUB_SUB);
		}

		int[][] answers = match.getAnswers();

		for (int i = 0; i < questions.length; i++)
		{
			builder.append(convertQuestion(questions[i]));
			if (answers[0].length > 0) builder.append(SPLIT_SUB_SUB);
		}

		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
			{
				builder.append(answers[i][j]);
				if (j + i * answers[0].length < answers.length * answers[0].length - 1) builder.append(SPLIT_SUB_SUB);
			}

		return builder.toString();
	}

	private String convertQuestion(Question question)
	{
		// convert a question
		StringBuilder builder = new StringBuilder();

		builder.append(question.getCategory().ordinal());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(question.getQuestion());
		builder.append(SPLIT_SUB_SUB_SUB);
		builder.append(question.getImage());
		builder.append(SPLIT_SUB_SUB_SUB);

		String[] answers = question.getAnswers();
		for (int i = 0; i < answers.length; i++)
		{
			builder.append(answers[i]);
			if (i < answers.length - 1) builder.append(SPLIT_SUB_SUB_SUB);
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
		// (re)start the server
		Server server = new Server(dataManager, port);
		System.out.println(Utils.MSG_SERVER_STARTING);

		// clear intern data
		server.accountIDs.clear();
		server.clientIDs.clear();
		server.matches.clear();
		server.matchSteps.clear();
		server.requests.clear();

		// try to start server
		try
		{
			if (!server.start()) throw new Exception();
		} catch (Exception e)
		{
			System.out.println(Utils.ERROR_SERVER_STARTING);
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
		Utils.initalizeLAF();

		// TODO TEMP
		new File(Constants.DB_FILE).delete();
		// TODO TEMP

		ServerView serverView = new ServerView();
		IDataManager dataManager = new DataManager(DATA, DB_FILE, true);
		serverView.open(dataManager);

		// TODO TEMP
		for (Category c : Category.values())
			for (int i = 0; i < 9; i++)
			{
				String img = i < 7 ? "e" + i + ".jpg" : "e7.png";
				if (i == 8) img = "";
				dataManager.addQuestion(new Question(c, c.toString() + "_question_" + i, img, new String[] { "correct", "incorrect_0", "incorrect_1", "incorrect_2" }));
			}

		dataManager.addAccount("1", "1");
		dataManager.addAccount("2", "2");
		// TODO TEMP

		for (Category category : Category.values())
			if (dataManager.getQuestions(category).size() < Constants.QUESTION_COUNT)
			{
				JOptionPane.showMessageDialog(null, Utils.ERROR_QUESTION_COUNT.replace("<C>", category.toString()));
				System.exit(1);
			}
	}
}