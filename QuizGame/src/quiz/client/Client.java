package quiz.client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import lib.net.tcp.client.AbstractTCPClient;
import quiz.Constants;
import quiz.client.model.IModel;
import quiz.client.model.Model;
import quiz.client.view.GameFrame;

/**
 * @author Stefan
 * @version 08.06.2016
 */
public class Client extends AbstractTCPClient // TODO eigener Thread
{
	private final IModel model;

	/**
	 * Creates an instance of Client.
	 * 
	 * @param server
	 *            the server-name
	 * @param port
	 *            the server-port
	 */
	public Client(String server, int port)
	{
		super(server, port);
		this.model = new Model(new Control(this));
	}

	@Override
	protected void received(String message)
	{
		Model model = (Model) this.model;

		// TODO
	}

	@Override
	protected void closed()
	{
	}

	/**
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		// try to connect to Server
		Client client = new Client("localhost", 1818); // TODO
		boolean con = client.connect();

		// initialize LookAndFeel
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if (Constants.LOOK_AND_FEEL.equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e)
		{
			con = false;
		}

		// some Exception
		if (con == false)
		{
			JOptionPane.showMessageDialog(null, "Cannot start Client!");
			System.exit(1);
		}

		// start surface
		SwingUtilities.invokeLater(() -> {
			// Swing needs to run on event dispatching thread
				GameFrame.getInstance();
			});
	}
}