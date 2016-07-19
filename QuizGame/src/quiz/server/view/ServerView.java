package quiz.server.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import quiz.Utils;
import quiz.server.Server;
import quiz.server.model.IDataManager;

/**
 * @author Stefan
 * @version 16.07.2016
 */
public final class ServerView extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JLabel portLabel;
	private JTextField port;
	private JToggleButton start;
	private JToggleButton stop;
	private JButton clear;
	private JTextArea information;
	private JScrollPane scroll;
	private JTextArea output;

	private IDataManager dataManager;
	private Server server;

	/**
	 * Creates an instance of ServerView.
	 */
	public ServerView()
	{
		setPreferredSize(new Dimension(400, 300));
		setLayout(null);

		initComponents();
		initListeners();
	}

	private void initComponents()
	{
		add(portLabel = new JLabel("PORT: "));
		portLabel.setBounds(5, 5, 40, 25);

		add(port = new JTextField("1819"));
		port.setBounds(45, 5, 50, 26);

		add(start = new JToggleButton("START"));
		start.setBounds(100, 5, 80, 25);

		add(stop = new JToggleButton("STOP"));
		stop.setEnabled(false);
		stop.setBounds(185, 5, 80, 25);

		add(clear = new JButton("CLEAR SCREEN"));
		clear.setMargin(new Insets(0, 0, 0, 0));
		clear.setBounds(270, 5, 125, 25);

		add(information = new JTextArea("QuizGame is coded by  : 'Alex, Eric, Quirin, Stefan'\nQuestions are created by : 'unknown Guys'\nIf the Server is closed, Matches and Match-Requests are NOT saved. Only Accounts and their score will be available after restarting!"));
		information.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		information.setFont(new Font("Arial", Font.BOLD, 12));
		information.setLineWrap(true);
		information.setWrapStyleWord(true);
		information.setEditable(false);
		information.setBounds(5, 35, 390, 63);

		add(scroll = new JScrollPane(output = new JTextArea()));
		OutputPrintStream.get(output);
		output.setEditable(false);
		scroll.setBounds(5, 103, 390, 192);
	}

	private boolean selected = false;

	private void initListeners()
	{
		start.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (selected)
				{
					start.setSelected(true);
					return;
				}

				try
				{
					int p = Integer.parseInt(port.getText());
					server = Server.start(dataManager, p);

					if (server != null) stop.setEnabled(true);

					port.setEditable(false);
					portLabel.setForeground(Color.BLACK);
					start.setSelected(true);
				} catch (Exception exc)
				{
					portLabel.setForeground(Color.RED);
					start.setSelected(false);
				}

				selected = start.isSelected();
			}
		});

		stop.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				stop();
				port.setEditable(true);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				output.setText("");
			}
		});
	}

	/**
	 * Stops the Server.
	 */
	public void stop()
	{
		if (server != null) server.close();
		System.out.println("Server closed!");

		start.setSelected(false);
		stop.setSelected(false);
		selected = false;
		stop.setEnabled(false);
		clear.setEnabled(true);
	}

	/**
	 * Opens the ServerView.
	 * 
	 * @param dataManager
	 *            the IDataManager instance
	 */
	public void open(IDataManager dataManager)
	{
		SwingUtilities.invokeLater(() -> {
			this.dataManager = dataManager;

			JFrame frame = new JFrame("QuizGame - Server");
			frame.setIconImage(Utils.loadIcon());
			frame.setResizable(false);
			frame.add(this);
			frame.pack();
			frame.setLocationRelativeTo(null);

			frame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					stop();
					dataManager.close();
					System.exit(0);
				}
			});

			frame.setVisible(true);
		});
	}
}