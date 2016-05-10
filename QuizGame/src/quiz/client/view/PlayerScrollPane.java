package quiz.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;

/**
 * 
 * @author Eric
 * @version 8.05.16
 */
public class PlayerScrollPane extends JScrollPane implements IView {

	private Map<Account, JPanel> accounts = new HashMap<Account, JPanel>();
	private IModel model;
	private IControl control;
	private JScrollPane scrollPane;
	private JLabel accountStatus;

	/**
	 * Creates a new PlayerListPanel.
	 */
	public PlayerScrollPane() {
		setMinimumSize(new Dimension(100, Integer.MAX_VALUE));
		setPreferredSize(new Dimension(100, Integer.MAX_VALUE));
		setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public void init(IModel model, IControl control) {
		this.model = model;
		this.control = control;
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		if (type == ChangeType.OPPONENTS && status != Status.NO_OPPONENTS_AVAILABLE) {
			for (Account account : model.getOpponents()) {
				if (account.isOnline()) {
					accountStatus.setText(account.getName() + (!account.isAvailable() ? "(Im Spiel)" : "(Online)"));
					
					if (!accounts.containsKey(account)) {
						JPanel playerPanel = createPlayerPanel(account);
						accounts.put(account, playerPanel);
						scrollPane.add(playerPanel);
					}
				} else if (accounts.containsKey(account)) {
					accounts.remove(account);
					scrollPane.remove(accounts.get(account));
				}
			}
		}
	}
	
	private JPanel createPlayerPanel(Account account) {
		JPanel playerPanel = new JPanel();
		playerPanel.setPreferredSize(new Dimension(100, 50));
		playerPanel.setLayout(new BorderLayout());

		playerPanel.add(accountStatus = new JLabel(account.getName() + (!account.isAvailable() ? "(Im Spiel)" : "(Online)")),
				BorderLayout.CENTER);

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem matchRequest = new JMenuItem("Herausfordern");
		matchRequest.addActionListener(event -> {
			if (!account.isAvailable())
				control.requestMatch(account);
		});

		popupMenu.add(matchRequest);
		playerPanel.add(popupMenu);
		return playerPanel;
	}
}