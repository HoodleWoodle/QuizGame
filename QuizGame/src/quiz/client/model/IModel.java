package quiz.client.model;

import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

/**
 * @author Alex
 * @version 03.05.2016
 */
public interface IModel
{
	/**
	 * Getter.
	 * 
	 * @return the logged in Account
	 */
	Account getAccount();

	/**
	 * Getter.
	 * 
	 * @return the current Match
	 */
	Match getMatch();

	/**
	 * Getter.
	 * 
	 * @return the current Question
	 */
	Question getQuestion();

	/**
	 * Getter.
	 * 
	 * @return all registered opponents
	 */
	Account[] getOpponents();

	/**
	 * Getter.
	 * 
	 * @return the match-requests
	 */
	Match[] getRequests();

	/**
	 * Getter.
	 * 
	 * @return the information
	 */
	Information getInformation();
}