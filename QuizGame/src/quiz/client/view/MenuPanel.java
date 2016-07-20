package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Category;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author Eric
 * @version 1.05.16
 */
public class MenuPanel extends JPanel implements IView {

    private JLabel gameTitle;
    private IModel model;
    private IControl control;
    private final QuestionPanel questionPanel;
    private GameFrame gameFrame;
    private ResourceBundle localization = GameFrame.getLocalization();
    private JButton challenge;
    private JTextField opponentNameField;
    private JLabel opponentName, category;
    private JComboBox<String> categories;
    private JCheckBox randomOpponent, randomCategory;

    /**
     * Creates a new MenuPanel.
     *
     * @param gameFrame the current GameFrame instance
     * @param control   the IControl implementation
     * @param model     the IModel implementation
     */
    public MenuPanel(GameFrame gameFrame, IControl control, IModel model) {
        this.gameFrame = gameFrame;
        this.model = model;
        this.control = control;

        model.addView(this);
        questionPanel = new QuestionPanel(gameFrame, control, model);

        setLayout(new GridBagLayout());
        initComponents();
    }

    /**
     * Returns the QuestionPanel.
     *
     * @return the QuestionPanel
     */
    public QuestionPanel getQuestionPanel() {
        return questionPanel;
    }

    /**
     * Returns the text field for the opponent's name.
     *
     * @return the text field for the opponent's name
     */
    public JTextField getOpponentName() {
        return opponentNameField;
    }

    private void initComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.25;
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        JScrollPane playerListScrollPane = new JScrollPane(new PlayerListPanel(gameFrame, model));
        JLabel players = new JLabel(localization.getString("PLAYERS") + ":");
        players.setHorizontalAlignment(JLabel.CENTER);

        playerListScrollPane.setColumnHeaderView(players);
        playerListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(playerListScrollPane, c);

        c.gridx = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        add(createMainPart(), c);

        JScrollPane matchRequestScrollPane = new JScrollPane(new MatchRequestListPanel(gameFrame, control, model));
        JLabel matchRequests = new JLabel(localization.getString("MATCH_REQUESTS") + ":");
        matchRequests.setHorizontalAlignment(JLabel.CENTER);
        matchRequestScrollPane.setColumnHeaderView(matchRequests);
        matchRequestScrollPane.setBorder(BorderFactory.createEmptyBorder());

        c.gridx = GridBagConstraints.RELATIVE;
        c.weightx = 0.25;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        add(matchRequestScrollPane, c);

        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.CENTER;
    }

    private JPanel createMainPart() {
        JPanel mainPart = new JPanel();
        mainPart.setLayout(new BoxLayout(mainPart, BoxLayout.PAGE_AXIS));
        mainPart.add(Box.createVerticalGlue());
        mainPart.add(gameTitle = new JLabel(localization.getString("GAME_NAME")));
        gameTitle.setAlignmentX(CENTER_ALIGNMENT);
        gameTitle.setFont(new Font("Arial", Font.BOLD, 40));

        mainPart.add(Box.createVerticalGlue());
        mainPart.add(opponentName = new JLabel(localization.getString("OPPONENT_NAME") + ":"));
        opponentName.setFont(opponentName.getFont().deriveFont(Font.BOLD, 14));
        mainPart.add(opponentNameField = new JTextField(""));
        mainPart.add(randomOpponent = new JCheckBox(localization.getString("RANDOM_OPPONENT")));

        mainPart.add(Box.createVerticalStrut(10));
        mainPart.add(category = new JLabel(localization.getString("CATEGORY") + ":"));
        category.setFont(category.getFont().deriveFont(Font.BOLD, 14));
        mainPart.add(categories = new JComboBox<>());
        Arrays.stream(Category.values()).forEach(category -> categories.addItem(category.toString()));
        mainPart.add(randomCategory = new JCheckBox(localization.getString("RANDOM_CATEGORY")));

        mainPart.add(Box.createVerticalGlue());
        mainPart.add(challenge = new JButton(localization.getString("CHALLENGE")));
        gameFrame.getRootPane().setDefaultButton(challenge);
        mainPart.add(Box.createVerticalGlue());

        GameFrame.setProperties(new Dimension(125, 30), new Dimension(150, 35), new Dimension(175, 40), categories,
                challenge, opponentNameField, randomCategory, randomOpponent, opponentName, category);

        challenge.addActionListener(e -> {
            if(!randomCategory.isSelected() && !randomOpponent.isSelected()) {
                searchOpponent(opponentNameField.getText(), Category.values()[categories.getSelectedIndex()]);
            } else if(!randomCategory.isSelected())
                control.requestMatch(Category.values()[categories.getSelectedIndex()]);
            else if(!randomOpponent.isSelected())
                searchOpponent(opponentNameField.getText(), null);
            else
                control.requestMatch();
        });

        randomCategory.addItemListener(e -> categories.setEnabled(!randomCategory.isSelected()));
        randomOpponent.addItemListener(e -> opponentNameField.setEnabled(!randomOpponent.isSelected()));

        return mainPart;
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if (type == ChangeType.MATCH && model.getMatch() != null)
            gameFrame.setContentPane(questionPanel);

        if(type == ChangeType.REQUESTS && status == Status.NO_OPPONENTS_AVAILABLE)
            gameFrame.showExceptionMessage(localization.getString("NO_OPPONENTS_AVAILABLE"));

        if(type == ChangeType.ACCOUNT)
            gameFrame.setTitle(localization.getString("GAME_NAME") + " - " + model.getAccount().getName()
                    + "(" + localization.getString("SCORE") + ":" + model.getAccount().getScore() + ")");
    }

    private void searchOpponent(String opponentName, Category category) {
        if (opponentName != null && !opponentName.trim().isEmpty()) {
            Account[] opponents = model.getOpponents();

            for (Account opponent : opponents) {
                if (opponent.getName().equals(opponentName)) {
                    if(category == null)
                        control.requestMatch(opponent);
                    else
                        control.requestMatch(category, opponent);
                    return;
                }
            }

            // only existing players can accept a match
            MessageFormat formatter = new MessageFormat(localization.getString("PLAYER_DOES_NOT_EXIST"));
            gameFrame.showExceptionMessage(formatter.format(new Object[]{opponentName}));
        }
    }
}