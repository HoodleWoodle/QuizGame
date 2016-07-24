package quiz.model;

import java.util.ResourceBundle;

import quiz.client.view.GameFrame;

/**
 * @author Alex, Eric, Quirin, Stefan
 * @version 29.04.2016
 */
public enum Category
{
	/***/
	NATURE;

	private static ResourceBundle localization = GameFrame.getLocalization();

	@Override
	public String toString()
	{
		switch (this)
		{
		case NATURE:
			return localization.getString("NATURE");
		default:
			return null;
		}
	}
}