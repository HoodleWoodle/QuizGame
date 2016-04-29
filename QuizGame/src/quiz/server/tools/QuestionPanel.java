package quiz.server.tools;

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

import quiz.model.Category;
import quiz.model.Question;
import quiz.server.model.DataManager;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class QuestionPanel extends JPanel
{
	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * The IDataManager.
	 */
	private final/* I */DataManager dataManager;

	/**
	 * The table.
	 */
	private JTable table;
	/**
	 * The QuestionTableModel.
	 */
	private QuestionTableModel model;

	/**
	 * The question-field.
	 */
	private JTextField question;
	/**
	 * The answer-fields.
	 */
	private JTextField[] answers;
	/**
	 * The category-box.
	 */
	private JComboBox<Category> category;
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
	 * Creates an instance of QuestionPanel.
	 * 
	 * @param dataManager
	 *            the IDataManager
	 */
	QuestionPanel(/* I */DataManager dataManager)
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
		question.setBounds(70, 241, 525, 22);
		answers = new JTextField[4];
		add(answers[0] = new JTextField());
		answers[0].setBounds(70, 266, 223, 22);
		add(answers[1] = new JTextField());
		answers[1].setBounds(372, 266, 223, 22);
		add(answers[2] = new JTextField());
		answers[2].setBounds(70, 291, 223, 22);
		add(answers[3] = new JTextField());
		answers[3].setBounds(372, 291, 223, 22);
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
				if (selected == null)
					return;

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
				if (selected == null)
					return;

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

				// check input
				if (!DataManager.check(q, 1024))
					return;
				for (String a : as)
					if (!DataManager.check(a))
						return;
				if (ca == null)
					return;
				if (!dataManager.addQuestion(new Question(ca, q, as)))
					return;

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