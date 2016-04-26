package quiz.model;

/**
 * @author Stefan
 */
public final class Match
{
	/**
	 * The ID of the Match.
	 */
	private final int ID;
	/**
	 * The Category of the Match.
	 */
	private final Category category;
	/**
	 * The Opponents of the Match.
	 */
	private final Account[] opponents;
	/**
	 * The done Questions of the Match.
	 */
	private final Question[] questions;
	/**
	 * The answer-indices of the Match.
	 */
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
}