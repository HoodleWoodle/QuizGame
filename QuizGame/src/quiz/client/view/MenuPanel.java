package quiz.client.view;

import static quiz.Constants.FRAME_HEIGHT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;

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
	private final String[] MENU_BUTTON_NAMES = { localization.getString("SEARCH_OPPONENT"),
			localization.getString("RANDOM_OPPONENT") };
	private JButton[] menuButtons = new JButton[MENU_BUTTON_NAMES.length];

	/**
	 * Creates a new MenuPanel.
	 *
	 * @param gameFrame
	 *            the current GameFrame instance
	 * @param control
	 *            the IControl implementation
	 * @param model
	 *            the IModel implementation
	 */
	public MenuPanel(GameFrame gameFrame, IControl control, IModel model) {
		this.gameFrame = gameFrame;
		this.model = model;
		this.control = control;

		model.addView(this);
		questionPanel = new QuestionPanel(gameFrame, control, model);
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
		if (event.getSource() == menuButtons[0]) {
			String opponentName = JOptionPane.showInputDialog(gameFrame, localization.getString("SEARCH_FOR_OPPONENT"));

			if (opponentName != null) {
				Account[] opponents = model.getOpponents();

				boolean exists = false;
				for (Account opponent : opponents) {
					if (opponent != null) {
						if (opponent.getName().equals(opponentName)) {
							// only online players can accept a match
							if (!opponent.isOnline()) {
								gameFrame.showExceptionMessage(localization.getString("PLAYER_NOT_ONLINE"));
								return;
							}

							// only available players can accept a match
							if (!opponent.isAvailable()) {
								MessageFormat formatter = new MessageFormat(
										localization.getString("PLAYER_ALREADY_IN_MATCH"));
								gameFrame.showExceptionMessage(formatter.format(new Object[] { opponentName }));
								return;
							}

							control.requestMatch(opponent);
							exists = true;
							break;
						}
					}
				}

				// only existing players can accept a match
				if (!exists) {
					MessageFormat formatter = new MessageFormat(localization.getString("PLAYER_DOES_NOT_EXIST"));
					gameFrame.showExceptionMessage(formatter.format(new Object[] { opponentName }));
				}
			}
		} else if (event.getSource() == menuButtons[1])
			control.requestMatch();
	}

    @Override
    public void onChange(ChangeType type, Status status) {
        if (type == ChangeType.MATCH) {
            Match match = model.getMatch();
            if (match != null) {
                for (Account account : match.getOpponents()) {
                    // set unavailable during match
                    account.setAvailable(false);
                }

                gameFrame.setContentPane(questionPanel);
            }
        }
    }
}