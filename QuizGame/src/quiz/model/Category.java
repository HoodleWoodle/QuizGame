package quiz.model;

/**
 * @author Alex, Eric, Quirin, Stefan
 * @version 29.04.2016
 */
public enum Category
{
	/***/
	ENTERTAINMENT,
	/***/
	GAMING,
	/***/
	HISTORY,
	/***/
	SCIENCE,
	/***/
	TECHNOLOGY;

	/**
	 * Returns a Category by ordinal.
	 * 
	 * @param ordinal
	 *            the ordinal-number
	 * @return the desired Category
	 */
	public static Category getCategory(int ordinal)
	{
		switch (ordinal)
		{
		case 0:
			return ENTERTAINMENT;
		case 1:
			return GAMING;
		case 2:
			return HISTORY;
		case 3:
			return SCIENCE;
		case 4:
			return TECHNOLOGY;
		}

		return null;
	}
}