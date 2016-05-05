package quiz.sc_module_test;

import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Information;
import quiz.client.view.IView;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

public class Model implements IModel
{
	private final IView view;

	private Account account;
	private Match match;
	private Question question;
	private Account[] opponents;
	private Match[] requests;
	private Information information;

	Model(IView view)
	{
		this.view = view;
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

	@Override
	public Information getInformation()
	{
		return information;
	}

	public void setInformation(Information information)
	{
		this.information = information;
		view.onChange(ChangeType.INFORMATION);
	}

	public void setAccount(Account account)
	{
		this.account = account;
		view.onChange(ChangeType.ACCOUNT);
	}

	public void setMatch(Match match)
	{
		this.match = match;
		view.onChange(ChangeType.MATCH);
	}

	public void setQuestion(Question question)
	{
		this.question = question;
		view.onChange(ChangeType.QUESTION);
	}

	public void setOpponents(Account[] opponents)
	{
		this.opponents = opponents;
		view.onChange(ChangeType.OPPONENTS);
	}

	public void setRequests(Match[] requests)
	{
		this.requests = requests;
		view.onChange(ChangeType.REQUESTS);
	}
}