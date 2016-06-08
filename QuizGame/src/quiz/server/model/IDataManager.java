package quiz.server.model;

import java.util.List;

import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Question;

/**
 * @author Alex
 * @version 03.05.2016
 */
public interface IDataManager
{
	/**
	 * Returns all Questions of a Category.
	 * 
	 * @param category
	 *            the desired Category
	 * @return the desired Questions (null if Exception)
	 */
	List<Question> getQuestions(Category category);

	/**
	 * Returns all Questions.
	 * 
	 * @return the desired Questions (null if Exception)
	 */
	List<Question> getQuestions();

	/**
	 * Returns the count of Questions.
	 * 
	 * @return the count of Questions
	 */
	int getQuestionCount();

	/**
	 * Returns an Account by login-data.
	 * 
	 * @param name
	 *            the name
	 * @param password
	 *            the password
	 * @return the desired Account (null if Exception)
	 */
	Account getAccount(String name, String password);

	/**
	 * Returns an Account by ID.
	 * 
	 * @param ID
	 *            the ID of the desired Account
	 * @return the desired Account (null if Exception)
	 */
	Account getAccount(int ID);

	/**
	 * Returns all Accounts.
	 * 
	 * @return the Accounts (null by Exception)
	 */
	List<Account> getAccounts();

	/**
	 * Returns the count of Accounts.
	 * 
	 * @return the count of Accounts
	 */
	int getAccountCount();

	/**
	 * Adds a Question.
	 * 
	 * @param question
	 *            the Question to add
	 * @return whether adding was successful
	 */
	boolean addQuestion(Question question);

	/**
	 * Adds an Account.
	 * 
	 * @param name
	 *            the name
	 * @param password
	 *            the password
	 * @return the created Account (null by Exception)
	 */
	Account addAccount(String name, String password);

	/**
	 * Removes a Question.
	 * 
	 * @param question
	 *            the Question to remove
	 */
	void removeQuestion(Question question);

	/**
	 * Removes an Account.
	 * 
	 * @param account
	 *            the Account to remove
	 */
	void removeAccount(Account account);

	/**
	 * Updates an Account.
	 * 
	 * @param account
	 *            the Account to update
	 * @param score
	 *            the new score
	 */
	void updateAccount(Account account, int score);

	/**
	 * Closes the IDataManager.
	 */
	void close();
}