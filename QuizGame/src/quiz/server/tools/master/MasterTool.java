package quiz.server.tools.master;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class MasterTool extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final IDataManager dataManager;

	private JTabbedPane pane;
	private QuestionPanel question;
	private AccountPanel account;

	/**
	 * Creates an instance of MasterTool.
	 */
	MasterTool()
	{
		dataManager = new DataManager();

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

	public static void invalid()
	{
		JOptionPane.showMessageDialog(null, "Invalid Input!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		MasterTool tool = new MasterTool();

		// initialize frame
		JFrame frame = new JFrame("QuizGame - MasterTool");
		frame.setResizable(false);
		frame.add(tool);
		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				tool.dataManager.close();
				System.exit(0);
			}
		});

		frame.setVisible(true);
	}
}