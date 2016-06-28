package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.IModel;
import quiz.model.Account;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import static quiz.Constants.FRAME_HEIGHT;
import static quiz.Constants.FRAME_WIDTH;

/**
 * 
 * @author Eric
 * @version 1.05.16
 */
public final class GameFrame extends JFrame {

	private static GameFrame instance;

	private static final ResourceBundle localization = ResourceBundle.getBundle("quiz.client.view.localization");
	private final MenuPanel menuPanel;
	private Account user;

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
	 * Sets the properties minimum Dimension @param min, preferred
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

	/**
	 * Returns the ResourceBundle containing the current localization.
	 *
	 * @return the ResourceBundle containing the current localization
     */
	public static ResourceBundle getLocalization() {
		return localization;
	}

	/**
	 * Creates a new GameFrame with the given implementation of IControl and IModel.
	 * @param control the control implementation
	 * @param model the model implementation
     */
	public GameFrame(IControl control, IModel model) {
		super(localization.getString("GAME_NAME"));
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setContentPane(menuPanel = new MenuPanel(this, control, model));
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
					int answer = JOptionPane.showConfirmDialog(GameFrame.this,
							localization.getString("CONFIRM_LEAVE"),
							localization.getString("CONFIRM_LEAVE_SCREEN_TITLE"), JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						dispose();
						System.exit(1);
						return;
					}
				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		new LoginDialog(this, control, model);
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
		setTitle(getTitle() + " - " + user.getName());
	}

	/**
	 * Returns the MenuPanel.
	 * 
	 * @return the MenuPanel
	 */
	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	public void showExceptionMessage(String message) {
		JOptionPane.showMessageDialog(this, message, localization.getString("EXCEPTION"), JOptionPane.ERROR_MESSAGE);
	}
}