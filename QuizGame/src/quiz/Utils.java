package quiz;

import quiz.net.NetworkKeys;

/**
 * @author Hoodle
 */
public final class Utils
{
	/**
	 * Checks whether a string contains a character which is invalid for net-communication.
	 * 
	 * @param string
	 *            the string to check
	 * @return whether the string contains a character which is invalid for net-communication
	 */
	public static boolean checkString(String string)
	{
		if (string.contains(";"))
			return false;
		if (string.contains(NetworkKeys.SPLIT_SUB))
			return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB))
			return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB_SUB))
			return false;
		return true;
	}
}