package quiz.model;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class Account
{
	private final int ID;
	private final String name;
	private final String password;
	private final int score;

	private boolean online;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Account account = (Account) o;

		if (ID != account.ID) return false;
		if (score != account.score) return false;
		if (name != null ? !name.equals(account.name) : account.name != null) return false;
		return password != null ? password.equals(account.password) : account.password == null;

	}

	@Override
	public int hashCode() {
		int result = ID;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + score;
		return result;
	}
}