package quiz.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import quiz.client.IControl;
import quiz.client.model.IModel;
import quiz.model.Account;

/**
 * 
 * @author Eric
 * @version 8.05.16
 */
public class PlayerListPanel extends JPanel {

	private IModel model;
	private IControl control;

	/**
	 * Creates a new PlayerListPanel.
	 * 
	 * @param control
	 *            the control
	 * @param model
	 *            the model
	 */
	public PlayerListPanel(IControl control, IModel model) {
		this.control = control;
		this.model = model;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
		setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(300, Integer.MAX_VALUE));

		initComponents();
	}

	private void initComponents() {
		JScrollPane scrollPane = new JScrollPane(this);

		for (Account account : model.getOpponents()) 
			if (account.isOnline()) 
				scrollPane.add(createPlayerPanel(account));

		add(scrollPane);
	}

	private JPanel createPlayerPanel(Account account) {
		JPanel playerPanel = new JPanel();
		playerPanel.setMinimumSize(new Dimension(300, 50));
		playerPanel.setMaximumSize(new Dimension(300, 50));
		playerPanel.setPreferredSize(new Dimension(300, 50));
		playerPanel.setLayout(new BorderLayout());
		playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		playerPanel.add(new JLabel(account.getName() + (!account.isAvailable() ? "(Im Spiel)" : "(Online)")),
				BorderLayout.CENTER);

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem matchRequest = new JMenuItem("Herausfordern");
		matchRequest.addActionListener(event -> {
			if(!account.isAvailable())
				control.requestMatch(account);
		});

		popupMenu.add(matchRequest);
		playerPanel.add(popupMenu);
		return playerPanel;
	}
}