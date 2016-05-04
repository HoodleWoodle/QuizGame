package quiz.server.tools;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import quiz.model.Account;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class AccountPanel extends JPanel
{
	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * The IDataManager.
	 */
	private final IDataManager dataManager;

	/**
	 * The table.
	 */
	private JTable table;
	/**
	 * The AccountTableModel.
	 */
	private AccountTableModel model;

	/**
	 * The name-field.
	 */
	private JTextField name;
	/**
	 * The password-field.
	 */
	private JTextField password;
	/**
	 * The get-button.
	 */
	private JButton get;
	/**
	 * The remove-button.
	 */
	private JButton remove;
	/**
	 * The add-button.
	 */
	private JButton add;

	/**
	 * Creates an instance of AccountPanel.
	 * 
	 * @param dataManager
	 *            the IDataManager instance
	 */
	AccountPanel(IDataManager dataManager)
	{
		this.dataManager = dataManager;

		setLayout(null);

		// initialize components
		initComponents();
		// initialize listeners
		initListeners();

		// update table
		update();
	}

	/**
	 * Initializes components.
	 */
	private void initComponents()
	{
		// initialize table
		JScrollPane scroll = new JScrollPane(table = new JTable(model = new AccountTableModel()));
		add(scroll);
		scroll.setBounds(0, 0, 596, 240);

		// initialize labels
		JLabel label = null;
		add(label = new JLabel("Name:"));
		label.setBounds(0, 240, 70, 25);
		add(label = new JLabel("Password:"));
		label.setBounds(0, 265, 70, 25);

		// initialize fields
		add(name = new JTextField());
		name.setBounds(70, 241, 223, 22);
		add(password = new JTextField());
		password.setBounds(70, 266, 223, 22);

		// initialize buttons
		add(get = new JButton("get"));
		get.setMargin(new Insets(0, 0, 0, 0));
		get.setBounds(2, 345, 120, 25);
		add(remove = new JButton("remove"));
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setBounds(238, 345, 120, 25);
		add(add = new JButton("add"));
		add.setMargin(new Insets(0, 0, 0, 0));
		add.setBounds(473, 345, 120, 25);
	}

	/**
	 * Initializes listeners.
	 */
	private void initListeners()
	{
		get.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// get selected
				Account selected = model.get(table.getSelectedRow());

				// there is something selected
				if (selected == null)
					return;

				// set data to input
				name.setText(selected.getName());
				password.setText(selected.getPassword());
			}
		});

		remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// get selected
				Account selected = model.get(table.getSelectedRow());

				// there is something selected
				if (selected == null)
					return;

				// remove selected
				dataManager.removeAccount(selected);

				// update table
				update();
			}
		});

		add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// get input
				String n = name.getText();
				String p = password.getText();

				// check input
				if (!DataManager.check(n) || !DataManager.check(p))
					return;
				if (dataManager.addAccount(n, p) == null)
					return;

				// clear input
				name.setText("");
				password.setText("");

				// update table
				update();
			}
		});
	}

	/**
	 * On focus or as update.
	 */
	void update()
	{
		model.update(dataManager.getAccounts());
	}
}