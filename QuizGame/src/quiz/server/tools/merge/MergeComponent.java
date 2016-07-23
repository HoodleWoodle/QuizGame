package quiz.server.tools.merge;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import quiz.model.Question;

/**
 * @author Stefan
 * @version 23.07.2016
 */
public final class MergeComponent extends JComponent
{
	private static final long serialVersionUID = 1L;

	private JTextArea area;
	private JButton button;

	private final MergePanel merge;

	private Question question;

	MergeComponent(MergePanel merge)
	{
		this.merge = merge;

		setLayout(null);

		// initialize components
		initComponents();
		// initialize listeners
		initListeners();
	}

	private void initComponents()
	{
		setPreferredSize(new Dimension(585, 100));

		JScrollPane scroll = new JScrollPane(area = new JTextArea());
		area.setEditable(false);
		area.setLineWrap(false);
		scroll.setBounds(0, 0, 525, 100);
		add(scroll);

		add(button = new JButton("use"));
		button.setBounds(530, 45, 50, 30);
	}

	private void initListeners()
	{
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				merge.setMerge(question);
			}
		});
	}

	void setQuestion(Question question)
	{
		this.question = question;
		String[] answers = question.getAnswers();
		String image = question.getImage();

		area.setText("");

		area.append("Category: ");
		area.append(question.getCategory() + "");
		area.append("\nQuestion: ");
		area.append(question.getQuestion());
		area.append("\nCorrect: ");
		area.append(answers[0]);
		area.append("\nAnswers: ");
		for (int i = 1; i < answers.length; i++)
		{
			area.append(answers[i]);
			if (i < answers.length - 1) area.append("; ");
		}
		area.append("\nImage: ");
		area.append(image.isEmpty() ? "-" : image);
	}
}