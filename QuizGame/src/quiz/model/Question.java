package quiz.model;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class Question
{
	private final Category category;
	private final String question;
	private final String image;
	private final String[] answers;

	/**
	 * Creates an instance of Question.
	 * 
	 * @param category
	 *            the Category of the Question
	 * @param question
	 *            the question-string of the Question
	 * @param image
	 *            the image-name of the Question
	 * @param answers
	 *            the answer-strings of the Question
	 */
	public Question(Category category, String question, String image, String[] answers)
	{
		this.category = category;
		this.question = question;
		this.image = image;
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
	 * @return the image-name of the Question
	 */
	public String getImage()
	{
		return image;
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