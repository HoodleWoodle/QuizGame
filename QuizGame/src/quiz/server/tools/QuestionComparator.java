package quiz.server.tools;

import java.util.Comparator;

import quiz.model.Question;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class QuestionComparator implements Comparator<Question>
{
	@Override
	public int compare(Question q0, Question q1)
	{
		// compare two Questions by question
		return q0.getQuestion().compareTo(q1.getQuestion());
	}
}