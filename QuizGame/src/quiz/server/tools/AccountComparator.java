package quiz.server.tools;

import java.util.Comparator;

import quiz.model.Account;

/**
 * @author Stefan
 */
final class AccountComparator implements Comparator<Account>
{
	@Override
	public int compare(Account a0, Account a1)
	{
		// compare to Accounts by name
		return a0.getName().compareTo(a1.getName());
	}
}