package quiz.server.tools;

import java.util.Comparator;

import quiz.model.Question;

/**
 * @author Stefan
 */
final class QuestionComparator implements Comparator<Question>
{
	@Override
	public int compare(Question q0, Question q1)
	{
		// compare Questions by question
		return q0.getQuestion().compareTo(q1.getQuestion());
	}
}