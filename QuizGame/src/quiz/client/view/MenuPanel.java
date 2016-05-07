package quiz.client.view;

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
import javax.swing.JSeparator;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
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

	/**
	 * Creates a new MenuPanel.
	 */
	public MenuPanel() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		add(Box.createHorizontalGlue());
		add(createMainPart());
		add(Box.createHorizontalGlue());
		add(new JSeparator(JSeparator.VERTICAL));
		add(createMatchRequests());
	}
	
	private JPanel createMainPart() {
		JPanel mainPart = new JPanel();
		mainPart.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
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
	
	private JPanel createMatchRequests() {
		JPanel matchRequests = new JPanel();
		matchRequests.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
		
		matchRequests.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		matchRequests.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		return matchRequests;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == menuButtons[0]) {
			String opponentName = JOptionPane.showInputDialog(null, "Suche nach dem Gegner?");

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
	public void onChange(ChangeType type) {
		if (type == ChangeType.MATCH) {
			Match match = model.getMatch();
			for (Account account : match.getOpponents()) {
				// set unavailable during match
				account.setAvailable(false);
			}
		}
		else if(type == ChangeType.REQUESTS) {
			Match[] matchRequests = model.getRequests();
		}
	}
}