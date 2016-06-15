package quiz.client.model;

import java.util.ArrayList;
import java.util.List;

import quiz.client.view.IView;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public class Model implements IModel
{
	private final List<IView> views;

	private Account account;
	private Match match;
	private Question question;
	private Account[] opponents;
	private Match[] requests;

	private Status status;

	/**
	 * Creates an instance of Model.
	 */
	public Model()
	{
		views = new ArrayList<IView>();
	}

	@Override
	public Account getAccount()
	{
		return account;
	}

	@Override
	public Match getMatch()
	{
		return match;
	}

	@Override
	public Question getQuestion()
	{
		return question;
	}

	@Override
	public Account[] getOpponents()
	{
		return opponents;
	}

	@Override
	public Match[] getRequests()
	{
		return requests;
	}

	/**
	 * Setter.
	 * 
	 * @param status
	 *            the new Status
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}

	/**
	 * Setter. Triggers an onChange - event.
	 * 
	 * @param account
	 *            the new Account
	 */
	public void setAccount(Account account)
	{
		this.account = account;
		onChange(ChangeType.ACCOUNT);
	}

	/**
	 * Setter. Triggers an onChange - event.
	 * 
	 * @param match
	 *            the current Match
	 */

	public void setMatch(Match match)
	{
		this.match = match;
		onChange(ChangeType.MATCH);
	}

	/**
	 * Setter. Triggers an onChange - event.
	 * 
	 * @param question
	 *            the current Account
	 */

	public void setQuestion(Question question)
	{
		this.question = question;
		onChange(ChangeType.QUESTION);
	}

	/**
	 * Setter. Triggers an onChange - event.
	 * 
	 * @param opponents
	 *            all the opponents
	 */

	public void setOpponents(Account[] opponents)
	{
		this.opponents = opponents;
		onChange(ChangeType.OPPONENTS);
	}

	/**
	 * Setter. Triggers an onChange - event.
	 * 
	 * @param requests
	 *            all requests
	 */

	public void setRequests(Match[] requests)
	{
		this.requests = requests;
		onChange(ChangeType.REQUESTS);
	}

	@Override
	public void addView(IView view)
	{
		views.add(view);
	}

	@Override
	public void removeView(IView view)
	{
		views.remove(view);
	}

	private void onChange(ChangeType type)
	{
		for (IView view : views)
			view.onChange(ChangeType.REQUESTS, status);
	}
}