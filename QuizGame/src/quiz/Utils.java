package quiz;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import quiz.model.Account;
import quiz.net.NetworkKeys;

/**
 * @author Stefan
 * @version 14.07.2016
 */
@SuppressWarnings("javadoc")
public final class Utils
{
	public static final String TITLE_EDITOR = "QuizGame - Editor";
	public static final String TITLE_SERVER = "QuizGame - Server";
	public static final String INFO_SERVER = "QuizGame is coded by  : '(Alex, Quirin,) Eric, Stefan'\nQuestions are created by : 'unknown Guys'\nIf the Server is closed, Matches and Match-Requests are NOT saved. Only Accounts and their score will be available after restarting!";
	public static final String MSG_DB_CONNECTING = "Connecting to database...";
	public static final String MSG_DB_CLOSING = "Database closed";
	public static final String MSG_SERVER_STARTING = "Starting Server...";
	public static final String MSG_SERVER_CLOSED = "Server closed!";
	public static final String MSG_CLOSED = "Account disconnected. (<N>[<ID>])";
	public static final String MSG_REGISTERED = "Account registered. (<N>[<ID>])";
	public static final String MSG_LOGGED_IN = "Account logged in. (<N>[<ID>])";
	public static final String ERROR_DB_CONNECTING = "Cannot connect to database!";
	public static final String ERROR_DB_CLOSING = "Cannot close database!";
	public static final String ERROR_SERVER_STARTING = "Cannot start server!";
	public static final String ERROR_CLIENT_STARTING = "Cannot start client!";
	public static final String ERROR_QUESTION_COUNT = "Invalid question count ('<C>')!";

	/**
	 * Checks whether a string contains a character which is invalid for net-communication.
	 * 
	 * @param string
	 *            the string to check
	 * @return whether the string contains a character which is invalid for net-communication
	 */
	public static boolean checkString(String string)
	{
		if (string.contains(";")) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB)) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB)) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB_SUB)) return false;
		return true;
	}

	/**
	 * Initializes the surfaces Look And Feel.
	 */
	public static void initalizeLAF()
	{
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				if (Constants.LOOK_AND_FEEL.equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
		} catch (Exception e)
		{
		}
	}

	public static String replaceWithAccount(String string, Account account)
	{
		return string.replaceAll("<N>", account.getName()).replaceAll("<ID>", "" + account.getID());
	}
}