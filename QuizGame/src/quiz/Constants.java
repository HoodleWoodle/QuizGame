package quiz;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alex, Eric, Quirin, Stefan
 * @version 29.04.2016
 */
public interface Constants
{
	/**
	 * The count of Questions per Match.
	 */
	public static final int QUESTION_COUNT = 3;
	/**
	 * The score-increase for the winner.
	 */
	public static final int SCORE_WIN = 3;
	/**
	 * The score-increase for each distance.
	 */
	public static final int SCORE_DISTANCE = 1;
	/**
	 * The score-increase for a fool.
	 */
	public static final int SCORE_FOOL = -5;
	/**
	 * The number of Questions per row and player in the GameOverPanel. QUESTION_COUNT must be a multiple of QUESTIONS_PER_ROW.
	 */
	public static final int QUESTIONS_PER_ROW_AND_PLAYER = 3;
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
	/**
	 * The LookAndFeel.
	 */
	public static final String LOOK_AND_FEEL = "Nimbus";
	/**
	 * The path to the username file.
	 */
	public static final Path USERNAME_FILE = Paths.get("data").resolve("username.txt");
	/**
	 * Time per answer in seconds.
	 */
	public static final int SECONDS_PER_ANSWER = 20;
	/**
	 * GameFrame's width.
	 */
	public static final int FRAME_WIDTH = 700;
	/**
	 * GameFrame's height.
	 */
	public static final int FRAME_HEIGHT = 700;
}