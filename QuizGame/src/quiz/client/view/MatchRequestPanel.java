package quiz.client.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import quiz.model.Account;
import quiz.model.Match;

/**
 * @author Eric
 * @version 7.05.16
 */
public class MatchRequestPanel extends JPanel {

	private JOptionPane confirm;
	private Match matchRequest;
	
	public MatchRequestPanel(Match matchRequest) {
		this.matchRequest = matchRequest;
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		setPreferredSize(new Dimension(300, 200));
		setMinimumSize(new Dimension(280, 200));
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		initComponents();
	}

	private void initComponents() {
		for(Account account : matchRequest.getOpponents()) {
			
		}
		
		int option = JOptionPane.showInternalConfirmDialog(this, "Du hast eine Herausforderung von ... erhalten!", "", JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION) {
			
		}
		else if(option == JOptionPane.NO_OPTION) {
			
		}
		
	}
}