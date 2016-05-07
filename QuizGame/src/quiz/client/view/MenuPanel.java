package quiz.client.view;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Eric
 * @version 1.05.16
 */
public class MenuPanel extends JPanel {
	
	private static final String[] MENU_BUTTON_NAMES = {"Gegner suchen", "Zufälliger Gegner", "Benutzer wechseln"};
	private JButton[] menuButtons = new JButton[MENU_BUTTON_NAMES.length];
	private JLabel gameTitle;

	/**
	 * Creates a new MenuPanel.
	 */
	public MenuPanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		initComponents();
		initListeners();
	}
	
	private void initComponents() {
		add(Box.createVerticalGlue());
		add(gameTitle = new JLabel("Quiz Game"));
		gameTitle.setAlignmentX(CENTER_ALIGNMENT);
		
		
		for(int i = 0; i < menuButtons.length; i++) {
			menuButtons[i] = new JButton(MENU_BUTTON_NAMES[i]);
			GameFrame.setStandardProperties(menuButtons[i]);
			add(Box.createVerticalGlue());
			add(menuButtons[i]);
		}
		add(Box.createVerticalGlue());
	}

	private void initListeners() {
		
	}
}