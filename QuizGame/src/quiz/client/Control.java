package quiz.client;

import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public class Control implements IControl
{
	private final Client client;

	/**
	 * Creates an instance of Control.
	 * 
	 * @param client
	 *            the Client instance
	 */
	public Control(Client client)
	{
		this.client = client;
	}

	@Override
	public void register(String name, String password)
	{
		client.send("reg," + name + "," + password);
	}

	@Override
	public void login(String name, String password)
	{
		client.send("log," + name + "," + password);
	}

	@Override
	public void requestMatch(Category category, Account aim)
	{
		client.send("req,1," + category.ordinal() + "," + aim.getID());
	}

	@Override
	public void requestMatch(Category category)
	{
		client.send("req,2," + category.ordinal());
	}

	@Override
	public void requestMatch(Account aim)
	{
		client.send("req,3," + aim.getID());
	}

	@Override
	public void requestMatch()
	{
		client.send("req,4");
	}

	@Override
	public void acceptRequest(Match request)
	{
		client.send("acc," + request.getID());
	}

	@Override
	public void denyRequest(Match request)
	{
		client.send("den," + request.getID());
	}

	@Override
	public void setAnswer(int index)
	{
		client.send("set," + index);
	}
}