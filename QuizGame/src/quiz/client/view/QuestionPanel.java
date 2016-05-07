package quiz.client.view;

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
		questionText.setText(question.getQuestion());
		
		add(questionText, c);
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.20;
		
		List<String> answers = Arrays.asList(question.getAnswers());
		
		for(int i = 0; i < answerButtons.length; i++) {
			Collections.shuffle(answers);
			answerButtons[i] = new JButton(answers.get(0));
			answerButtons[i].addActionListener(this);
			answers.remove(0);
		}
		
		c.gridy = 1;
		c.insets = new Insets(10, 20, 10, 10);
		add(answerButtons[0], c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 10, 20);
		add(answerButtons[1], c);
		
		c.gridy = 2;
		c.insets = new Insets(10, 20, 10, 10);
		add(answerButtons[2], c);
		
		c.gridx = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 10, 20);
		add(answerButtons[3], c);
		
		c.gridy = 3;
		c.gridwidth = 2;
		c.weighty = 0.05;
		c.insets = new Insets(10, 20, 20, 20);
		countdown = new CountdownProgressBar(20);
		add(countdown, c);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		JButton answerButton = (JButton) event.getSource();
		
		String[] answers = question.getAnswers();
		for(int i = 0; i < answers.length; i++) {
			if(answers[i].equals(answerButton.getText())) {
				control.setAnswer(i);
			}
		}
	}

	@Override
	public void init(IModel model, IControl control) {
		this.model = model;
		this.control = control;
	}

	@Override
	public void onChange(ChangeType types) {
		
	}
}