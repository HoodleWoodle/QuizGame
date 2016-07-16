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
 * @version 13.07.16
 */
public class ChallengeDialog extends JDialog implements IView {

    private ResourceBundle localization = GameFrame.getLocalization();
    private IControl control;
    private IModel model;
    private JButton challenge;
    private JTextField opponentName;
    private GameFrame gameFrame;
    private JComboBox<String> categories;
    private boolean searchOpponent = true;
    private Dimension bigSize = new Dimension(180, 200);
    private Dimension smallSize = new Dimension(180, 150);

    /**
     * Creates a new ChallengeDialog.
     *
     * @param gameFrame the GameFrame
     * @param control the IControl
     * @param model the IModel
     */
    public ChallengeDialog(GameFrame gameFrame, IControl control, IModel model) {
        this.gameFrame = gameFrame;
        this.control = control;
        this.model = model;

        setTitle(localization.getString("CHALLENGE"));
        setModal(true);
        setSize(bigSize);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        initComponents();
        initListeners();

        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Returns whether the dialog contains a text field for the opponent name.
     *
     * @return whether the dialog contains a text field for the opponent name.
     */
    public boolean isSearchOpponent() {
        return searchOpponent;
    }

    /**
     * Sets whether the dialog contains a text field for the opponent name.
     *
     * @param searchOpponent whether the dialog contains a text field for the opponent name.
     */
    public void setSearchOpponent(boolean searchOpponent) {
        this.searchOpponent = searchOpponent;
        opponentName.setVisible(searchOpponent);
        setSize(searchOpponent ? bigSize : smallSize);
    }

    /**
     * Resets the dialog.
     */
    public void reset() {
        categories.setSelectedIndex(0);
        opponentName.setText("");
        getRootPane().setDefaultButton(challenge);
        setVisible(true);
    }

    private void initComponents() {
        add(Box.createVerticalGlue());
        add(categories = new JComboBox<>());
        Arrays.stream(Category.values()).forEach(category -> categories.addItem(category.toString()));
        add(Box.createVerticalGlue());
        add(opponentName = new JTextField(""));
        add(Box.createVerticalGlue());
        add(new JSeparator());
        add(Box.createVerticalGlue());
        add(challenge = new JButton(localization.getString("CHALLENGE")));
        add(Box.createVerticalGlue());

        GameFrame.setProperties(new Dimension(75, 20), new Dimension(100, 30), new Dimension(125, 40), categories,
                challenge, opponentName);
    }

    private void initListeners() {
        challenge.addActionListener(e -> {
            Category category = Category.values()[categories.getSelectedIndex()];
            if (searchOpponent && opponentName.getText() != null) {
                if(opponentName.getText().trim().isEmpty())
                    return;

                Account[] opponents = model.getOpponents();

                for (Account opponent : opponents) {
                    if (opponent.getName().equals(opponentName.getText())) {
                        control.requestMatch(category, opponent);
                        setVisible(false);
                        return;
                    }
                }

                // only existing players can accept a match
                MessageFormat formatter = new MessageFormat(localization.getString("PLAYER_DOES_NOT_EXIST"));
                gameFrame.showExceptionMessage(formatter.format(new Object[]{opponentName.getText()}));
            } else {
                control.requestMatch(category);
                setVisible(false);
            }
        });
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if(type == ChangeType.REQUESTS && status == Status.NO_OPPONENTS_AVAILABLE) {
            gameFrame.showExceptionMessage(localization.getString("NO_OPPONENTS_AVAILABLE"));
        }
    }
}