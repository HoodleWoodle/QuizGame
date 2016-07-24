package quiz.server.tools.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import quiz.Constants;

/**
 * @author Stefan
 * @version 20.07.2016
 */
final class DropTargetHandler implements DropTargetListener
{
	private final QuestionPanel questionPanel;

	DropTargetHandler(QuestionPanel questionPanel)
	{
		this.questionPanel = questionPanel;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
	}

	@Override
	public void dragExit(DropTargetEvent dte)
	{
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void drop(DropTargetDropEvent dtde)
	{
		Transferable transferable = dtde.getTransferable();
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			dtde.acceptDrop(dtde.getDropAction());
			try
			{
				List data = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (data != null && data.size() == 1)
				{
					File file = (File) data.get(0);
					String name = file.getName();
					File dest = new File(Constants.DATA + "/" + name);
					if (dest.exists() || QuestionPanel.copyFile(file, dest))
					{
						questionPanel.setImage(name);
						dtde.dropComplete(true);
					}
				}
				return;
			} catch (

			Exception e)
			{
			}
		}
	}
}