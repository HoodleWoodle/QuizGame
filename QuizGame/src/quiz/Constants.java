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
	 * The path of the data folder.
	 */
	String DATA = "data";
	/**
	 * The count of Questions per Match.
	 */
	int QUESTION_COUNT = 3;
	/**
	 * The score-increase for the winner.
	 */
	int SCORE_WIN = 3;
	/**
	 * The score-increase for each distance.
	 */
	int SCORE_DISTANCE = 1;
	/**
	 * The score-increase for a fool.
	 */
	int SCORE_FOOL = -5;
	/**
	 * The number of Questions per row and player in the GameOverPanel. QUESTION_COUNT must be a multiple of QUESTIONS_PER_ROW.
	 */
	int QUESTIONS_PER_ROW_AND_PLAYER = 3;
	/**
	 * The name of the database.
	 */
	String DB_FILE = DATA + "/quizDB.mv.db";
	/**
	 * The name of the database.
	 */
	String DB_PATH = "jdbc:h2:./" + DATA + "/quizDB";
	/**
	 * The path of the ini file.
	 */
	String INI_FILE = DATA + "/quiz.ini";
	/**
	 * The path of the icon file.
	 */
	String ICON_FILE = DATA + "/icon_image.png";
	/**
	 * The user-name to connect to the database.
	 */
	String DB_USERNAME = "quizMaster";
	/**
	 * The password to connect to the database.
	 */
	String DB_PASSWORD = "qM";
	/**
	 * The LookAndFeel.
	 */
	String LOOK_AND_FEEL = "Nimbus";
	/**
	 * The path to the username file.
	 */
	Path USERNAME_FILE = Paths.get("data").resolve("username.txt");
	/**
	 * Time per answer in seconds.
	 */
	int SECONDS_PER_ANSWER = 20;
	/**
	 * GameFrame's width.
	 */
	int FRAME_WIDTH = 700;
	/**
	 * GameFrame's height.
	 */
	int FRAME_HEIGHT = 700;
	/**
	 * The delay in milliseconds until the next question is displayed.
	 */
	int DELAY_BETWEEN_QUESTIONS = 2000;
}