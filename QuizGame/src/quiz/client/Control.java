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
		NetworkMessage msg = new NetworkMessage(TAG_REGISTER, new String[] { name, password });
		client.send(msg.getBytes());
	}

	@Override
	public void login(String name, String password)
	{
		NetworkMessage msg = new NetworkMessage(TAG_LOGIN, new String[] { name, password });
		client.send(msg.getBytes());
	}

	@Override
	public void requestMatch(Category category, Account aim)
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_0, category.ordinal() + "", aim.getID() + "" });
		client.send(msg.getBytes());
	}

	@Override
	public void requestMatch(Category category)
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_1, category.ordinal() + "" });
		client.send(msg.getBytes());
	}

	@Override
	public void requestMatch(Account aim)
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST, new String[] { TAG_REQUEST_2, aim.getID() + "" });
		client.send(msg.getBytes());
	}

	@Override
	public void requestMatch()
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST, TAG_REQUEST_3);
		client.send(msg.getBytes());
	}

	@Override
	public void acceptRequest(Match request)
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST_ACCEPT, request.getID() + "");
		client.send(msg.getBytes());
	}

	@Override
	public void denyRequest(Match request)
	{
		NetworkMessage msg = new NetworkMessage(TAG_REQUEST_DENY, request.getID() + "");
		client.send(msg.getBytes());
	}

	@Override
	public void setAnswer(int index)
	{
		NetworkMessage msg = new NetworkMessage(TAG_SET_ANSWER, index + "");
		client.send(msg.getBytes());
	}
}