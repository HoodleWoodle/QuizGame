package quiz.server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public class Database
{
	/**
	 * The Database-tag.
	 */
	private final String database;
	/**
	 * The Database-user.
	 */
	private final String user;
	/**
	 * The Database-password.
	 */
	private final String password;

	/**
	 * The Database-connection.
	 */
	private Connection connection;
	/**
	 * Whether the Database is connected.
	 */
	private boolean connected;
	/**
	 * The Database-statement.
	 */
	private Statement statement;
	/**
	 * The Database-result-set.
	 */
	private ResultSet resultSet;

	/**
	 * Creates an new instance of Database (H2).
	 * 
	 * @param database
	 *            the database
	 * @param user
	 *            the user-name
	 * @param password
	 *            the user-password
	 */
	public Database(String database, String user, String password)
	{
		this.database = database;
		this.user = user;
		this.password = password;
	}

	/**
	 * Connects to the Database.
	 * 
	 * @return whether it was successful
	 */
	public boolean connect()
	{
		try
		{
			System.out.println("Connecting to database!");

			// try to connect to database
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection(database, user, password);
			statement = connection.createStatement();
			connected = true;
			return true;
		} catch (ClassNotFoundException | SQLException e)
		{
			// some Exception
			System.err.println("Cannot connect to database!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the Database.
	 * 
	 * @return whether it was successful
	 */
	public boolean close()
	{
		try
		{
			System.out.println("Closing database!");

			// try to close database-connection
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
			return true;
		} catch (SQLException e)
		{
			// some Exception
			System.err.println("Cannot close database!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Selects in the Database!
	 * 
	 * @param sql
	 *            the SQL-command
	 * @return the resultSet
	 */
	public ResultSet select(String sql)
	{
		if (connected == false)
			return null;
		try
		{
			// execute a kind of select-command
			if (resultSet != null)
				resultSet.close();
			resultSet = statement.executeQuery(sql);
			return resultSet;
		} catch (SQLException e)
		{
			// some Exception
			System.err.println("Cannot execute SQL-command: '" + sql + "'!");
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * Inserts in the Database.
	 * 
	 * @param sql
	 *            the SQL-command
	 * @return whether it was successful
	 */
	public boolean insert(String sql)
	{
		if (connected == false)
			return false;
		try
		{
			// execute a kind of insert-command
			statement.execute(sql);
			return true;
		} catch (SQLException e)
		{
			// some Exception
			System.err.println("Cannot execute SQL-command: '" + sql + "'!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Inserts in the Database.
	 * 
	 * @param sql
	 *            the SQL-command
	 * @return the insert-ID
	 */
	public int insertReturn(String sql)
	{
		if (connected != false)
			try
			{
				// execute a kind of insert-command and get key for returning
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.executeUpdate();
				if (resultSet != null)
					resultSet.close();
				resultSet = ps.getGeneratedKeys();
				if (resultSet.next())
					return resultSet.getInt(1);
			} catch (SQLException e)
			{
				// some Exception
				System.err.println("Cannot execute SQL-command: '" + sql + "'!");
				// e.printStackTrace();
			}
		return -1;
	}
}