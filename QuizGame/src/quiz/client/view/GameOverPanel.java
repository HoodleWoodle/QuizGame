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

        initComponents();
    }

    private void initComponents() {
        Match match = model.getMatch();
        Question[] questions = match.getQuestions();
        int[][] answers = match.getAnswers();
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 50, 10);
        JLabel categoryLabel = new JLabel(match.getCategory().toString(), SwingConstants.CENTER);
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 20));
        add(categoryLabel, c);

        int rows = match.getQuestions().length / QUESTIONS_PER_ROW_AND_PLAYER;
        for (int a = 0; a < match.getOpponents().length; a++) {
            c.gridx = (a == 0 ? 1 : 5);
            c.gridy = 0;
            c.insets = new Insets(10, 10, 50, 10);
            JLabel name = new JLabel(match.getOpponents()[a].getName(), SwingConstants.CENTER);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 20));
            add(name, c);

            for (int y = 1, count = 0; y < rows + 1; y++) {
                c.gridy = y;
                c.insets = new Insets(0, 0, 0, 0);

                for (int x = a * (QUESTIONS_PER_ROW_AND_PLAYER + 1); x <
                        a *(QUESTIONS_PER_ROW_AND_PLAYER + 1) + 3; x++, count++) {
                    c.gridx = x;

                    Question question = questions[count];
                    int answerIndex = answers[a][count];
                    String answer = question.getAnswers()[answerIndex];

                    JButton button = new JButton();
                    button.setFocusable(false);
                    button.setBorderPainted(false);
                    // correct answer is 0
                    button.setBackground((answerIndex == 0) ? Color.GREEN : Color.RED);
                    button.setToolTipText(answer);
                    add(button, c);
                    GameFrame.setProperties(new Dimension(75, 40), new Dimension(100, 50), new Dimension(125, 60), button);
                }
            }
        }

        c.gridy = rows + 1;
        c.gridx = 3;
        c.insets = new Insets(50, 0, 50, 0);

        JButton menu = new JButton(GameFrame.getLocalization().getString("MAIN_MENU"));
        menu.addActionListener(e -> gameFrame.setContentPane(gameFrame.getMenuPanel()));
        add(menu, c);
        GameFrame.setProperties(new Dimension(75, 40), new Dimension(100, 50), new Dimension(125, 60), menu);
    }
}