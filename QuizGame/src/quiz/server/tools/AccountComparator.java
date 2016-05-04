package quiz.server.tools;

import java.util.Comparator;

import quiz.model.Account;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class AccountComparator implements Comparator<Account>
{
	@Override
	public int compare(Account a0, Account a1)
	{
		// compare two Accounts by name
		return a0.getName().compareTo(a1.getName());
	}
}