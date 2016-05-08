package quiz.client.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;

/**
 * @author Eric
 * @version 7.05.16
 */
public class MatchRequestPanel extends JPanel implements IView {

	private final Match matchRequest;
	private IModel model;
	private IControl control;
	
	/**
	 * Creates a new MatchRequestPanel.
	 * 
	 * @param matchRequest the matchRequest
	 */
	public MatchRequestPanel(Match matchRequest) {
		this.matchRequest = matchRequest;
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		setPreferredSize(new Dimension(300, 200));
		setMinimumSize(new Dimension(280, 200));
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		initComponents();
	}
	
	/**
	 * Returns the match request as a Match.
	 * 
	 * @return the Match
	 */
	public Match getMatchRequest() {
		return matchRequest;
	}

	private void initComponents() {
		Account opponent = null;
		for(Account account : matchRequest.getOpponents()) {
			if(account.getID() != GameFrame.getInstance().getUser().getID()) {
				opponent = account;
				break;
			}
		}
		
		int option = JOptionPane.showInternalConfirmDialog(this, "Du hast eine Herausforderung von " + opponent.getName() + " erhalten!", "", JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION) {
			control.acceptRequest(matchRequest);
		}
		else if(option == JOptionPane.NO_OPTION) {
			setEnabled(false);	
		}
		
	}

	@Override
	public void init(IModel model, IControl control) {
		this.model = model;
		this.control = control;
	}

	@Override
	public void onChange(ChangeType type, Status status) {

	}
}