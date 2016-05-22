package quiz.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 * 
 * @author Eric
 * @version 1.05.16
 */
public class MenuPanel extends JPanel implements ActionListener, IView {

	private static final String[] MENU_BUTTON_NAMES = { "Gegner suchen", "Zufälliger Gegner" };
	private JButton[] menuButtons = new JButton[MENU_BUTTON_NAMES.length];
	private JLabel gameTitle;
	private IModel model;
	private IControl control;
	private final QuestionPanel questionPanel;

	/**
	 * Creates a new MenuPanel.
	 */
	public MenuPanel() {
		questionPanel = new QuestionPanel();
		setMinimumSize(new Dimension(200, Integer.MAX_VALUE));
		setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

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
		JScrollPane playerListScrollPane = new JScrollPane(new PlayerListPanel());
		JLabel players = new JLabel("Spieler:");
		players.setHorizontalAlignment(JLabel.CENTER);

		playerListScrollPane.setColumnHeaderView(players);
		playerListScrollPane.setBorder(BorderFactory.createEmptyBorder());

		add(playerListScrollPane, BorderLayout.LINE_START);
		add(createMainPart(), BorderLayout.CENTER);

		JScrollPane matchRequestScrollPane = new JScrollPane(new MatchRequestListPanel());
		JLabel matchRequests = new JLabel("Herausforderungen:");
		matchRequests.setHorizontalAlignment(JLabel.CENTER);

		matchRequestScrollPane.setColumnHeaderView(matchRequests);
		matchRequestScrollPane.setBorder(BorderFactory.createEmptyBorder());

		add(matchRequests, BorderLayout.LINE_END);
	}

	private JPanel createMainPart() {
		JPanel mainPart = new JPanel();
		mainPart.setLayout(new BoxLayout(mainPart, BoxLayout.PAGE_AXIS));
		mainPart.add(Box.createVerticalGlue());
		mainPart.add(gameTitle = new JLabel("Quiz Game"));
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
			String opponentName = JOptionPane.showInputDialog(null, "Suche nach einem Gegner?");

			if (opponentName != null) {
				Account[] opponents = model.getOpponents();

				boolean exists = false;
				for (Account opponent : opponents) {
					if (opponent.getName().equals(opponentName)) {
						// only online players can accept a match
						if (!opponent.isOnline()) {
							JOptionPane.showMessageDialog(null, "Der Spieler ist zurzeit leider nicht online!",
									"Fehler", JOptionPane.ERROR);
							return;
						}

						// only available players can accept a match
						if (!opponent.isAvailable()) {
							JOptionPane.showMessageDialog(null,
									"Der Spieler " + opponentName + " befindet sich zurzeit schon in einem Match!",
									"Fehler", JOptionPane.ERROR);
							return;
						}

						control.requestMatch(opponent);
						exists = true;
						break;
					}
				}

				// only existing players can accept a match
				if (!exists)
					JOptionPane.showMessageDialog(null, "Es gibt keinen Spieler mit dem Namen " + opponentName + "!");
			}
		} else if (event.getSource() == menuButtons[1]) {
			control.requestMatch();
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
			if (match != null) {
				for (Account account : match.getOpponents()) {
					// set unavailable during match
					account.setAvailable(false);
				}
			}

			GameFrame.getInstance().setContentPane(questionPanel);
		}
	}
}