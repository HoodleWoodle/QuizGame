package quiz.server.model;

import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

/**
 * @author Stefan
 * @version 14.07.2016
 */
public final class MatchStep
{
	private final Question question;
	private final Account[] accounts;
	private final int[] answers;
	private int index;

	/**
	 * Creates an instance of MatchStep.
	 * 
	 * @param question
	 *            the Question of the MatchStep
	 */
	public MatchStep(Question question)
	{
		this.question = question;
		this.accounts = new Account[2];
		this.answers = new int[2];
	}

	/**
	 * Returns whether the MatchStep is done.
	 * 
	 * @return whether the MatchStep is done
	 */
	public boolean isDone()
	{
		return index == 2;
	}

	/**
	 * Sets the answer of an Account.
	 * 
	 * @param account
	 *            the Account which has answered
	 * @param answer
	 *            the answer of the Account
	 */
	public void setAnswer(Account account, int answer)
	{
		accounts[index] = account;
		answers[index++] = answer;
	}

	/**
	 * Adds this MatchStep to an Match.
	 * 
	 * @param match
	 *            the Match to edit
	 * @return the new Match
	 */
	public Match editMatch(Match match)
	{
		// advance match by this
		Question[] questions = match.getQuestions();
		Question[] newQuestions = new Question[questions.length + 1];
		for (int i = 0; i < questions.length; i++)
			newQuestions[i] = questions[i];
		newQuestions[newQuestions.length - 1] = question;

		Account[] opponents = match.getOpponents();

		int[][] answers = match.getAnswers();
		int[][] newAnswers = new int[answers.length][answers[0].length + 1];
		for (int i = 0; i < answers.length; i++)
			for (int j = 0; j < answers[0].length; j++)
				newAnswers[i][j] = answers[i][j];

		for (int i = 0; i < newAnswers.length; i++)
			for (int j = 0; j < newAnswers.length; j++)
				if (opponents[i].equals(accounts[j])) newAnswers[i][newAnswers[0].length - 1] = this.answers[j];

		return new Match(match.getID(), match.getCategory(), opponents, newQuestions, newAnswers);
	}
}