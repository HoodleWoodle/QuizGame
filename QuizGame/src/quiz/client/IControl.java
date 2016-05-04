package quiz.client;

import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;

/**
 * @author Alex
 * @version 03.05.2016
 */
public interface IControl
{
	/**
	 * Registers the IView.
	 * 
	 * @param name
	 *            the name of the Account
	 * @param password
	 *            the password of the Account
	 */
	void register(String name, String password);

	/**
	 * Logs an Account in.
	 * 
	 * @param name
	 *            the name of the Account
	 * @param password
	 *            the password of the Account
	 */
	void login(String name, String password);

	/**
	 * Requests a new Match.
	 * 
	 * @param category
	 *            the Category
	 */
	void requestMatch(Category category);

	/**
	 * Requests a new Match.
	 * 
	 * @param category
	 *            the Category
	 * @param aim
	 *            the aim-Account
	 */
	void requestMatch(Category category, Account aim);

	/**
	 * Requests a new Match.
	 */
	void requestMatch();

	/**
	 * Requests a new Match.
	 * 
	 * @param aim
	 *            the aim-Account
	 */
	void requestMatch(Account aim);

	/**
	 * Accepts a request.
	 * 
	 * @param request
	 *            the request to accept
	 */
	void acceptRequest(Match request);

	/**
	 * Sets a choice.
	 * 
	 * @param index
	 *            the index of the correct answer
	 */
	void setChoice(int index);
}