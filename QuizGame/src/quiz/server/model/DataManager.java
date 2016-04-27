package quiz.server.model;

import static quiz.Constants.DB_FILE;
import static quiz.Constants.DB_PASSWORD;
import static quiz.Constants.DB_PATH;
import static quiz.Constants.DB_USERNAME;

import java.io.File;

import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Question;
import utils.db.U_Database;

/**
 * @author Stefan
 */
public final class DataManager // implements IModelManager
{
	/**
	 * The database-connection of the DataManager.
	 */
	private final U_Database db;

	/**
	 * Creates an instance of DataManager.
	 */
	public DataManager()
	{
		db = new U_Database(DB_PATH, DB_USERNAME, DB_PASSWORD);

		boolean create = false;
		if (!new File(DB_FILE).exists())
			// if database does not exist activate creating tables
			create = true;

		// connect to database
		if (!db.connect())
			// if connecting fails
			// shut down process
			System.exit(1);

		if (create)
			// create database tables
			create();
	}

	/**
	 * Creates the database tables.
	 */
	private void create()
	{
		// create Accounts-table
		db.insert("CREATE TABLE Accounts (ID INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, score INTEGER DEFAULT '0' NOT NULL)");
		// create Questions-table
		db.insert("CREATE TABLE Questions (question VARCHAR(1023) NOT NULL UNIQUE, category TINYINT NOT NULL, a0 VARCHAR(255) NOT NULL, a1 VARCHAR(255) NOT NULL, a2 VARCHAR(255) NOT NULL, a3 VARCHAR(255) NOT NULL, correct TINYINT NOT NULL)");
	}

	// @Override
	public Question getQuestion(Category category)
	{
		// db.select("SELECT * FROM Questions WHERE category='" +
		// category.ordinal() + "'");

		return null;
	}

	// @Override
	public int getQuestionCount()
	{
		return -1;
	}

	// @Override
	public Account getAccount(String name, String password)
	{
		return null;
	}

	// @Override
	public Account[] getAccounts()
	{
		return null;
	}

	// @Override
	public int getAccountCount()
	{
		return -1;
	}

	// @Override
	public boolean addQuestion(Question question)
	{
		// get data from Question-object
		String quest = question.getQuestion();
		Category category = question.getCategory();
		String[] answers = question.getAnswers();
		int correct = question.getCorrect();

		// check question-string
		if (!check(quest, 1023))
			return false;
		// check category
		if (category == null)
			return false;
		// check answer-strings
		if (answers.length != 4)
			return false;
		for (String answer : answers)
			if (!check(answer))
				return false;
		// check correct
		if (correct < 0 || correct > 3)
			return false;

		// insert question into database
		if (!db.insert("INSERT INTO Questions (question, category, a0, a1, a2, a3, correct) VALUES ('" + quest + "', '" + category.ordinal() + "','" + answers[0] + "','" + answers[1] + "','" + answers[2] + "','" + answers[3] + "','" + correct + "')"))
			return false; // TODO

		return true;
	}

	// @Override
	public Account addAccount(String name, String password)
	{
		// check name and password
		if (!check(name) || !check(password))
			return null;

		int ID = db.insertReturn("INSERT INTO Accounts (name, password) VALUES ('" + name + "','" + password + "')");
		if (ID == -1)
			return null; // TODO

		return new Account(ID, name, password, 0);
	}

	// @Override
	public void removeQuestion(Question question)
	{
	}

	// @Override
	public void removeAccount(Account account)
	{
	}

	// @Override
	public void updateAccount(Account account, int score)
	{
	}

	// @Override
	public void close()
	{
		if (!db.close())
			; // TODO
	}

	private boolean check(String toCheck)
	{
		return check(toCheck, 255);
	}

	private boolean check(String toCheck, int maxLength)
	{
		// check whether string is valid for database
		if (toCheck == null || toCheck.isEmpty() || toCheck.length() > maxLength)
			// invalid string
			return false;
		// valid string
		return true;
	}

	public static void main(String[] args)
	{
		DataManager manager = new DataManager();

		String[] answers = { "a", "b", "c", "d" };
		manager.addQuestion(new Question(Category.X, "q0", answers, 0));
		manager.addQuestion(new Question(Category.X, "q1", answers, 0));
		manager.addAccount("n0", "p");
		manager.addAccount("n1", "p");

		manager.close();
	}
}