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
public final class ModelManager // implements IModelManager
{
	/**
	 * The database-connection of the ModelManager.
	 */
	private final U_Database db;

	/**
	 * Creates an instance of ModelManager.
	 */
	public ModelManager()
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
		// db.select("SELECT * FROM Questions WHERE category='" + category.ordinal() + "'");

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
		String quest = question.getQuestion();
		if (quest == null || quest.isEmpty() || quest.length() >= 1022)
			// invalid question-string
			return false;

		Category category = question.getCategory();
		if (category == null)
			// invalid category
			return false;
		int cate = category.ordinal();

		String[] answers = question.getAnswers();
		if (answers.length != 4)
			// invalid answers-count
			return false;
		for (String answer : answers)
			if (answer == null || answer.isEmpty() || answers.length >= 254)
				// invalid answer-string
				return false;

		int correct = question.getCorrect();
		if (correct < 0 || correct > 3)
			// invalid correct-answer
			return false;

		return db.insert("INSERT INTO Questions (question, category, a0, a1, a2, a3, correct) VALUES ('" + quest + "', '" + cate + "','" + answers[0] + "','" + answers[1] + "','" + answers[2] + "','" + answers[3] + "','" + correct + "')");
	}

	// @Override
	public Account addAccount(String name, String password)
	{
		if (name == null || name.isEmpty() || name.length() >= 254)
			// invalid name
			return null;

		if (password == null || password.isEmpty() || password.length() >= 254)
			// invalid password
			return null;

		int ID = db.insertReturn("INSERT INTO Accounts (name, password) VALUES ('" + name + "','" + password + "')");
		if (ID == -1)
			// invalid input
			return null;

		return null;
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
	public void refreshAccount(Account account, int score)
	{
	}

	public static void main(String[] args)
	{
		ModelManager manager = new ModelManager();

		manager.addAccount("name", "pswd");
		manager.addAccount("name", "pswd1");
	}
}