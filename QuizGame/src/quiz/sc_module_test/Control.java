package quiz.sc_module_test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quiz.Constants;
import quiz.client.IControl;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;
import quiz.model.Question;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

public class Control implements IControl
{
	private static int MATCH_ID = 0;

	private static final Random random = new Random();

	public IDataManager data;
	public Model model;

	public List<Match> requests;
	public List<Match> matches;

	Control()
	{
		requests = new ArrayList<Match>();
		matches = new ArrayList<Match>();

		data = new DataManager();

		for (Category category : Category.values())
			if (data.getQuestions(category).size() < Constants.QUESTION_COUNT)
			{
				System.err.println("NOT ENOUGH QUESTIONS ('" + category + "' - " + data.getQuestions(category).size() + "/" + Constants.QUESTION_COUNT + ")");
				System.exit(1);
			}

		model = new Model(new Control());
	}

	@Override
	public void register(String name, String password)
	{
		if (checkAcc())
			return;

		Account acc = data.addAccount(name, password);

		if (acc == null)
		{
			model.setStatus(Status.INVALID_REGISTER_DETAILS);
			return;
		}

		model.setAccount(acc);
	}

	@Override
	public void login(String name, String password)
	{
		if (checkAcc())
			return;

		Account acc = data.getAccount(name, password);

		if (acc == null)
		{
			model.setStatus(Status.INVALID_LOGIN_DETAILS);
			return;
		}

		model.setAccount(acc);
	}

	@Override
	public void requestMatch(Category category, Account aim)
	{
		if (!checkAcc_Mat())
			return;

		requests.add(new Match(MATCH_ID++, category, new Account[] { model.getAccount(), aim }, new Question[0], new int[0][0]));
		model.setRequests(getRequests(model.getAccount()));
	}

	@Override
	public void requestMatch(Category category)
	{
		if (!checkAcc_Mat())
			return;

		requestMatch(category, getRandomOpponent(model.getAccount()));
	}

	@Override
	public void requestMatch(Account aim)
	{
		if (!checkAcc_Mat())
			return;

		requestMatch(getRandomCategory(), aim);
	}

	@Override
	public void requestMatch()
	{
		if (!checkAcc_Mat())
			return;

		requestMatch(getRandomCategory(), getRandomOpponent(model.getAccount()));
	}

	@Override
	public void acceptRequest(Match request)
	{
		if (!checkAcc_Mat())
			return;

		for (Account acc : request.getOpponents())
			if (!acc.isAvailable())
			{
				model.setStatus(Status.OPPONENT_NOT_AVAILABLE);
				return;
			}

		requests.remove(request);
		matches.add(request);
	}

	@Override
	public void setAnswer(int index)
	{
		if (model.getAccount() == null)
			return;

		// TODO
	}

	private boolean checkAcc_Mat()
	{
		if (!checkAcc())
			return false;
		if (model.getMatch() != null)
		{
			model.setStatus(Status.ALREADY_IN_MATCH);
			return false;
		}
		return true;
	}

	private boolean checkAcc()
	{
		if (model.getAccount() == null)
		{
			model.setStatus(Status.NOT_LOGGED_IN);
			return false;
		}
		return true;
	}

	public Match[] getRequests(Account account)
	{
		List<Match> result = new ArrayList<Match>();

		for (Match match : requests)
			for (Account acc : match.getOpponents())
				if (acc.equals(account))
					result.add(match);

		return (Match[]) result.toArray();
	}

	public Category getRandomCategory()
	{
		return Category.values()[Category.values().length];
	}

	public Account getRandomOpponent(Account account)
	{
		List<Account> accounts = new ArrayList<Account>();
		List<Account> accs = data.getAccounts();
		for (Account acc : accs)
			if (acc.isAvailable())
				accounts.add(acc);
		if (accounts.size() <= 1)
		{
			model.setStatus(Status.NO_OPPONENTS_AVAILABLE);
			return null;
		}
		Account acc = accounts.get(random.nextInt(accounts.size()));
		if (acc.equals(account))
			return getRandomOpponent(account);
		return accounts.get(random.nextInt(accounts.size()));
	}
}