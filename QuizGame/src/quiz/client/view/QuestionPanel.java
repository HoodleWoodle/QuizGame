package quiz.client.view;

import static quiz.Constants.SECONDS_PER_ANSWER;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Match;
import quiz.model.Question;

/**
 * 
 * @author Eric
 * @version 5.05.16
 */
public class QuestionPanel extends JPanel implements IView, ActionListener {

	private IModel model;
	private IControl control;
	private JTextPane questionText;
	private JButton[] answerButtons = new JButton[4];
	private CountdownProgressBar countdown;
	private boolean answerLoggedIn;
	private Question question;

	/**
	 * Creates a new QuestionPanel.
	 */
	public QuestionPanel() {
		setLayout(new GridBagLayout());

		question = model.getQuestion();
		initComponents();
	}

	private void initComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 20, 10, 20);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 0.55;
		c.fill = GridBagConstraints.BOTH;

		questionText = new JTextPane();
		questionText.setEditable(false);

		add(questionText, c);
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.20;

		c.gridy = 1;
		c.insets = new Insets(10, 20, 10, 10);
		add(answerButtons[0] = new JButton(""), c);

		c.gridx = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 10, 20);
		add(answerButtons[1] = new JButton(""), c);

		c.gridy = 2;
		c.insets = new Insets(10, 20, 10, 10);
		add(answerButtons[2] = new JButton(""), c);

		c.gridx = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 10, 20);
		add(answerButtons[3] = new JButton(""), c);

		c.gridy = 3;
		c.gridwidth = 2;
		c.weighty = 0.05;
		c.insets = new Insets(10, 20, 20, 20);
		countdown = new CountdownProgressBar(SECONDS_PER_ANSWER);
		add(countdown, c);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton answerButton = (JButton) event.getSource();

		String[] answers = question.getAnswers();
		for (int i = 0; i < answers.length; i++) {
			if (answers[i].equals(answerButton.getText())) {
				// only accept answers once and before the countdown is over
				if (!countdown.isOver() && !answerLoggedIn) {
					answerLoggedIn = true;
					control.setAnswer(i);
				}
			}

			for (int j = 0; j < answerButtons.length; j++) {
				if (answerButtons[j].getText().equals(answers[i])) {
					// first answer is correct
					answerButtons[j].setBackground((i == 0) ? Color.GREEN : Color.RED);
				}
			}
		}
	}

	@Override
	public void init(IModel model, IControl control) {
		this.model = model;
		this.control = control;
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		if (type == ChangeType.MATCH) {
			Match match = model.getMatch();

			int[][] answersGiven = match.getAnswers();

			// todo
			for (int a = 0; a < answersGiven.length; a++) {
				for (int j = 0; j < answersGiven[0].length; j++) {

				}
			}
		}
		if (type == ChangeType.QUESTION) {
			// prepare the next question
			question = model.getQuestion();
			List<String> answers = Arrays.asList(question.getAnswers());
			questionText.setText(question.getQuestion());

			for (int i = 0; i < answerButtons.length; i++) {
				Collections.shuffle(answers);
				answerButtons[i].setText(answers.get(0));
				answerButtons[i].setBackground(Color.WHITE);
				answerButtons[i].addActionListener(this);
				answers.remove(0);
			}

			answerLoggedIn = false;
		}
	}
}