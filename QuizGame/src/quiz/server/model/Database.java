package quiz.server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Stefan
 */
public class Database
{
	/**
	 * The info of connecting to a Database.
	 */
	private static final String TAG_CONNECTING = "Connecting to H2Database: '<T>' - '<U>'!";
	/**
	 * The error-message of connecting to Database.
	 */
	private static final String ERR_CONNECTING = "Cannot connect to H2Database!";
	/**
	 * The error-message of closing Database.
	 */
	private static final String ERR_CLOSING = "Cannot close H2Database!";
	/**
	 * The error-message of executing a sql-command.
	 */
	private static final String ERR_SQL = "Cannot execute SQL-command: '<C>'!";

	/**
	 * The Database-tag.
	 */
	private final String database;
	/**
	 * The Database-user.
	 */
	private final String user;
	/**
	 * The Database password.
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
			System.out.println(TAG_CONNECTING.replaceAll("<T>", database).replaceAll("<U>", user));

			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection(database, user, password);
			statement = connection.createStatement();
			connected = true;
			return true;
		} catch (ClassNotFoundException | SQLException e)
		{
			System.err.println(ERR_CONNECTING);
			e.printStackTrace();
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
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
			return true;
		} catch (SQLException e)
		{
			System.err.println(ERR_CLOSING);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Selects in the Database!
	 * 
	 * @param sql
	 *            the sql-command
	 * @return the ResultSet
	 */
	public ResultSet select(String sql)
	{
		if (connected == false)
			return null;
		try
		{
			if (resultSet != null)
				resultSet.close();
			resultSet = statement.executeQuery(sql);
			return resultSet;
		} catch (SQLException e)
		{
			System.err.println(ERR_SQL.replaceAll("<C>", sql));
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Inserts in the Database.
	 * 
	 * @param sql
	 *            the sql-command
	 * @return whether it was successful
	 */
	public boolean insert(String sql)
	{
		if (connected == false)
			return false;
		try
		{
			statement.execute(sql);
			return true;
		} catch (SQLException e)
		{
			System.err.println(ERR_SQL.replaceAll("<C>", sql));
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Inserts in the Database.
	 * 
	 * @param sql
	 *            the sql-command
	 * @return the insert-id
	 */
	public int insertReturn(String sql)
	{
		if (connected != false)
			try
			{
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.executeUpdate();
				if (resultSet != null)
					resultSet.close();
				resultSet = ps.getGeneratedKeys();
				if (resultSet.next())
					return resultSet.getInt(1);
			} catch (SQLException e)
			{
				System.err.println(ERR_SQL.replaceAll("<C>", sql));
				e.printStackTrace();
			}
		return -1;
	}
}