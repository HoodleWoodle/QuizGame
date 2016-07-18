package quiz.client.view;

import quiz.client.model.IModel;
import quiz.model.Match;
import quiz.model.Question;

import javax.swing.*;
import java.awt.*;

import static quiz.Constants.QUESTIONS_PER_ROW_AND_PLAYER;

/**
 * @author Eric
 * @version 10.05.16
 */
public class GameOverPanel extends JPanel {

    private IModel model;
    private GameFrame gameFrame;

    /**
     * Creates a new GameOverPanel.
     *
     * @param gameFrame the current GameFrame instance
     * @param model     the IModel implementation
     */
    public GameOverPanel(GameFrame gameFrame, IModel model) {
        this.model = model;
        this.gameFrame = gameFrame;
        setLayout(new GridBagLayout());
    }

    private void initComponents() {
        Match match = model.getMatch();
        Question[] questions = match.getQuestions();
        int[][] answers = match.getAnswers();
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 0;
        add(new JLabel(match.getCategory().toString()), c);

        int rows = match.getQuestions().length / QUESTIONS_PER_ROW_AND_PLAYER + 1;
        for (int a = 0; a < match.getOpponents().length; a++) {
            c.gridx = (a == 0 ? 1 : 5);
            add(new JLabel(match.getOpponents()[a].getName()), c);

            for (int y = 1, count = 0; y < rows; y++) {
                c.gridy = y;

                for (int x = a * (QUESTIONS_PER_ROW_AND_PLAYER + 1); x < x
                        + QUESTIONS_PER_ROW_AND_PLAYER; x++, count++) {
                    c.gridx = x;

                    Question question = questions[count];
                    int answerIndex = answers[a][count];
                    String answer = question.getAnswers()[answerIndex];

                    JButton button = new JButton("");
                    button.setFocusable(false);
                    button.setBorderPainted(false);
                    // correct answer is 0
                    button.setBackground((answerIndex == 0) ? Color.GREEN : Color.RED);
                    button.setToolTipText(answer);
                    add(button, c);
                }
            }
        }

        c.gridy = rows + 1;
        c.gridx = 3;

        JButton menu = new JButton(GameFrame.getLocalization().getString("MAIN_MENU"));
        menu.addActionListener(e -> gameFrame.setContentPane(gameFrame.getMenuPanel()));
        add(menu, c);
    }
}