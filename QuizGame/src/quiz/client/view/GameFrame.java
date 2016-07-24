package quiz.client.view;

import lib.net.tcp.client.AbstractTCPClient;
import quiz.ImageResourceLoader;
import quiz.client.IControl;
import quiz.client.model.IModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import static quiz.Constants.FRAME_HEIGHT;
import static quiz.Constants.FRAME_WIDTH;

/**
 * @author Eric, Stefan
 * @version 1.05.16
 */
public final class GameFrame extends JFrame {

    private static final ResourceBundle localization = ResourceBundle.getBundle("quiz.client.view.localization");
    private final MenuPanel menuPanel;
    private boolean available = true;

    /**
     * Sets the standard properties for @param components.
     *
     * @param components the components to the set the standard properties for
     */
    public static void setStandardProperties(JComponent... components) {
        setProperties(new Dimension(150, 30), new Dimension(200, 40), new Dimension(250, 50), components);
    }

    /**
     * Sets the properties minimum Dimension @param min, preferred
     * Dimension @param preferred, and maximum Dimension @param max for the
     * components @components.
     *
     * @param min        the minimum Dimension
     * @param preferred  the preferred Dimension
     * @param max        the maximum Dimension
     * @param components the components to set the properties for
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
	 * scale image
	 *
	 * @param img
	 *            image to scale
	 * @param minSize
	 *            the minimum size of the result-image
	 * @param maxSize
	 *            the maximum size of the result-image
	 * @return scaled image
	 */
	public static BufferedImage scale(BufferedImage img, Dimension minSize, Dimension maxSize)
	{
		if (img == null) return null;

		int width = img.getWidth();
		int height = img.getHeight();

		double[] sizes = { minSize.width, maxSize.width, minSize.height, maxSize.height };
		boolean[] conditions = { width < sizes[0], width > sizes[1], height < sizes[2], height > sizes[3] };
		double[] factors = new double[conditions.length];

		boolean sth = false;

		for (int i = 0; i < conditions.length; i++)
			if (conditions[i])
			{
				if (i < conditions.length / 2) factors[i] = sizes[i] / width;
				else factors[i] = sizes[i] / height;
				sth = true;
			}

		if (!sth) return img;

		double factor = 1;

		for (int i = 0; i < conditions.length; i++)
			if (conditions[i])
			{
				factor = factors[i];
				for (int j = 0; j < conditions.length; j++)
					if (conditions[j] && 1 - factors[i] < 1 - factors[j]) factor = factors[j];
			}

		BufferedImage result = new BufferedImage((int) (width * factor), (int) (height * factor), img.getType());
		Graphics2D g = result.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
		g.drawRenderedImage(img, at);

		return result;
	}

    /**
     * Creates a new GameFrame with the given implementation of IControl and IModel.
     *
     * @param client  the AbstractTCPClient implementation
     * @param control the control implementation
     * @param model   the model implementation
     */
    public GameFrame(AbstractTCPClient client, IControl control, IModel model) {
        super(localization.getString("GAME_NAME"));
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setContentPane(menuPanel = new MenuPanel(this, control, model));
        setResizable(false);

        setIconImage(ImageResourceLoader.loadIcon());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                // during a game
                if (model.getAccount() != null && !available) {
                    int answer = JOptionPane.showConfirmDialog(GameFrame.this,
                            localization.getString("CONFIRM_LEAVE"),
                            localization.getString("CONFIRM_LEAVE_SCREEN_TITLE"), JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION)
                        stop(client);
                } else
                    stop(client);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        new LoginDialog(this, control, model);
    }

    /**
     * Returns whether the client is available.
     *
     * @return whether the client is available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets whether the client is available.
     *
     * @param available whether the client is available
     */
    public void setAvailable(boolean available) {
        this.available = available;
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
     * Useful method for showing an exception.
     */
    public void showExceptionMessage(String message) {
        JOptionPane.showMessageDialog(this, message, localization.getString("EXCEPTION"), JOptionPane.ERROR_MESSAGE);
    }

    private void stop(AbstractTCPClient client) {
        dispose();
        client.close();
        System.exit(1);
    }
}