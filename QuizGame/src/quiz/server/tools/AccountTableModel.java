package quiz.server.tools;

import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import quiz.model.Account;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class AccountTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	private final AccountComparator comparator = new AccountComparator();

	private List<Account> model;

	/**
	 * Updates the tableModel.
	 * 
	 * @param model
	 *            the model
	 */
	void update(List<Account> model)
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
	public Account get(int row)
	{
		if (row < 0 || row >= model.size())
			return null;

		return model.get(row);
	}

	@Override
	public int getColumnCount()
	{
		return 4;
	}

	@Override
	public String getColumnName(int column)
	{
		switch (column)
		{
		case 0:
			return "ID";
		case 1:
			return "Name";
		case 2:
			return "Password";
		case 3:
			return "Score";
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
			return model.get(row).getID();
		case 1:
			return model.get(row).getName();
		case 2:
			return model.get(row).getPassword();
		case 3:
			return model.get(row).getScore();
		}

		return null;
	}
}