package quiz.client.view;

import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;
import quiz.model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static quiz.Constants.*;

/**
 * @author Eric
 * @version 10.05.16
 */
public class GameOverPanel extends JPanel implements IView {

    private IModel model;
    private GameFrame gameFrame;
    private ResourceBundle localization = GameFrame.getLocalization();

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
        model.addView(this);
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if(type == ChangeType.MATCH) {
            Match match = model.getMatch();
            if(match != null) {
                Question[] questions = match.getQuestions();
                if(questions.length >= QUESTION_COUNT) {
                    GridBagConstraints c = new GridBagConstraints();

                    c.fill = GridBagConstraints.BOTH;
                    c.gridx = 3;
                    c.gridy = 0;
                    c.anchor = GridBagConstraints.CENTER;
                    c.insets = new Insets(10, 10, 10, 10);
                    JLabel categoryLabel = new JLabel(match.getCategory().toString(), SwingConstants.CENTER);
                    categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 22));
                    add(categoryLabel, c);

                    int rows = questions.length / QUESTIONS_PER_ROW_AND_PLAYER + 2;
                    for (int a = 0; a < match.getOpponents().length; a++) {
                        c.gridx = (a == 0 ? 1 : 5);
                        c.gridy = 0;
                        Account account = match.getOpponents()[a];
                        JLabel result = new JLabel("", SwingConstants.CENTER);
                        setFont(account, result);
                        if(match.getWinner() != null)
                            result.setText(match.getWinner().getID() == account.getID() ?
                                    localization.getString("WINNER") : localization.getString("LOSER"));
                        else
                            result.setText(localization.getString("DRAW"));
                        add(result, c);

                        c.gridy = 1;
                        c.insets = new Insets(10, 10, 50, 10);
                        JLabel name = new JLabel(account.getName() + " (" + localization.getString("SCORE")
                                +  ": " + account.getScore() + ")", SwingConstants.CENTER);
                        setFont(account, name);
                        add(name, c);

                        for (int y = 2, count = 0; y < rows; y++) {
                            c.gridy = y;
                            c.insets = new Insets(10, 0, 10, 0);
                            c.anchor = GridBagConstraints.CENTER;
                            c.fill = GridBagConstraints.NONE;

                            for (int x = a * (QUESTIONS_PER_ROW_AND_PLAYER + 1); x <
                                    a * (QUESTIONS_PER_ROW_AND_PLAYER + 1) + QUESTIONS_PER_ROW_AND_PLAYER; x++, count++) {
                                c.gridx = x;

                                Question question = match.getQuestions()[count];
                                int answerIndex = match.getAnswers()[a][count];
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
                    c.fill = GridBagConstraints.BOTH;

                    JButton menu = new JButton(GameFrame.getLocalization().getString("MAIN_MENU"));
                    menu.addActionListener(e -> gameFrame.setContentPane(gameFrame.getMenuPanel()));
                    add(menu, c);
                    GameFrame.setProperties(new Dimension(75, 40), new Dimension(100, 50), new Dimension(125, 60), menu);

                    Timer timer = new Timer(DELAY_BETWEEN_QUESTIONS - 500, event -> {
                        SwingUtilities.invokeLater(() -> {
                            gameFrame.setContentPane(this);
                            gameFrame.repaint();
                            gameFrame.revalidate();
                            gameFrame.getMenuPanel().getQuestionPanel().reset();
                        });
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }

    private void setFont(Account account, JComponent component) {
        if(account.getID() == model.getAccount().getID()) {
            Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
            fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            component.setFont(component.getFont().deriveFont(Font.BOLD, 20).deriveFont(fontAttributes));
        }
        else
            component.setFont(component.getFont().deriveFont(20f));
    }
}