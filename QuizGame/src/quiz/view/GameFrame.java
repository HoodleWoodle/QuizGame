package quiz.view;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;

import quiz.client.view.IView;

/**
 * 
 * @author Eric
 * @version 1.05.16
 */
public final class GameFrame extends JFrame {

	/**
	 * Singleton. The GameFrame instance.
	 */
	public static GameFrame instance;

	private final MenuPanel menuPanel;
	private final QuestionPanel questionPanel;

	/**
	 * Singleton. Returns the instance of GameFrame.
	 * 
	 * @return the instance of GameFrame
	 */
	public static GameFrame getInstance() {
		if (instance == null)
			instance = new GameFrame();

		return instance;
	}

	/**
	 * Sets the standard properties for @param components.
	 * 
	 * @param components
	 *            the components to the set the standard properties for
	 */
	public static void setStandardProperties(JComponent... components) {
		setProperties(new Dimension(150, 30), new Dimension(200, 40), new Dimension(250, 50), components);
	}

	/**
	 * Sets the poperties minimum Dimension @param min, preferred
	 * Dimension @param preferred, and maximum Dimension @param max for the
	 * components @components.
	 * 
	 * @param min
	 *            the minimum Dimension
	 * @param preferred
	 *            the preferred Dimension
	 * @param max
	 *            the maximum Dimension
	 * @param components
	 *            the components to set the properties for
	 */
	public static void setProperties(Dimension min, Dimension preferred, Dimension max, JComponent... components) {
		for (JComponent component : components) {
			component.setMinimumSize(min);
			component.setPreferredSize(preferred);
			component.setMaximumSize(max);
			component.setAlignmentX(CENTER_ALIGNMENT);
		}
	}

	private GameFrame() {
		setSize(new Dimension(700, 700));
		setResizable(false);
		setLocationRelativeTo(null);

		setContentPane(menuPanel = new MenuPanel());
		setContentPane(questionPanel = new QuestionPanel());
		setVisible(true);
	}

	/**
	 * Returns the MenuPanel.
	 * 
	 * @return the MenuPanel
	 */
	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	/**
	 * Returns the QuestionPanel.
	 * 
	 * @return the QuestionPanel
	 */
	public QuestionPanel getQuestionPanel() {
		return questionPanel;
	}

	/**
	 * Returns the IView.
	 * 
	 * @return the IView
	 */
	public IView getView() {
		return getQuestionPanel();
	}

	/**
	 * Main.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		GameFrame.getInstance();
	}
}