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
final class Database
{
	private final String database;
	private final String user;
	private final String password;

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;

	private boolean connected;

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
	Database(String database, String user, String password)
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
	boolean connect()
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
			System.out.println("Cannot connect to database!");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the Database.
	 * 
	 * @return whether it was successful
	 */
	boolean close()
	{
		try
		{
			System.out.println("Closing database!");

			// try to close database-connection
			if (statement != null) statement.close();
			if (resultSet != null) resultSet.close();
			return true;
		} catch (SQLException e)
		{
			// some Exception
			System.out.println("Cannot close database!");
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
	ResultSet select(String sql)
	{
		if (connected == false) return null;
		try
		{
			// execute select like SQL-command
			if (resultSet != null) resultSet.close();
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
	boolean insert(String sql)
	{
		if (connected == false) return false;
		try
		{
			// execute of insert like SQL-command
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
	int insertReturn(String sql)
	{
		if (connected != false) try
		{
			// execute insert like SQL-command and get key for returning
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.executeUpdate();
			if (resultSet != null) resultSet.close();
			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) return resultSet.getInt(1);
		} catch (SQLException e)
		{
			// some Exception
			System.err.println("Cannot execute SQL-command: '" + sql + "'!");
			// e.printStackTrace();
		}
		return -1;
	}
}