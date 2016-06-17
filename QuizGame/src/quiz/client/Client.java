package quiz.client;

import static quiz.net.NetworkMessage.SPLIT_SUB;
import static quiz.net.NetworkMessage.SPLIT_SUB_SUB;
import static quiz.net.NetworkMessage.SPLIT_SUB_SUB_SUB;
import static quiz.net.NetworkMessage.TAG_SET_ACCOUNT;
import static quiz.net.NetworkMessage.TAG_SET_MATCH;
import static quiz.net.NetworkMessage.TAG_SET_OPPONENTS;
import static quiz.net.NetworkMessage.TAG_SET_QUESTION;
import static quiz.net.NetworkMessage.TAG_SET_REQUESTS;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import lib.net.tcp.client.AbstractTCPClient;
import quiz.Constants;
import quiz.client.model.IModel;
import quiz.client.model.Model;
import quiz.client.view.GameFrame;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;
import quiz.net.NetworkMessage;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public class Client extends AbstractTCPClient // TODO eigener Thread
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
		NetworkMessage msg = new NetworkMessage(message);

		Model model = (Model) this.model;

		switch (msg.getTag())
		{
		case TAG_SET_ACCOUNT:
			model.setAccount(parseAccount(msg.getParameter(0)));
			break;
		case TAG_SET_OPPONENTS:
			model.setOpponents(parseAccounts(msg));
			break;
		case TAG_SET_REQUESTS:
			model.setRequests(parseMatches(msg));
			break;
		case TAG_SET_MATCH:
			model.setMatch(parseMatch(msg.getParameter(0)));
			break;
		case TAG_SET_QUESTION:
			model.setQuestion(parseQuestion(msg.getParameter(0)));
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
		String[] data = parameter.split(SPLIT_SUB_SUB);

		int ID = Integer.parseInt(data[0]);
		String name = data[1];
		int score = Integer.parseInt(data[2]);

		boolean online = Boolean.parseBoolean(data[3]);
		boolean available = Boolean.parseBoolean(data[4]);

		Account account = new Account(ID, name, score);
		account.setOnline(online);
		account.setAvailable(available);

		return account;
	}

	private Match parseMatch(String parameter)
	{
		String[] data = parameter.split(SPLIT_SUB);

		int ID = Integer.parseInt(data[0]);
		Category category = getCategory(Integer.parseInt(data[1]));

		String[] sub = data[2].split(SPLIT_SUB_SUB);
		Account[] opponents = new Account[sub.length];
		for (int i = 0; i < opponents.length; i++)
			opponents[i] = parseAccount(data[2]);

		sub = data[3].split(SPLIT_SUB_SUB);
		Question[] questions = new Question[sub.length];
		for (int i = 0; i < questions.length; i++)
			questions[i] = parseQuestion(sub[i]);

		sub = data[3].split(SPLIT_SUB_SUB);
		int[][] answers = new int[opponents.length][questions.length];
		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
				answers[i][j] = Integer.parseInt(sub[i + j]);

		return new Match(ID, category, opponents, questions, answers);
	}

	private Question parseQuestion(String parameter)
	{
		String[] data = parameter.split(SPLIT_SUB_SUB_SUB);

		Category category = getCategory(Integer.parseInt(data[0]));
		String question = data[1];
		String[] answers = new String[data.length - 2];
		for (int i = 2; i < answers.length; i++)
			answers[i] = data[2 + i];

		return new Question(category, question, answers);
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
	public static void main(String args[])
	{
		// try to connect to Server
		Client client = new Client("localhost", 5555); // TODO
		boolean con = client.connect();

		IModel model = client.model;
		IControl control = new Control(client);

		// initialize LookAndFeel
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if (Constants.LOOK_AND_FEEL.equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e)
		{
			con = false;
		}

		// some Exception
		if (con == false)
		{
			JOptionPane.showMessageDialog(null, "Cannot start Client!");
			System.exit(1);
		}

		// start surface
		SwingUtilities.invokeLater(() -> {
			// Swing needs to run on event dispatching thread
				new GameFrame(control, model);
			});
	}
}