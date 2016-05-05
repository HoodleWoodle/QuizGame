package quiz.model;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class Question
{
	private final Category category;
	private final String question;
	private final String[] answers;

	/**
	 * Creates an instance of Question.
	 * 
	 * @param category
	 *            the Category of the Question
	 * @param question
	 *            the question-string of the Question
	 * @param answers
	 *            the answer-strings of the Question
	 */
	public Question(Category category, String question, String[] answers)
	{
		this.category = category;
		this.question = question;
		this.answers = answers;
	}

	/**
	 * Getter.
	 * 
	 * @return the Category of the Question
	 */
	public Category getCategory()
	{
		return category;
	}

	/**
	 * Getter.
	 * 
	 * @return the question-string of the Question
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 * Getter.
	 * 
	 * @return the answer-strings of the Question
	 */
	public String[] getAnswers()
	{
		return answers;
	}
}