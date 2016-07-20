package quiz.server.tools.master;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import quiz.Constants;

/**
 * @author Stefan
 * @version 20.07.2016
 */
// bad practice
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
					File file = ((File) data.get(0));
					if (file.isFile())
					{
						String name = file.getName();
						if (name.endsWith(".png") || name.endsWith(".jpg"))
						{
							copyFile(file, new File(Constants.DATA + "/" + name));
							questionPanel.setImage(name);
							dtde.dropComplete(true);
						}
					}
				}
				return;
			} catch (Exception e)
			{
			}
		}
	}

	private void copyFile(File source, File dest) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		try
		{
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0)
				os.write(buffer, 0, length);
		} finally
		{
			is.close();
			os.close();
		}
	}
}