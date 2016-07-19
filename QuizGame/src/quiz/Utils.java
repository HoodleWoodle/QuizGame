package quiz;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import quiz.net.NetworkKeys;

/**
 * @author Stefan
 * @version 14.07.2016
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
		if (string.contains(";")) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB)) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB)) return false;
		if (string.contains(NetworkKeys.SPLIT_SUB_SUB_SUB)) return false;
		return true;
	}

	/**
	 * Initializes the surfaces Look And Feel.
	 */
	public static void initalizeLAF()
	{
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				if (Constants.LOOK_AND_FEEL.equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
		} catch (Exception e)
		{
		}
	}

	// bad practice
	@SuppressWarnings("javadoc")
	public static Image loadIcon()
	{
		try
		{
			return ImageIO.read(new File(Constants.ICON_FILE));
		} catch (IOException e)
		{
			return null;
		}

	}
}