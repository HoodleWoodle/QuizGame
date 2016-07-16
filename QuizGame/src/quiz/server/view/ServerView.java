package quiz.server.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import quiz.Constants;
import quiz.model.Category;
import quiz.model.Question;
import quiz.server.Server;
import quiz.server.model.DataManager;
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
		port.setBounds(50, 5, 50, 25);

		add(start = new JToggleButton("START"));
		start.setBounds(115, 5, 80, 25);

		add(stop = new JToggleButton("STOP"));
		stop.setEnabled(false);
		stop.setBounds(205, 5, 80, 25);

		add(clear = new JButton("CLEAR SCREEN"));
		clear.setMargin(new Insets(0, 0, 0, 0));
		clear.setBounds(295, 5, 100, 25);

		add(information = new JTextArea("QuizGame is coded by  : 'Alex, Eric, Quirin, Stefan'\nQuestions are made of : 'unknown Guys'\nIf the Server is closed, Matches and Match-Requests are NOT saved. Only Accounts and their score will be available after restarting!"));
		information.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		information.setFont(new Font("Arial", Font.BOLD, 12));
		information.setLineWrap(true);
		information.setWrapStyleWord(true);
		information.setEditable(false);
		information.setBounds(5, 35, 390, 68);

		add(scroll = new JScrollPane(output = new JTextArea()));
		OutputPrintStream.get(output);
		output.setEditable(false);
		scroll.setBounds(5, 107, 390, 190);
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

					if (server != null)
						stop.setEnabled(true);

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

	private void init(IDataManager dataManager)
	{
		this.dataManager = dataManager;
	}

	private void stop()
	{
		if (server != null)
			server.close();
		System.out.println("Server closed!");

		start.setSelected(false);
		stop.setSelected(false);
		selected = false;
		stop.setEnabled(false);
		clear.setEnabled(true);
	}

	/**
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO TEMP
		new File(Constants.DB_FILE).delete();

		ServerView serverView = new ServerView();
		IDataManager dataManager = new DataManager();
		serverView.init(dataManager);

		JFrame frame = new JFrame("QuizGame - Server");
		frame.setResizable(false);
		frame.add(serverView);
		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				serverView.stop();
				dataManager.close();
				System.exit(0);
			}
		});

		frame.setVisible(true);

		// TODO TEMP
		for (Category c : Category.values())
			for (int i = 0; i < Constants.QUESTION_COUNT + 1; i++)
				dataManager.addQuestion(new Question(c, c.toString() + "-question-" + i, new String[] { "correct", "incorrect-0", "incorrect-1", "incorrect-2" }));

		dataManager.addAccount("1", "1");
		dataManager.addAccount("2", "2");

		for (Category category : Category.values())
			if (dataManager.getQuestions(category).size() < Constants.QUESTION_COUNT)
			{
				System.out.println("Invalid question count ('" + category.toString() + "')!");
				return;
			}
	}
}