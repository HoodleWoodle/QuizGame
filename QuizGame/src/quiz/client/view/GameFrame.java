package quiz.client.view;

import static quiz.Constants.FRAME_HEIGHT;
import static quiz.Constants.FRAME_WIDTH;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import quiz.model.Account;

/**
 * 
 * @author Eric
 * @version 1.05.16
 */
public final class GameFrame extends JFrame {

	private static GameFrame instance;

	private final MenuPanel menuPanel;
	private Account user;

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
		super("Quiz Game");
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setContentPane(menuPanel = new MenuPanel());
		setResizable(false);

		try {
			setIconImage(ImageIO.read(Paths.get("data").resolve("icon_image.png").toFile()));
		} catch (IOException e) {
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				// during a game
				if (user != null && !user.isAvailable()) {
					JOptionPane.showConfirmDialog(GameFrame.this,
							"Wenn du QuizGame während des Matches beendest, verlierst du dadurch sofort. Möchstest du das Spiel wirklich schon beenden?",
							"QuizGame beenden", JOptionPane.YES_NO_OPTION);
					return;
				}

				dispose();
				System.exit(1);
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		new LoginDialog();
	}

	/**
	 * Returns the currently logged in user.
	 * 
	 * @return the currently logged in user
	 */
	public Account getUser() {
		return user;
	}

	/**
	 * Sets the currently logged in user.
	 * 
	 * @param user
	 *            the currently logged in user
	 */
	public void setUser(Account user) {
		this.user = user;
	}

	/**
	 * Returns the MenuPanel.
	 * 
	 * @return the MenuPanel
	 */
	public MenuPanel getMenuPanel() {
		return menuPanel;
	}
}