package quiz;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Stefan, Eric
 * @version 21.07.2016
 */
public final class ImageResourceLoader
{
	/**
	 * Returns the Image of the game-icon.
	 * 
	 * @return the Image of the game-icon
	 */
	public static Image loadIcon()
	{
		try
		{
			URL url = ImageResourceLoader.class.getResource("/icon_image.png");
			if (url != null) return ImageIO.read(url);
		} catch (IOException e)
		{
		}

		return null;
	}

	/**
	 * Returns the standard icon image.
	 *
	 * @return the standard icon image
	 */
	public static Image loadStandardImage()
	{
		try
		{
			URL url = ImageResourceLoader.class.getResource("/standard.png");
			if (url != null) return ImageIO.read(url);
		} catch (IOException e)
		{
		}

		return null;
	}

	/**
	 * Returns the in-game icons.
	 * 
	 * @return the desired icons
	 */
	public static Icon[] loadIcons()
	{
		try
		{
			Icon[] icons = new Icon[3];
			URL url = ImageResourceLoader.class.getResource("/icon_online.png");
			if (url != null) icons[0] = new ImageIcon(ImageIO.read(url));
			url = ImageResourceLoader.class.getResource("/icon_not_available.png");
			if (url != null) icons[1] = new ImageIcon(ImageIO.read(url));
			url = ImageResourceLoader.class.getResource("/icon_offline.png");
			if (url != null) icons[2] = new ImageIcon(ImageIO.read(url));

			return icons;
		} catch (IOException e)
		{
		}

		return null;
	}
}