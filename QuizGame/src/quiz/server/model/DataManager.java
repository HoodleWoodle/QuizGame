package quiz.server.model;

import static quiz.Constants.DB_FILE;
import static quiz.Constants.DB_PASSWORD;
import static quiz.Constants.DB_PATH;
import static quiz.Constants.DB_USERNAME;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Question;
import utils.db.U_Database;

/**
 * @author Stefan
 */
public final class DataManager // implements IDataManager
{
	/**
	 * The name of the Accounts-table.
	 */
	private static final String TABLE_ACCOUNTS = "accounts";
	/**
	 * The name of the Questions-table.
	 */
	private static final String TABLE_QUESTIONS = "questions";

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
		db.insert("CREATE TABLE " + TABLE_ACCOUNTS + " (ID INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, score INTEGER DEFAULT '0' NOT NULL)");
		// create Questions-table
		db.insert("CREATE TABLE " + TABLE_QUESTIONS + " (category TINYINT NOT NULL, question VARCHAR(1023) NOT NULL UNIQUE, a0 VARCHAR(255) NOT NULL, a1 VARCHAR(255) NOT NULL, a2 VARCHAR(255) NOT NULL, a3 VARCHAR(255) NOT NULL, correct TINYINT NOT NULL)");
	}

	// @Override
	public List<Question> getQuestions(Category category)
	{
		// check category
		if (category == null)
			return null;

		// select Questions
		ResultSet result = db.select("SELECT * FROM " + TABLE_QUESTIONS + " WHERE category='" + category.ordinal() + "'");

		// get Questions
		return getQuestions(result);
	}

	// @Override
	public List<Question> getQuestions()
	{
		// select all Questions
		ResultSet result = db.select("SELECT * FROM " + TABLE_QUESTIONS);

		// get Questions
		return getQuestions(result);
	}

	// @Override
	public int getQuestionCount()
	{
		// get count
		return getCount(TABLE_QUESTIONS);
	}

	// @Override
	public Account getAccount(String name, String password)
	{
		// select Account
		ResultSet result = db.select("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE name='" + name + "' AND password='" + password + "'");

		try
		{
			// go to next row
			if (!result.next())
				// there is no row
				return null;
		} catch (SQLException e)
		{
			// some Exception
			e.printStackTrace();
			return null; // TODO
		}

		// get Account
		return getAccount(result);
	}

	// @Override
	public List<Account> getAccounts()
	{
		// select all Accounts
		ResultSet result = db.select("SELECT * FROM " + TABLE_ACCOUNTS);

		try
		{
			List<Account> accounts = new ArrayList<Account>();

			// loop through all rows
			while (result.next())
			{
				// get Account
				Account account = getAccount(result);
				if (account != null)
					accounts.add(account);
				else
					; // TODO
			}

			return accounts;
		} catch (SQLException e)
		{
			// some Exception
			e.printStackTrace();
			return null; // TODO
		}
	}

	// @Override
	public int getAccountCount()
	{
		// get count
		return getCount(TABLE_ACCOUNTS);
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
		if (!db.insert("INSERT INTO " + TABLE_QUESTIONS + " (question, category, a0, a1, a2, a3, correct) VALUES ('" + quest + "', '" + category.ordinal() + "','" + answers[0] + "','" + answers[1] + "','" + answers[2] + "','" + answers[3] + "','" + correct + "')"))
			return false; // TODO

		return true;
	}

	// @Override
	public Account addAccount(String name, String password)
	{
		// check name and password
		if (!check(name) || !check(password))
			return null;

		int ID = db.insertReturn("INSERT INTO " + TABLE_ACCOUNTS + " (name, password) VALUES ('" + name + "','" + password + "')");
		if (ID == -1)
			return null; // TODO

		return new Account(ID, name, password, 0);
	}

	// @Override
	public void removeQuestion(Question question)
	{
		if (!db.insert("DELETE FROM " + TABLE_QUESTIONS + " WHERE question='" + question.getQuestion() + "'"))
			; // TODO
	}

	// @Override
	public void removeAccount(Account account)
	{
		if (!db.insert("DELETE FROM " + TABLE_ACCOUNTS + " WHERE ID='" + account.getID() + "'"))
			; // TODO
	}

	// @Override
	public void updateAccount(Account account, int score)
	{
		if (!db.insert("UPDATE " + TABLE_ACCOUNTS + " SET score='" + score + "' WHERE ID='" + account.getID() + "'"))
			; // TODO
	}

	// @Override
	public void close()
	{
		if (!db.close())
			; // TODO
	}

	/**
	 * Returns Questions from a resultSet.
	 * 
	 * @param result
	 *            the resultSet
	 * @return the Questions (Exception: null)
	 */
	private List<Question> getQuestions(ResultSet result)
	{
		try
		{
			List<Question> questions = new ArrayList<Question>();

			// loop through all rows
			while (result.next())
			{
				// add Question
				String[] answers = new String[4];
				for (int a = 0; a < answers.length; a++)
					answers[a] = result.getString(3 + a);
				questions.add(new Question(Category.getCategory(result.getInt(1)), result.getString(2), answers, result.getInt(7)));
			}

			return questions;
		} catch (SQLException e)
		{
			// some Exception
			e.printStackTrace();
			return null; // TODO
		}
	}

	/**
	 * Returns an Account from a resultSet.
	 * 
	 * @param result
	 *            the resultSet
	 * @return the Account (Exception: null)
	 */
	private Account getAccount(ResultSet result)
	{
		try
		{
			// get Account
			return new Account(result.getInt(1), result.getString(2), result.getString(3), result.getInt(4));
		} catch (SQLException e)
		{
			// some Exception
			e.printStackTrace();
			return null; // TODO
		}
	}

	/**
	 * Returns the count of rows from a table
	 * 
	 * @param table
	 *            the table
	 * @return the count (Exception: -1)
	 */
	private int getCount(String table)
	{
		// select count
		ResultSet result = db.select("SELECT COUNT(*) AS count FROM " + table);

		try
		{
			// get count
			if (result.next())
				return result.getInt("count");
		} catch (SQLException e)
		{
			// some Exception
			e.printStackTrace();
		}

		return -1; // TODO
	}

	/**
	 * Checks whether a string is valid for database. (max-length: 255)
	 * 
	 * @param string
	 *            the string to check
	 * @return whether a string is valid for database
	 */
	private boolean check(String string)
	{
		// check string
		return check(string, 255);
	}

	/**
	 * Checks whether a string is valid for database.
	 * 
	 * @param string
	 *            the string to check
	 * @param maxLength
	 *            the max-length of the string
	 * @return whether a string is valid for database
	 */
	private boolean check(String string, int maxLength)
	{
		// check whether string is valid for database
		if (string == null || string.isEmpty() || string.length() > maxLength)
			// invalid string
			return false;
		// valid string
		return true;
	}
}