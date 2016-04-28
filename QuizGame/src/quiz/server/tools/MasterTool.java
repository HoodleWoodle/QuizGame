package quiz.server.tools;

import quiz.server.model.DataManager;

/**
 * @author Stefan
 */
public final class MasterTool
{
	/**
	 * The IDataManager of the MasterTool.
	 */
	private final/* I */DataManager dataManager;

	/**
	 * Creates an instance of MasterTool.
	 */
	public MasterTool()
	{
		dataManager = new DataManager();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	}
}