package quiz;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Stefan
 * @version 21.07.2016
 */
public final class ImageResourceLoader
{
	private static ImageResourceLoader instance;

	/**
	 * A Singleton.
	 * 
	 * @return the instance of ImageResourceLoader.
	 */
	public static ImageResourceLoader getInstance()
	{
		if (instance == null) instance = new ImageResourceLoader();
		return instance;
	}

	private Image icon;
	private Icon[] icons;

	private ImageResourceLoader()
	{
		icons = new Icon[3];

		try
		{
			URL url = getClass().getResource("/icon_image.png");
			if (url != null) icon = ImageIO.read(url);
			url = getClass().getResource("/icon_online.png");
			if (url != null) icons[0] = new ImageIcon(ImageIO.read(url));
			url = getClass().getResource("/icon_not_available.png");
			if (url != null) icons[1] = new ImageIcon(ImageIO.read(url));
			url = getClass().getResource("/icon_offline.png");
			if (url != null) icons[2] = new ImageIcon(ImageIO.read(url));
		} catch (IOException e)
		{
		}
	}

	/**
	 * Returns the Image of the game-icon.
	 * 
	 * @return the Image of the game-icon
	 */
	public Image getIcon()
	{
		return icon;
	}

	/**
	 * Returns an icon.
	 * 
	 * @param index
	 *            the index of the desired icon
	 * @return the desired icon
	 */
	public Icon getIcon(int index)
	{
		return icons[index];
	}
}