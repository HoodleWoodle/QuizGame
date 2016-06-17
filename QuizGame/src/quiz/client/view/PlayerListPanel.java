package quiz.client.view;

import static quiz.Constants.FRAME_HEIGHT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

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
public class PlayerListPanel extends JPanel implements IView {

	private Map<Account, PlayerPanel> accounts = new HashMap<Account, PlayerPanel>();
	private IModel model;
	private IControl control;

	/**
	 * Creates a new PlayerListPanel.
	 */
	public PlayerListPanel(IControl control, IModel model) {
		this.control = control;
		this.model = model;

		setMinimumSize(new Dimension(100, FRAME_HEIGHT - 200));
		setPreferredSize(new Dimension(150, FRAME_HEIGHT - 100));
		setMaximumSize(new Dimension(200, FRAME_HEIGHT));

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		// update account statuses
		if (type == ChangeType.OPPONENTS && status != Status.NO_OPPONENTS_AVAILABLE) {
			for (Account account : model.getOpponents()) {
				if (account.isOnline()) {
					if (!accounts.containsKey(account)) {
						PlayerPanel playerPanel = new PlayerPanel(account);
						accounts.put(account, playerPanel);
						add(playerPanel);
					} else
						accounts.get(account).updateStatus();

				} else if (accounts.containsKey(account)) {
					remove(accounts.get(account));
					accounts.remove(account);
				}
			}
		}
	}

	/**
	 * 
	 * @author Eric
	 * @version 22.05.16
	 */
	private class PlayerPanel extends JPanel {

		private final Account account;
		private final JLabel status;

		/**
		 * Creates a new PlayerPanel with the given Account.
		 * 
		 * @param account
		 *            the Account
		 */
		public PlayerPanel(Account account) {
			this.account = account;

			setMinimumSize(new Dimension(100, 50));
			setPreferredSize(new Dimension(150, 50));
			setMaximumSize(new Dimension(200, 50));
			setLayout(new BorderLayout());

			add(status = new JLabel(), BorderLayout.CENTER);

			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem matchRequest = new JMenuItem("Herausfordern");
			matchRequest.addActionListener(event -> {
				if (!account.isAvailable())
					control.requestMatch(account);
			});

			popupMenu.add(matchRequest);
			add(popupMenu);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					if (event.isPopupTrigger())
						popupMenu.show(PlayerPanel.this, event.getX(), event.getY());
				}
			});
		}

		/**
		 * Reflects the current status of the account in view.
		 */
		public void updateStatus() {
			status.setText(account.getName() + (!account.isAvailable() ? "(Im Spiel)" : "(Online)"));
		}
	}
}