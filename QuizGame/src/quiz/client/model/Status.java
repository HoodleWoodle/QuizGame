package quiz.client.model;

/**
 * @author Stefan
 * @version 05.05.2016
 */
public enum Status
{
	/**
	 * The status, if the last action was successfully executed.
	 */
	SUCCESS,
	/**
	 * The status, if the client could not be registered because of wrong details.
	 */
	INVALID_REGISTER_DETAILS,
	/**
	 * The status, if the client could not be logged in because of wrong details.
	 */
	INVALID_LOGIN_DETAILS,
	/**
	 * The status, that no opponents are available.
	 */
	NO_OPPONENTS_AVAILABLE,
	/**
	 * The status, that the desired opponent is not available.
	 */
	OPPONENT_NOT_AVAILABLE,
	/**
	 * The status, if the account is already logged in.
	 */
	ALREADY_LOGGED_IN,
	/**
	 * The status, if this match is already requested.
	 */
	ALREADY_REQUESTED;
}