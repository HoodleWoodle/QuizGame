package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static quiz.Constants.QUESTION_COUNT;
import static quiz.Constants.SECONDS_PER_ANSWER;
import static quiz.Constants.DELAY_BETWEEN_QUESTIONS;

/**
 * @author Eric
 * @version 5.05.16
 */
public class QuestionPanel extends JPanel implements IView, ActionListener {

    private IModel model;
    private IControl control;
    private GameFrame gameFrame;
    private JTextPane questionText;
    private JButton[] answerButtons = new JButton[4];
    private CountdownProgressBar countdown;
    private boolean answerLoggedIn;
    private Question question;
    private int questionsAnswered = 0;
    private GameOverPanel gameOverPanel;

    /**
     * Creates a new QuestionPanel.
     *
     * @param gameFrame the current GameFrame instance
     * @param control   the IControl implementation
     * @param model     the IModel implementation
     */
    public QuestionPanel(GameFrame gameFrame, IControl control, IModel model) {
        this.gameFrame = gameFrame;
        this.control = control;
        this.model = model;
        model.addView(this);
        setLayout(new GridBagLayout());
        initComponents();
    }

    /**
     * Returns the GameOverPanel.
     *
     * @return the GameOverPanel
     */
    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
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
        questionText.setBackground(Color.LIGHT_GRAY);
        StyledDocument doc = questionText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        add(questionText, c);
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.20;

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JButton();
            answerButtons[i].addActionListener(this);
            answerButtons[i].setBackground(Color.LIGHT_GRAY);
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

        countdown = new CountdownProgressBar(SECONDS_PER_ANSWER);
        countdown.getTimer().addActionListener(e -> {
            if (countdown.getCounter() == 0 && !answerLoggedIn) {
                answerLoggedIn = true;
                control.setAnswer(-1);
                questionsAnswered++;

                showAnswers(question.getAnswers());
            }
        });
        add(countdown, c);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton answerButton = (JButton) event.getSource();

        String[] answers = question.getAnswers();
        for (int i = 0; i < answers.length; i++) {
            if (answers[i].equals(answerButton.getText())) {
                // only accept answers once and when the countdown is running
                if (countdown.isRunning() && !answerLoggedIn) {
                    answerLoggedIn = true;
                    control.setAnswer(i);
                    answerButton.setBorder(BorderFactory.createDashedBorder(Color.BLACK, 3, 5, 5, true));
                    questionsAnswered++;
                    countdown.setCounter(0);

                    showAnswers(answers);
                }
            }
        }
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if (type == ChangeType.MATCH) {
            Match match = model.getMatch();
            int[][] answersGiven = match.getAnswers();

            int opponentIndex = -1;
            for (int a = 0; a < match.getOpponents().length; a++) {
                if (match.getOpponents()[a].getID() != model.getAccount().getID()) {
                    opponentIndex = a;
                }
            }

            if (questionsAnswered > 0) {
                int opponentAnswerIndex = answersGiven[opponentIndex][answersGiven[0].length - 1];
                String opponentAnswer = question.getAnswers()[opponentAnswerIndex];

                // make the opponent's answer visible
                for (JButton answerButton : answerButtons)
                    if (answerButton.getText().equals(opponentAnswer))
                        answerButton.setBorder(BorderFactory.createDashedBorder(new Color(10, 144, 232), 3, 5, 5, true));
            }
        }

        if (type == ChangeType.QUESTION) {
            int delay = (questionsAnswered == 0) ? 0 : DELAY_BETWEEN_QUESTIONS;
            Timer timer = new Timer(delay, event -> {
                if (questionsAnswered < QUESTION_COUNT) {
                    // prepare the next question
                    question = model.getQuestion();
                    List<String> answers = new ArrayList<>(Arrays.asList(question.getAnswers()));
                    questionText.setText(question.getQuestion());

                    for (JButton answerButton : answerButtons) {
                        Collections.shuffle(answers);
                        answerButton.setText(answers.get(0));
                        answerButton.setBackground(Color.LIGHT_GRAY);
                        answerButton.setBorder(BorderFactory.createEmptyBorder());
                        answers.remove(0);
                    }

                    answerLoggedIn = false;
                    countdown.setCounter(countdown.getMaximum());
                    countdown.restart();
                    repaint();
                    revalidate();
                } else {
                    gameFrame.setContentPane(gameOverPanel = new GameOverPanel(gameFrame, model));
                    repaint();
                    revalidate();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void showAnswers(String[] answers) {
        for (int k = 0; k < answers.length; k++) {
            for (int j = 0; j < answerButtons.length; j++) {
                if (answerButtons[j].getText().equals(answers[k])) {
                    // first answer is correct
                    answerButtons[j].setBackground((k == 0) ? Color.GREEN : Color.RED);
                }
            }
        }
    }
}