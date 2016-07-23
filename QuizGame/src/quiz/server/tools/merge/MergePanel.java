package quiz.server.tools.merge;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import quiz.model.Question;

/**
 * @author Stefan
 * @version 23.07.2016
 */
public final class MergePanel extends JScrollPane
{
	private static final long serialVersionUID = 1L;

	private final static JPanel panel = new JPanel();
	private MergeComponent[] components;

	private final MergeTool tool;

	private List<Question[]> toMerge;

	MergePanel(MergeTool tool)
	{
		super(panel);

		this.tool = tool;

		panel.setLayout(null);
	}

	private void setMerge()
	{
		if (toMerge.size() == 0)
		{
			tool.switchPanels();
			tool.success();
			return;
		}

		Question[] toMerge = this.toMerge.remove(0);

		if (components == null || components.length != toMerge.length)
		{
			removeAll();

			components = new MergeComponent[toMerge.length];
			for (int i = 0; i < components.length; i++)
			{
				add(components[i] = new MergeComponent(this));
				components[i].setBounds(5, i * 100 + 5, 590, 120);
			}
		}

		for (int i = 0; i < components.length; i++)
			components[i].setQuestion(toMerge[i]);
	}

	void setMerge(Question question)
	{
		tool.setMerge(question);
		setMerge();
	}

	void setToMerge(List<Question[]> toMerge)
	{
		this.toMerge = toMerge;
		setMerge();
	}
}