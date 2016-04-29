package quiz;

/**
 * @author Alex, Eric, Quirin, Stefan
 * @version 29.04.2016
 */
public interface Constants
{
	/**
	 * The count of Questions in on Match.
	 */
	public static final int QUESTION_COUNT = 3;
	/**
	 * The name of the database.
	 */
	public static final String DB_FILE = "quizDB.mv.db";
	/**
	 * The name of the database.
	 */
	public static final String DB_PATH = "jdbc:h2:./quizDB";
	/**
	 * The user-name to connect to the database.
	 */
	public static final String DB_USERNAME = "quizMaster";
	/**
	 * The password to connect to the database.
	 */
	public static final String DB_PASSWORD = "qM";
}