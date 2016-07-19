package quiz.model;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class Match
{
	private final int ID;
	private final Category category;
	private final Account[] opponents;
	private final Question[] questions;
	private final int[][] answers;

	/**
	 * Creates an instance of Match.
	 * 
	 * @param ID
	 *            the ID of the Match
	 * @param category
	 *            the Category of the Match
	 * @param opponents
	 *            the Opponents of the Match
	 * @param questions
	 *            the done Questions of the Match
	 * @param answers
	 *            the answer-indices of the Match
	 */
	public Match(int ID, Category category, Account[] opponents, Question[] questions, int[][] answers)
	{
		this.ID = ID;
		this.category = category;
		this.opponents = opponents;
		this.questions = questions;
		this.answers = answers;
	}

	/**
	 * Getter.
	 * 
	 * @return the ID of the Match
	 */
	public int getID()
	{
		return ID;
	}

	/**
	 * Getter.
	 * 
	 * @return the Category of the Match
	 */
	public Category getCategory()
	{
		return category;
	}

	/**
	 * Getter.
	 * 
	 * @return the Opponents of the Match
	 */
	public Account[] getOpponents()
	{
		return opponents;
	}

	/**
	 * Getter.
	 * 
	 * @return the done Questions of the Match
	 */
	public Question[] getQuestions()
	{
		return questions;
	}

	/**
	 * Getter.
	 * 
	 * @return the answer-indices of the Match
	 */
	public int[][] getAnswers()
	{
		return answers;
	}

	/**
	 * Returns the Account winning the Match. (null while tie)
	 * 
	 * @return the Account winning the Match
	 */
	public Account getWinner()
	{
		int[] wins = new int[opponents.length];

		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
				wins[i] += answers[i][j] == 0 ? 1 : 0;

		for (int i = 0; i < opponents.length; i++)
		{
			int j = (i + 1) % opponents.length;
			if (wins[i] > wins[j]) return opponents[i];
		}
		return null;
	}
}