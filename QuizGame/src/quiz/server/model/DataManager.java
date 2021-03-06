package quiz.server.model;

import static quiz.Constants.DB_PASSWORD;
import static quiz.Constants.DB_USERNAME;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import quiz.Constants;
import quiz.Utils;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Question;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class DataManager implements IDataManager
{
	private static final String TABLE_ACCOUNTS = "accounts";
	private static final String TABLE_QUESTIONS = "questions";

	private final Database db;

	/**
	 * Creates an instance of DataManager.
	 * 
	 * @param path
	 *            the path of the database
	 * @param file
	 *            the file of the database
	 * @param relative
	 *            whether the path is relative
	 */
	public DataManager(String path, String file, boolean relative)
	{
		String database = null;
		if (relative) database = "jdbc:h2:./";
		else database = "jdbc:h2:";
		database += path + "/" + Constants.DB_NAME_SIMPLE;

		db = new Database(database, DB_USERNAME, DB_PASSWORD);

		File data = new File(path);
		if (!data.exists()) data.mkdirs();

		boolean create = false;
		if (!new File(file).exists())
			// if database does not exist activate tables creating
			create = true;

		// connect to database
		if (!db.connect())
		{
			// if connecting fails
			JOptionPane.showMessageDialog(null, Utils.ERROR_DB_CONNECTING);
			// shut down process
			System.exit(1);
		}

		if (create)
			// create database tables
			create();
	}

	/**
	 * Creates the database tables.
	 */
	private void create()
	{
		System.out.println("Creating database!");

		// create Accounts-table
		db.insert("CREATE TABLE " + TABLE_ACCOUNTS + " (ID INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, score INTEGER DEFAULT '0' NOT NULL)");
		// create Questions-table
		db.insert("CREATE TABLE " + TABLE_QUESTIONS + " (category TINYINT NOT NULL, question VARCHAR(1023) NOT NULL UNIQUE, image VARCHAR(255) NOT NULL, correct VARCHAR(255) NOT NULL, answer1 VARCHAR(255) NOT NULL, answer2 VARCHAR(255) NOT NULL, answer3 VARCHAR(255) NOT NULL)");
	}

	@Override
	public synchronized List<Question> getQuestions(Category category)
	{
		// check category
		if (category == null) return null;

		// select Questions
		ResultSet result = db.select("SELECT * FROM " + TABLE_QUESTIONS + " WHERE category='" + category.ordinal() + "'");

		// get Questions
		return getQuestions(result);
	}

	@Override
	public synchronized List<Question> getQuestions()
	{
		// select all Questions
		ResultSet result = db.select("SELECT * FROM " + TABLE_QUESTIONS);

		// get Questions
		return getQuestions(result);
	}

	/**
	 * Returns a Question by its question-string.
	 * 
	 * @param question
	 *            the question-string
	 * @return the desired Question (Exception: null)
	 */
	public synchronized Question getQuestion(String question)
	{
		// select all Questions
		ResultSet result = db.select("SELECT * FROM " + TABLE_QUESTIONS + " WHERE question='" + question + "'");

		// get Question
		List<Question> questions = getQuestions(result);
		if (questions == null) return null;
		return questions.get(0);
	}

	@Override
	public synchronized int getQuestionCount()
	{
		// get count
		return getCount(TABLE_QUESTIONS);
	}

	@Override
	public synchronized Account getAccount(String name, String password)
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
			log_err(0);
			// e.printStackTrace();
			return null;
		}

		// get Account
		return getAccount(result);
	}

	@Override
	public Account getAccount(int ID)
	{
		// get all Accounts
		List<Account> accounts = getAccounts();

		// search Account
		for (Account account : accounts)
			if (account.getID() == ID) return account;

		log_err(1);
		return null;
	}

	@Override
	public synchronized List<Account> getAccounts()
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
				if (account != null) accounts.add(account);
				else log_err(2);
			}

			return accounts;
		} catch (SQLException e)
		{
			// some Exception
			log_err(3);
			// e.printStackTrace();
			return null;
		}
	}

	@Override
	public synchronized int getAccountCount()
	{
		// get count
		return getCount(TABLE_ACCOUNTS);
	}

	@Override
	public synchronized boolean addQuestion(Question question)
	{
		// get data from Question-object
		String quest = question.getQuestion();
		String image = question.getImage();
		Category category = question.getCategory();
		String[] answers = question.getAnswers();

		// check question-string
		if (!check(quest, 1023)) return false;
		// check category
		if (category == null) return false;
		// check image-name
		if (image == null || image.length() > 255) return false;
		// check answer-strings
		if (answers.length != 4) return false;
		for (String answer : answers)
			if (!check(answer)) return false;

		// insert question into database
		if (!db.insert("INSERT INTO " + TABLE_QUESTIONS + " (category, question, image, correct, answer1, answer2, answer3) VALUES ('" + category.ordinal() + "', '" + quest + "', '" + image + "', '" + answers[0] + "', '" + answers[1] + "', '" + answers[2] + "', '" + answers[3] + "')"))
		{
			log_err(4);
			return false;
		}

		return true;
	}

	@Override
	public synchronized Account addAccount(String name, String password)
	{
		// check name and password
		if (!check(name) || !check(password)) return null;

		int ID = db.insertReturn("INSERT INTO " + TABLE_ACCOUNTS + " (name, password) VALUES ('" + name + "', '" + password + "')");
		if (ID == -1)
		{
			log_err(5);
			return null;
		}

		return new Account(ID, name, password, 0);
	}

	@Override
	public synchronized void removeQuestion(Question question)
	{
		if (!db.insert("DELETE FROM " + TABLE_QUESTIONS + " WHERE question='" + question.getQuestion() + "'")) log_err(6);
	}

	@Override
	public synchronized void removeAccount(Account account)
	{
		if (!db.insert("DELETE FROM " + TABLE_ACCOUNTS + " WHERE ID='" + account.getID() + "'")) log_err(7);
	}

	@Override
	public synchronized Account updateAccount(Account account, int score)
	{
		if (!db.insert("UPDATE " + TABLE_ACCOUNTS + " SET score='" + score + "' WHERE ID='" + account.getID() + "'")) log_err(8);
		return new Account(account.getID(), account.getName(), score);
	}

	@Override
	public synchronized void close()
	{
		if (!db.close()) log_err(9);
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
				for (int answer = 0; answer < answers.length; answer++)
					answers[answer] = result.getString(4 + answer);
				questions.add(new Question(Category.values()[result.getInt(1)], result.getString(2), result.getString(3), answers));
			}

			return questions;
		} catch (SQLException e)
		{
			// some Exception
			log_err(10);
			// e.printStackTrace();
			return null;
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
			log_err(11);
			// e.printStackTrace();
			return null;
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
			if (result.next()) return result.getInt("count");
		} catch (SQLException e)
		{
			// some Exception
			// e.printStackTrace();
		}

		log_err(12);
		return -1;
	}

	/**
	 * Logs an error.
	 * 
	 * @param code
	 *            the error-code
	 */
	private void log_err(int code)
	{
		System.err.println("DataManager-error: '" + code + "'!");
	}

	/**
	 * Checks whether a string is valid for database. (max-length: 255)
	 * 
	 * @param string
	 *            the string to check
	 * @return whether a string is valid for database
	 */
	public static boolean check(String string)
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
	public static boolean check(String string, int maxLength)
	{
		// check whether string is valid for database
		if (string == null || string.isEmpty() || string.length() > maxLength)
			// invalid string
			return false;
		// valid string
		return true;
	}
}