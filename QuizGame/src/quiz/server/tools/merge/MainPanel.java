package quiz.server.tools.merge;

import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import quiz.Constants;
import quiz.model.Question;
import quiz.server.model.DataManager;

/**
 * @author Stefan
 * @version 23.07.2016
 */
public final class MainPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JList<File> files;
	private DefaultListModel<File> model;
	private JButton merge;

	private final MergeTool tool;
	private final List<Question> questions;

	MainPanel(MergeTool tool)
	{
		this.tool = tool;

		questions = new LinkedList<Question>();

		setLayout(null);

		// initialize components
		initComponents();
		// initialize listeners
		initListeners();
	}

	private void initComponents()
	{
		setPreferredSize(new Dimension(600, 400));

		JScrollPane scroll = new JScrollPane(files = new JList<File>(model = new DefaultListModel<File>()));
		files.setToolTipText("Drag and Drop is enabled for Directories with an quizDB file and maybe some images. (Only Questions are copied!)");
		scroll.setBounds(5, 5, 590, 360);
		add(scroll);

		try
		{
			new DropTarget(files, DnDConstants.ACTION_COPY, null).addDropTargetListener(new DropTargetHandler(this));
		} catch (TooManyListenersException e)
		{
		}

		add(merge = new JButton("merge"));
		merge.setBounds(240, 370, 120, 25);
	}

	private void initListeners()
	{
		merge.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) // BAD
			{
				if (model.getSize() == 0) return;

				File result = new File("result");
				if (result.exists()) delete(result);
				result.mkdirs();

				for (int i = 0; i < model.getSize(); i++)
				{
					File file = model.getElementAt(i);

					File db = null;
					for (File f : file.listFiles())
						if (isDBFile(f)) db = f;
						else
						{
							String name = f.getName();
							if (name.endsWith(".png") || name.endsWith(".jpg")) try
							{
								Files.copy(f.toPath(), new File(result.getAbsolutePath() + "/" + name).toPath());
							} catch (IOException exc)
							{
								exc.printStackTrace();
							}
						}
					DataManager dataManager = new DataManager(db.getParentFile().getAbsolutePath(), db.getAbsolutePath(), false);
					questions.addAll(dataManager.getQuestions());
					dataManager.close();
				}

				tool.dataManager = new DataManager(result.getAbsolutePath(), new File(result.getAbsolutePath() + Constants.DB_NAME).getAbsolutePath(), false);

				List<Question[]> toMerge = new LinkedList<Question[]>();

				for (Question question : questions)
					if (!tool.dataManager.addQuestion(question))
					{
						Question[] merge = null;
						for (Question[] qs : toMerge)
							if (qs[0].getQuestion().equals(question.getQuestion())) merge = qs;

						if (merge == null) merge = new Question[] { question, tool.dataManager.getQuestion(question.getQuestion()) };
						else
						{
							toMerge.remove(merge);

							Question[] temp = new Question[merge.length + 1];
							for (int i = 0; i < merge.length; i++)
								temp[i] = merge[i];
							temp[merge.length] = question;

							merge = temp;
						}

						toMerge.add(merge);
					}

				clear();
				if (toMerge.size() == 0) tool.success();
				else
				{
					tool.setToMerge(toMerge);
					tool.switchPanels();
				}
			}

			private void delete(File dir)
			{
				File[] files = dir.listFiles();
				for (File file : files)
					if (file.isDirectory()) delete(file);
					else file.delete();
			}
		});
	}

	private void clear()
	{
		model.clear();
		questions.clear();
	}

	void addFile(File file)
	{
		model.addElement(file);
	}

	boolean isDBFile(File file)
	{
		return file.getName().equals("quizDB.mv.db"); // BAD
	}
}