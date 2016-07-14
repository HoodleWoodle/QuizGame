package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import static quiz.Constants.FRAME_HEIGHT;

/**
 * @author Eric
 * @version 1.05.16
 */
public class MenuPanel extends JPanel implements ActionListener, IView {

    private JLabel gameTitle;
    private IModel model;
    private IControl control;
    private final QuestionPanel questionPanel;
    private GameFrame gameFrame;
    private ResourceBundle localization = GameFrame.getLocalization();
    private final String[] MENU_BUTTON_NAMES = {localization.getString("SEARCH_OPPONENT"),
            localization.getString("RANDOM_OPPONENT")};
    private JButton[] menuButtons = new JButton[MENU_BUTTON_NAMES.length];
    private ChallengeDialog challengeDialog;

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
        challengeDialog = new ChallengeDialog(gameFrame, control, model);
        setMinimumSize(new Dimension(200, FRAME_HEIGHT));
        setPreferredSize(new Dimension(250, FRAME_HEIGHT));
        setMaximumSize(new Dimension(300, FRAME_HEIGHT));

        setLayout(new BorderLayout());
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

    private void initComponents() {
        JScrollPane playerListScrollPane = new JScrollPane(new PlayerListPanel(control, model));
        JLabel players = new JLabel(localization.getString("PLAYERS") + ":");
        players.setHorizontalAlignment(JLabel.CENTER);

        playerListScrollPane.setColumnHeaderView(players);
        playerListScrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(playerListScrollPane, BorderLayout.LINE_START);
        add(createMainPart(), BorderLayout.CENTER);

        JScrollPane matchRequestScrollPane = new JScrollPane(new MatchRequestListPanel(gameFrame, control, model));
        JLabel matchRequests = new JLabel(localization.getString("MATCH_REQUESTS") + ":");
        matchRequests.setHorizontalAlignment(JLabel.CENTER);

        matchRequestScrollPane.setColumnHeaderView(matchRequests);
        matchRequestScrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(matchRequestScrollPane, BorderLayout.LINE_END);
    }

    private JPanel createMainPart() {
        JPanel mainPart = new JPanel();
        mainPart.setLayout(new BoxLayout(mainPart, BoxLayout.PAGE_AXIS));
        mainPart.add(Box.createVerticalGlue());
        mainPart.add(gameTitle = new JLabel(localization.getString("GAME_NAME")));
        gameTitle.setAlignmentX(CENTER_ALIGNMENT);
        gameTitle.setFont(new Font("TimesNewRoman", Font.BOLD, 32));

        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i] = new JButton(MENU_BUTTON_NAMES[i]);
            menuButtons[i].addActionListener(this);
            GameFrame.setStandardProperties(menuButtons[i]);
            mainPart.add(Box.createVerticalGlue());
            mainPart.add(menuButtons[i]);
        }
        mainPart.add(Box.createVerticalGlue());

        return mainPart;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        challengeDialog.setSearchOpponent(event.getSource() == menuButtons[0]);
        challengeDialog.reset();
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if (type == ChangeType.MATCH && model.getMatch() != null)
            gameFrame.setContentPane(questionPanel);
    }
}