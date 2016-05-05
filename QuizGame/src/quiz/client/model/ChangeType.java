package quiz.client.model;

/**
 * @author Stefan
 * @version 05.05.2016
 */
public enum ChangeType
{
	/**
	 * If the Account has changed.
	 */
	ACCOUNT,
	/**
	 * If the Match has changed or updated.
	 */
	MATCH,
	/**
	 * If the Question has changed.
	 */
	QUESTION,
	/**
	 * If the opponents have changed.
	 */
	OPPONENTS,
	/**
	 * If the requests have changed.
	 */
	REQUESTS,
	/**
	 * If the Information has changed.
	 */
	INFORMATION;
}