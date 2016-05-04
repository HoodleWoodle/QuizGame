package quiz.server.tools;

import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import quiz.model.Question;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class QuestionTableModel extends AbstractTableModel
{
	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * The comparator.
	 */
	private final QuestionComparator comparator = new QuestionComparator();

	/**
	 * The model.
	 */
	private List<Question> model;

	/**
	 * Updates the tableModel.
	 * 
	 * @param model
	 *            the model
	 */
	void update(List<Question> model)
	{
		this.model = model;
		Collections.sort(model, comparator);
		fireTableDataChanged();
	}

	/**
	 * Returns an row-value.
	 * 
	 * @param row
	 *            the row
	 * @return the row-value
	 */
	public Question get(int row)
	{
		if (row < 0 || row >= model.size())
			return null;

		return model.get(row);
	}

	@Override
	public int getColumnCount()
	{
		return 6;
	}

	@Override
	public String getColumnName(int column)
	{
		switch (column)
		{
		case 0:
			return "Question";
		case 1:
			return "Correct";
		case 2:
			return "Answer 1";
		case 3:
			return "Answer 2";
		case 4:
			return "Answer 3";
		case 5:
			return "Category";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount()
	{
		if (model == null)
			return 0;

		return model.size();
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (model == null)
			return null;

		switch (column)
		{
		case 0:
			return model.get(row).getQuestion();
		case 1:
			return model.get(row).getAnswers()[0];
		case 2:
			return model.get(row).getAnswers()[1];
		case 3:
			return model.get(row).getAnswers()[2];
		case 4:
			return model.get(row).getAnswers()[3];
		case 5:
			return model.get(row).getCategory();
		}

		return null;
	}
}