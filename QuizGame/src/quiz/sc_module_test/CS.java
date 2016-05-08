package quiz.sc_module_test;

import quiz.client.view.GameFrame;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

public class CS implements Runnable
{
	private final Control control;

	public CS(Control control)
	{
		this.control = control;
		Thread t = new Thread(this);
		t.start();
	}

	public static void main(String[] args)
	{
		new CS(new Control());
		GameFrame.getInstance();
	}

	@Override
	public void run()
	{
		boolean loggedIn = false;

		control.data.addAccount("test0", "0");
		control.data.addAccount("test1", "1");

		while (true)
		{
			if (!loggedIn)
				if (control.model.getAccount() != null)
				{
					control.model.setOpponents(((Account[]) control.data.getAccounts().toArray()));
					loggedIn = true;
				}

			if (control.model.getAccount()!=null)
			{

				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				control.requests.add(new Match(50, control.getRandomCategory(), new Account[] { control.model.getAccount(), control.getRandomOpponent(control.model.getAccount()) }, new Question[0], new int[0][0]));
				control.model.setRequests(control.getRequests(control.model.getAccount()));
			}

			try
			{
				Thread.sleep(2000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}