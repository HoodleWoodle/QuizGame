package quiz.server.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * @author Hoodle
 * @version 16.07.2016
 */
public final class OutputPrintStream extends OutputStream
{
	private final JTextArea area;

	private OutputPrintStream(JTextArea area)
	{
		this.area = area;
	}

	@Override
	public void write(int b) throws IOException
	{
		area.setText(area.getText() + (char) b);
	}

	/**
	 * Returns an instance of OutputPrintStream.
	 * 
	 * @param area
	 *            the JTextArea to write to
	 */
	public static void get(JTextArea area)
	{
		OutputPrintStream outputPrintStream = new OutputPrintStream(area);
		System.setOut(new PrintStream(outputPrintStream));
	}
}