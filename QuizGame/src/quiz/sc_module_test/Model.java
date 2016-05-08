package quiz.sc_module_test;

import java.util.ArrayList;
import java.util.List;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.client.view.IView;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

public class Model implements IModel
{
	private final IControl control;
	private final List<IView> views;

	private Account account;
	private Match match;
	private Question question;
	private Account[] opponents;
	private Match[] requests;

	private Status status;

	public Model(IControl control)
	{
		this.control = control;
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

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public void setAccount(Account account)
	{
		this.account = account;
		onChange(ChangeType.ACCOUNT);
	}

	public void setMatch(Match match)
	{
		this.match = match;
		onChange(ChangeType.MATCH);
	}

	public void setQuestion(Question question)
	{
		this.question = question;
		onChange(ChangeType.QUESTION);
	}

	public void setOpponents(Account[] opponents)
	{
		this.opponents = opponents;
		onChange(ChangeType.OPPONENTS);
	}

	public void setRequests(Match[] requests)
	{
		this.requests = requests;
		onChange(ChangeType.REQUESTS);
	}

	@Override
	public void addView(IView view)
	{
		views.add(view);
		view.init(this, control);
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