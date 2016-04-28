package quiz.server.tools;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import quiz.server.model.DataManager;

/**
 * @author Stefan
 */
public final class MasterTool extends JPanel
{
	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * The JTabbedPane.
	 */
	private JTabbedPane pane;
	/**
	 * The Panel for Question-controlling.
	 */
	private QuestionPanel question;
	/**
	 * The Panel for Account-controlling.
	 */
	private AccountPanel account;

	/**
	 * Creates an instance of MasterTool.
	 */
	public MasterTool()
	{
		// initialize components
		initComponents();
		// initialize listeners
		initListeners();
	}

	/**
	 * Initializes components.
	 */
	private void initComponents()
	{
		// initialize pane
		pane = new JTabbedPane();
		pane.setPreferredSize(new Dimension(600, 400));
		add(pane);

		/* I */DataManager dataManager = new DataManager();

		// initialize panels
		pane.add("Questions", question = new QuestionPanel(dataManager));
		pane.add("Accounts", account = new AccountPanel(dataManager));
	}

	/**
	 * Initializes listener.
	 */
	private void initListeners()
	{
		pane.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				// update panel with focus
				switch (pane.getSelectedIndex())
				{
				case 0:
					question.update();
					break;
				case 1:
					account.update();
					break;
				}
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// initialize frame
		JFrame frame = new JFrame("QuizGame - MasterTool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(new MasterTool());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}