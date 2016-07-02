package quiz.client.view;

import static quiz.Constants.FRAME_HEIGHT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
public class MatchRequestListPanel extends JPanel implements IView {

	private IModel model;
	private IControl control;
	private List<Match> lastMatchRequests;
	private GameFrame gameFrame;
	private ResourceBundle localization = GameFrame.getLocalization();

	/**
	 * Creates a new MatchRequestListPanel.
	 *
	 * @param gameFrame the current GameFrame instance
	 * @param control the IControl implementation
	 * @param model the IModel implementation
	 */
	public MatchRequestListPanel(GameFrame gameFrame, IControl control, IModel model) {
		this.gameFrame = gameFrame;
		this.control = control;
		this.model = model;

		model.addView(this);
		setMinimumSize(new Dimension(100, FRAME_HEIGHT - 200));
		setPreferredSize(new Dimension(150, FRAME_HEIGHT - 100));
		setMaximumSize(new Dimension(200, FRAME_HEIGHT));

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		lastMatchRequests = new ArrayList<>();
	}

	@Override
	public void onChange(ChangeType type, Status status) {
		if (type == ChangeType.REQUESTS) {
			if (status == Status.ALREADY_IN_MATCH) {
				gameFrame.showExceptionMessage(localization.getString("EXCEPTION_ALREADY_IN_MATCH"));
				return;
			}

			List<Match> matchRequests = Arrays.asList(model.getRequests());

			// add all new match requests
			List<Match> newMatchRequests = new ArrayList<Match>(matchRequests);
			newMatchRequests.removeAll(lastMatchRequests);

			for (Match matchRequest : newMatchRequests) {
				MatchRequestPanel matchRequestPanel = new MatchRequestPanel(matchRequest);
				add(matchRequestPanel);
			}

			// remove all old match requests
			lastMatchRequests.removeAll(matchRequests);

			for (Match matchRequest : lastMatchRequests) {
				for (ListIterator<Component> it = Arrays.asList(getComponents()).listIterator(); it.hasNext();) {
					MatchRequestPanel next = (MatchRequestPanel) it.next();
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

	/**
	 * 
	 * @author Eric
	 * @version 22.05.16
	 */
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
				if (account.getID() != gameFrame.getUser().getID()) {
					opponent = account;
					break;
				}
			}

			MessageFormat formatter = new MessageFormat(localization.getString("RECEIVED_MATCHREQUEST"));
			JTextArea textArea = new JTextArea(formatter.format(opponent.getName()));
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			add(textArea);
			add(Box.createVerticalGlue());

			Box row = Box.createHorizontalBox();
			row.add(Box.createHorizontalGlue());

			JButton accept = new JButton(localization.getString("ACCEPT"));
			accept.addActionListener(e -> control.acceptRequest(matchRequest));
			row.add(accept);
			row.add(Box.createHorizontalGlue());

			JButton deny = new JButton(localization.getString("DENY"));
			deny.addActionListener(e -> control.denyRequest(matchRequest));
			row.add(deny);
			row.add(Box.createHorizontalGlue());

			add(row);
			add(Box.createVerticalGlue());
		}
	}
}