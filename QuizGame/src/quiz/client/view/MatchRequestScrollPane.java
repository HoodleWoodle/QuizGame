package quiz.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;

/**
 * @author Eric
 * @version 10.05.16
 */
public class MatchRequestScrollPane extends JScrollPane implements IView {

	private IModel model;
	private IControl control;
	private List<Match> lastMatchRequests;
	private List<MatchRequestPanel> matchRequestPanels;

	/**
	 * Creates a new MatchRequestScrollPane.
	 */
	public MatchRequestScrollPane() {
		setMinimumSize(new Dimension(100, Integer.MAX_VALUE));
		setPreferredSize(new Dimension(150, Integer.MAX_VALUE));
		setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

		JLabel matchRequests = new JLabel("Herausforderungen:");
		matchRequests.setHorizontalAlignment(JLabel.CENTER);
		setColumnHeaderView(matchRequests);
		setBorder(BorderFactory.createEmptyBorder());
		lastMatchRequests = new ArrayList<>();
		matchRequestPanels = new ArrayList<>();
	}

	@Override
	public void init(IModel model, IControl control) {
		this.model = model;
		this.control = control;
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		if (type == ChangeType.REQUESTS) {
			if (status == Status.ALREADY_IN_MATCH) {
				JOptionPane.showMessageDialog(null, "Dieser Spieler befindet sich zurzeit schon in einem Match!",
						"Fehler", JOptionPane.ERROR);
				return;
			}

			List<Match> matchRequests = Arrays.asList(model.getRequests());

			// add all new match requests
			List<Match> newMatchRequests = new ArrayList<Match>(matchRequests);
			newMatchRequests.removeAll(lastMatchRequests);

			for (Match matchRequest : newMatchRequests) {
				MatchRequestPanel matchRequestPanel = new MatchRequestPanel(matchRequest);
				add(matchRequestPanel);
				matchRequestPanels.add(matchRequestPanel);
			}

			// remove all old match requets
			lastMatchRequests.removeAll(matchRequests);

			for (Match matchRequest : lastMatchRequests) {
				for (ListIterator<MatchRequestPanel> it = matchRequestPanels.listIterator(); it.hasNext();) {
					MatchRequestPanel next = it.next();
					if (next.matchRequest.getID() == matchRequest.getID()) {
						remove(next);
						break;
					}
				}
			}

			// update match requests
			lastMatchRequests = matchRequests;
		}
	}

	private class MatchRequestPanel extends JPanel {

		private final Match matchRequest;

		/**
		 * Creates a new MatchRequestPanel.
		 * 
		 * @param matchRequest
		 *            the matchRequest
		 */
		public MatchRequestPanel(Match matchRequest) {
			this.matchRequest = matchRequest;

			setMinimumSize(new Dimension(100, 200));
			setPreferredSize(new Dimension(150, 200));
			setMaximumSize(new Dimension(200, 200));
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));

			initComponents();
		}

		private void initComponents() {
			Account opponent = null;
			for (Account account : matchRequest.getOpponents()) {
				if (account.getID() != GameFrame.getInstance().getUser().getID()) {
					opponent = account;
					break;
				}
			}

			JTextArea textArea = new JTextArea("Du hast eine Herausforderung von " + opponent.getName() + " erhalten!");
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			add(textArea);
			add(Box.createVerticalGlue());

			Box row = Box.createHorizontalBox();
			row.add(Box.createHorizontalGlue());

			JButton accept = new JButton("Annehmen");
			accept.addActionListener(e -> control.acceptRequest(matchRequest));
			row.add(accept);
			row.add(Box.createHorizontalGlue());

			JButton deny = new JButton("Ablehnen");
			deny.addActionListener(e -> control.denyRequest(matchRequest));
			row.add(deny);
			row.add(Box.createHorizontalGlue());

			add(row);
			add(Box.createVerticalGlue());
		}
	}
}