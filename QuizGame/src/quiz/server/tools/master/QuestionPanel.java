package quiz.server.tools.master;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import quiz.Utils;
import quiz.model.Category;
import quiz.model.Question;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class QuestionPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final IDataManager dataManager;

	private JTable table;
	private QuestionTableModel model;
	private JTextField question;
	private JTextField[] answers;
	private JComboBox<Category> category;
	private JButton get;
	private JButton remove;
	private JButton add;

	/**
	 * Creates an instance of QuestionPanel.
	 * 
	 * @param dataManager
	 *            the IDataManager
	 */
	QuestionPanel(IDataManager dataManager)
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
		JScrollPane scroll = new JScrollPane(table = new JTable(model = new QuestionTableModel()));
		add(scroll);
		scroll.setBounds(0, 0, 596, 240);

		// initialize labels
		JLabel label = null;
		add(label = new JLabel("Question:"));
		label.setBounds(0, 240, 70, 25);
		add(label = new JLabel("Correct:"));
		label.setBounds(0, 265, 70, 25);
		add(label = new JLabel("Answer 1:"));
		label.setBounds(302, 265, 70, 25);
		add(label = new JLabel("Answer 2:"));
		label.setBounds(0, 290, 70, 25);
		add(label = new JLabel("Answer 3:"));
		label.setBounds(302, 290, 70, 25);
		add(label = new JLabel("Category:"));
		label.setBounds(0, 315, 70, 25);

		// initialize fields/boxes
		add(question = new JTextField());
		question.setBounds(70, 239, 525, 26);
		answers = new JTextField[4];
		add(answers[0] = new JTextField());
		answers[0].setBounds(70, 264, 223, 26);
		add(answers[1] = new JTextField());
		answers[1].setBounds(372, 264, 223, 26);
		add(answers[2] = new JTextField());
		answers[2].setBounds(70, 289, 223, 26);
		add(answers[3] = new JTextField());
		answers[3].setBounds(372, 289, 223, 26);
		add(category = new JComboBox<Category>(Category.values()));
		category.setBounds(70, 316, 120, 23);

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
				Question selected = model.get(table.getSelectedRow());

				// there is something selected
				if (selected == null) return;

				// set data to input
				question.setText(selected.getQuestion());
				for (int i = 0; i < 4; i++)
					answers[i].setText(selected.getAnswers()[i]);
				category.setSelectedItem(selected.getCategory());
			}
		});

		remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// get selected
				Question selected = model.get(table.getSelectedRow());

				// there is something selected
				if (selected == null) return;

				// remove selected
				dataManager.removeQuestion(selected);

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
				String q = question.getText();
				String[] as = new String[4];
				for (int i = 0; i < 4; i++)
					as[i] = answers[i].getText();
				Category ca = (Category) category.getSelectedItem();

				boolean correct = true;
				// check input
				if (!DataManager.check(q, 1024) || !Utils.checkString(q)) correct = false;
				for (String a : as)
					if (!DataManager.check(a) || !Utils.checkString(a)) correct = false;
				if (correct) for (int i = 1; i < as.length; i++)
					if (as[0].equals(as[i])) correct = false;
				if (ca == null) correct = false;
				if (!dataManager.addQuestion(new Question(ca, q, as))) correct = false;

				if (!correct)
				{
					MasterTool.invalid();
					return;
				}

				// clear input
				question.setText("");
				for (int i = 0; i < 4; i++)
					answers[i].setText("");
				category.setSelectedIndex(0);

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
		model.update(dataManager.getQuestions());
	}
}