package quiz.server.tools.merge;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import quiz.ImageResourceLoader;
import quiz.Utils;
import quiz.model.Question;
import quiz.server.model.DataManager;

/**
 * @author Stefan
 * @version 23.07.2016
 */
public final class MergeTool extends JFrame
{
	private static final long serialVersionUID = 1L;

	private MainPanel main;
	private MergePanel merge;

	private boolean isMerge = true;

	DataManager dataManager;

	private MergeTool()
	{
		// initialize components
		initComponents();
	}

	private void initComponents()
	{
		main = new MainPanel(this);
		merge = new MergePanel(this);

		switchPanels();
	}

	void switchPanels()
	{
		remove(main);
		remove(merge);

		if (isMerge) add(main);
		else add(merge);

		isMerge = !isMerge;

		repaint();
		revalidate();
	}

	void setToMerge(List<Question[]> toMerge)
	{
		merge.setToMerge(toMerge);
	}

	void setMerge(Question question)
	{
		if (dataManager == null) return;

		dataManager.removeQuestion(question);
		dataManager.addQuestion(question);
	}

	void success()
	{
		JOptionPane.showMessageDialog(null, "Merge was successful!");
		dataManager.close();
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
			// initialize frame
			MergeTool frame = new MergeTool();
			frame.setTitle(Utils.TITLE_MERGE_TOOL);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setIconImage(ImageResourceLoader.loadIcon());
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}