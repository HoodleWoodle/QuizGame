package quiz.server.tools.merge;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

/**
 * @author Stefan
 * @version 20.07.2016
 */
final class DropTargetHandler implements DropTargetListener
{
	private final MainPanel mainPanel;

	DropTargetHandler(MainPanel mainPanel)
	{
		this.mainPanel = mainPanel;
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
	@SuppressWarnings("unchecked")
	public void drop(DropTargetDropEvent dtde)
	{
		Transferable transferable = dtde.getTransferable();
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			dtde.acceptDrop(dtde.getDropAction());
			try
			{
				List<File> data = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (data != null)
				{
					for (File file : data)
						if (isCorrectDirectory(file)) mainPanel.addFile(file);
					dtde.dropComplete(true);
				}
				return;
			} catch (Exception e)
			{
			}
		}
	}

	private boolean isCorrectDirectory(File file)
	{
		if (!file.isDirectory()) return false;

		File[] files = file.listFiles();
		for (File f : files)
			if (mainPanel.isDBFile(f)) return true;

		return false;
	}
}