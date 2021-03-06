package quiz.client;

import static quiz.client.model.Status.ALREADY_LOGGED_IN;
import static quiz.client.model.Status.ALREADY_REQUESTED;
import static quiz.client.model.Status.INVALID_LOGIN_DETAILS;
import static quiz.client.model.Status.INVALID_REGISTER_DETAILS;
import static quiz.client.model.Status.NO_OPPONENTS_AVAILABLE;
import static quiz.client.model.Status.OPPONENT_DISCONNECTED;
import static quiz.client.model.Status.OPPONENT_NOT_AVAILABLE;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB;
import static quiz.net.NetworkKeys.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkKeys.TAG_ALREADY_LOGGED_IN;
import static quiz.net.NetworkKeys.TAG_ALREADY_REQUESTED;
import static quiz.net.NetworkKeys.TAG_INVALID_LOGIN_DETAILS;
import static quiz.net.NetworkKeys.TAG_INVALID_REGISTER_DETAILS;
import static quiz.net.NetworkKeys.TAG_NO_OPPONENTS_AVAILABLE;
import static quiz.net.NetworkKeys.TAG_OPPONENT_DISCONNECTED;
import static quiz.net.NetworkKeys.TAG_OPPONENT_NOT_AVAILABLE;
import static quiz.net.NetworkKeys.TAG_SET_ACCOUNT;
import static quiz.net.NetworkKeys.TAG_SET_MATCH;
import static quiz.net.NetworkKeys.TAG_SET_OPPONENTS;
import static quiz.net.NetworkKeys.TAG_SET_QUESTION;
import static quiz.net.NetworkKeys.TAG_SET_REQUESTS;
import static quiz.net.NetworkKeys.TAG_SET_SENT_REQUESTS;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import lib.net.tcp.NetworkMessage;
import lib.net.tcp.client.AbstractTCPClient;
import quiz.Constants;
import quiz.Utils;
import quiz.client.model.IModel;
import quiz.client.model.Model;
import quiz.client.model.Status;
import quiz.client.view.GameFrame;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public final class Client extends AbstractTCPClient
{
	private final IModel model;

	/**
	 * Creates an instance of Client.
	 * 
	 * @param server
	 *            the server-name
	 * @param port
	 *            the server-port
	 */
	public Client(String server, int port)
	{
		super(server, port);
		this.model = new Model();
	}

	@Override
	protected void received(byte[] message)
	{
		// parse message and set it
		NetworkMessage msg = new NetworkMessage(message);

		Model model = (Model) this.model;

		model.setStatus(Status.SUCCESS);
		switch (msg.getTag())
		{
		case TAG_INVALID_REGISTER_DETAILS:
			model.setStatus(INVALID_REGISTER_DETAILS);
			model.setAccount(null);
			break;
		case TAG_INVALID_LOGIN_DETAILS:
			model.setStatus(INVALID_LOGIN_DETAILS);
			model.setAccount(null);
			break;
		case TAG_NO_OPPONENTS_AVAILABLE:
			model.setStatus(NO_OPPONENTS_AVAILABLE);
			model.setRequests(model.getRequests());
			break;
		case TAG_OPPONENT_NOT_AVAILABLE:
			model.setStatus(OPPONENT_NOT_AVAILABLE);
			model.setRequests(model.getRequests());
			break;
		case TAG_OPPONENT_DISCONNECTED:
			model.setStatus(OPPONENT_DISCONNECTED);
			model.setMatch(model.getMatch());
			break;
		case TAG_ALREADY_LOGGED_IN:
			model.setStatus(ALREADY_LOGGED_IN);
			model.setAccount(null);
			break;
		case TAG_ALREADY_REQUESTED:
			model.setStatus(ALREADY_REQUESTED);
			model.setRequests(model.getRequests());
			break;
		case TAG_SET_ACCOUNT:
			model.setAccount(parseAccount(msg.getParameter(0)));
			break;
		case TAG_SET_MATCH:
			model.setMatch(parseMatch(msg.getParameter(0)));
			break;
		case TAG_SET_QUESTION:
			model.setQuestion(parseQuestion(msg.getParameter(0)));
			break;
		case TAG_SET_OPPONENTS:
			model.setOpponents(parseAccounts(msg));
			break;
		case TAG_SET_REQUESTS:
			model.setRequests(parseMatches(msg));
			break;
		case TAG_SET_SENT_REQUESTS:
			model.setSentRequests(parseMatches(msg));
			break;
		}
	}

	@Override
	protected void closed()
	{
	}

	private Account[] parseAccounts(NetworkMessage message)
	{
		Account[] accounts = new Account[message.getParameterCount()];

		for (int i = 0; i < accounts.length; i++)
			accounts[i] = parseAccount(message.getParameter(i));

		return accounts;
	}

	private Match[] parseMatches(NetworkMessage message)
	{
		Match[] matches = new Match[message.getParameterCount()];

		for (int i = 0; i < matches.length; i++)
			matches[i] = parseMatch(message.getParameter(i));

		return matches;
	}

	private Account parseAccount(String parameter)
	{
		String[] data = parameter.split(SPLIT_SUB_SUB_SUB);
		int index = 0;

		int ID = Integer.parseInt(data[index++]);
		String name = data[index++];
		int score = Integer.parseInt(data[index++]);

		boolean online = Boolean.parseBoolean(data[index++]);
		boolean available = Boolean.parseBoolean(data[index++]);

		Account account = new Account(ID, name, score);
		account.setOnline(online);
		account.setAvailable(available);

		return account;
	}

	private Match parseMatch(String parameter)
	{
		String[] data = parameter.split(SPLIT_SUB_SUB);
		int index = 0;

		int ID = Integer.parseInt(data[index++]);
		Category category = getCategory(Integer.parseInt(data[index++]));

		Account[] opponents = new Account[2];
		for (int i = 0; i < opponents.length; i++)
			opponents[i] = parseAccount(data[index++]);

		Question[] questions = new Question[(data.length - index) / 3];
		for (int i = 0; i < questions.length; i++)
			questions[i] = parseQuestion(data[index++]);

		int[][] answers = new int[opponents.length][questions.length];
		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
				answers[i][j] = Integer.parseInt(data[index++]);

		return new Match(ID, category, opponents, questions, answers);
	}

	private Question parseQuestion(String parameter)
	{
		String[] data = parameter.split(SPLIT_SUB_SUB_SUB);
		int index = 0;

		Category category = getCategory(Integer.parseInt(data[index++]));
		String question = data[index++];
		String image = data[index].isEmpty() ? null : data[index];
		index++;
		String[] answers = new String[data.length - 3];
		for (int i = 0; i < answers.length; i++)
			answers[i] = data[index++];

		return new Question(category, question, image, answers);
	}

	private Category getCategory(int ID)
	{
		Category[] values = Category.values();
		if (ID > values.length) return null;

		return values[ID];
	}

	/**
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		String server = null;
		int port = -1;
		try
		{
			Properties ini = new Properties();
			ini.load(new FileReader(new File(Constants.INI_FILE)));

			server = ini.getProperty("server");
			port = Integer.parseInt(ini.getProperty("port"));
		} catch (Exception e)
		{
			server = Constants.DEFAULT_SERVER;
			port = Constants.DEFAULT_PORT;
		}

		Client client = new Client(server, port);
		boolean con = client.connect();

		IModel model = client.model;
		IControl control = new Control(client);

		Utils.initalizeLAF();

		if (con == false)
		{
			JOptionPane.showMessageDialog(null, Utils.ERROR_CLIENT_STARTING);
			System.exit(1);
		}

		SwingUtilities.invokeLater(() -> {
			new GameFrame(client, control, model);
		});
	}
}