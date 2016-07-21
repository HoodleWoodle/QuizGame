package quiz.client;

import static quiz.net.NetworkKeys.TAG_LOGIN;
import static quiz.net.NetworkKeys.TAG_REGISTER;
import static quiz.net.NetworkKeys.TAG_REQUEST;
import static quiz.net.NetworkKeys.TAG_REQUEST_0;
import static quiz.net.NetworkKeys.TAG_REQUEST_1;
import static quiz.net.NetworkKeys.TAG_REQUEST_2;
import static quiz.net.NetworkKeys.TAG_REQUEST_3;
import static quiz.net.NetworkKeys.TAG_REQUEST_ACCEPT;
import static quiz.net.NetworkKeys.TAG_REQUEST_DENY;
import static quiz.net.NetworkKeys.TAG_SET_ANSWER;

import lib.net.tcp.NetworkMessage;
import quiz.model.Account;
import quiz.model.Category;
import quiz.model.Match;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public final class Control implements IControl
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
		client.send(new NetworkMessage(TAG_REGISTER, new String[] { name, password }).getBytes());
	}

	@Override
	public void login(String name, String password)
	{
		client.send(new NetworkMessage(TAG_LOGIN, new String[] { name, password }).getBytes());
	}

	@Override
	public void requestMatch(Category category, Account aim)
	{
		client.send(new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_0, category.ordinal() + "", aim.getID() + "" }).getBytes());
	}

	@Override
	public void requestMatch(Category category)
	{
		client.send(new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_1, category.ordinal() + "" }).getBytes());
	}

	@Override
	public void requestMatch(Account aim)
	{
		client.send(new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_2, aim.getID() + "" }).getBytes());
	}

	@Override
	public void requestMatch()
	{
		client.send(new NetworkMessage(TAG_REQUEST, TAG_REQUEST_3).getBytes());
	}

	@Override
	public void acceptRequest(Match request)
	{
		client.send(new NetworkMessage(TAG_REQUEST_ACCEPT, request.getID() + "").getBytes());
	}

	@Override
	public void denyRequest(Match request)
	{
		client.send(new NetworkMessage(TAG_REQUEST_DENY, request.getID() + "").getBytes());
	}

	@Override
	public void setAnswer(int index)
	{
		client.send(new NetworkMessage(TAG_SET_ANSWER, index + "").getBytes());
	}
}