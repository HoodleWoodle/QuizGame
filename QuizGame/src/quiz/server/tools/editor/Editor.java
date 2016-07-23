package quiz.server.tools.editor;

import static quiz.Constants.DATA;
import static quiz.Constants.DB_FILE;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import quiz.ImageResourceLoader;
import quiz.Utils;
import quiz.server.model.DataManager;
import quiz.server.model.IDataManager;

/**
 * @author Stefan
 * @version 29.04.2016
 */
final class Editor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final IDataManager dataManager;

	private JTabbedPane pane;
	private QuestionPanel question;
	private AccountPanel account;

	/**
	 * Creates an instance of MasterTool.
	 */
	private Editor()
	{
		dataManager = new DataManager(DATA, DB_FILE, true);

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
	 * Main-method.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Utils.initalizeLAF();

		SwingUtilities.invokeLater(() -> {
			Editor tool = new Editor();

			// initialize frame
			JFrame frame = new JFrame(Utils.TITLE_EDITOR);
			frame.setIconImage(ImageResourceLoader.loadIcon());
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
		});
	}
}