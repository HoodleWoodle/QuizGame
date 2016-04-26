package quiz.model;

/**
 * @author Stefan
 */
public final class Account
{
	/**
	 * The ID of the Account.
	 */
	private final int ID;
	/**
	 * The name of the Account.
	 */
	private final String name;
	/**
	 * The password of the Account.
	 */
	private final String password;
	/**
	 * The score of the Account.
	 */
	private final int score;
	/**
	 * Whether the Account is currently online.
	 */
	private boolean online;
	/**
	 * Whether the Account is currently available for a new Match.
	 */
	private boolean available;

	/**
	 * Creates an instance of Account.
	 * 
	 * @param ID
	 *            the ID of the Account
	 * @param name
	 *            the name of the Account
	 * @param password
	 *            the password of the Account
	 * @param score
	 *            the score of the Account
	 */
	public Account(int ID, String name, String password, int score)
	{
		this.ID = ID;
		this.name = name;
		this.password = password;
		this.score = score;
	}

	/**
	 * Creates an instance of Account.
	 * 
	 * @param ID
	 *            the ID of the Account
	 * @param name
	 *            the name of the Account
	 * @param score
	 *            the score of the Account
	 */
	public Account(int ID, String name, int score)
	{
		this(ID, name, null, score);
	}

	/**
	 * Getter.
	 * 
	 * @return the ID of the Account
	 */
	public int getID()
	{
		return ID;
	}

	/**
	 * Getter.
	 * 
	 * @return the name of the Account
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Getter.
	 * 
	 * @return the password of the Account
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Getter.
	 * 
	 * @return the score of the Account
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Getter.
	 * 
	 * @return whether the Account is currently online
	 */
	public boolean isOnline()
	{
		return online;
	}

	/**
	 * Getter.
	 * 
	 * @return whether the Account is currently available for a new Match
	 */
	public boolean isAvailable()
	{
		return available;
	}

	/**
	 * Setter.
	 * 
	 * @param online
	 *            whether the Account is online
	 */
	public void setOnline(boolean online)
	{
		this.online = online;
	}

	/**
	 * Setter.
	 * 
	 * @param available
	 *            whether the Account is available for a new Match
	 */
	public void setAvailable(boolean available)
	{
		this.available = available;
	}
}