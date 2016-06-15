package quiz.net;

/**
 * @author Stefan
 * @version 14.06.2016
 */
public final class NetworkMessage
{
	public static final byte TAG_REGISTER = 0;
	public static final byte TAG_LOGIN = 1;
	public static final byte TAG_REQUEST = 2;
	public static final byte TAG_REQUEST_ACCEPT = 3;
	public static final byte TAG_REQUEST_DENY = 4;
	public static final byte TAG_SET_ANSWER = 5;

	public static final byte TAG_SET_ACCOUNT = 0;
	public static final byte TAG_SET_OPPONENTS = 1;
	public static final byte TAG_SET_REQUESTS = 2;
	public static final byte TAG_SET_MATCH = 3;
	public static final byte TAG_SET_QUESTION = 4;

	public static final String TAG_REQUEST_0 = "0";
	public static final String TAG_REQUEST_1 = "1";
	public static final String TAG_REQUEST_2 = "2";
	public static final String TAG_REQUEST_3 = "3";

	public static final String SPLIT = ";";
	public static final String SPLIT_SUB = ",";
	public static final String SPLIT_SUB_SUB = ":";
	public static final String SPLIT_SUB_SUB_SUB = ".";

	private final byte tag;
	private final String[] parameters;

	/**
	 * Creates an instance of NetworkMessage. (Parsing)
	 * 
	 * @param message
	 *            a received message
	 */
	public NetworkMessage(byte[] message)
	{
		String msg = new String(message);
		System.err.println(msg);
		String[] columns = msg.split(SPLIT);

		tag = Byte.parseByte(columns[0]);

		int parameterSize = columns.length - 1;
		parameters = new String[parameterSize];
		for (int i = 0; i < parameterSize; i++)
			parameters[i] = columns[i + 1];
	}

	/**
	 * Creates an instance of NetworkMessage. (Converting)
	 * 
	 * @param tag
	 *            the tag of the NetworkMessage
	 * @param parameters
	 *            the parameters of the NetworkMessage
	 */
	public NetworkMessage(byte tag, String[] parameters)
	{
		this.tag = tag;
		this.parameters = parameters;
	}

	/**
	 * Creates an instance of NetworkMessage. (Converting)
	 * 
	 * @param tag
	 *            the tag of the NetworkMessage
	 * @param parameter
	 *            the parameter of the NetworkMessage
	 */
	public NetworkMessage(byte tag, String parameter)
	{
		this(tag, new String[] { parameter });
	}

	/**
	 * Getter.
	 * 
	 * @return the tag of the NetworkMessage
	 */
	public byte getTag()
	{
		return tag;
	}

	/**
	 * Returns the count of parameters.
	 * 
	 * @return the count of parameters
	 */
	public int getParameterCount()
	{
		return parameters.length;
	}

	/**
	 * Returns a parameter by index.
	 * 
	 * @param index
	 *            the index of the desired parameter
	 * @return the desired parameter (null if Exception)
	 */
	public String getParameter(int index)
	{
		if (index < 0 || tag >= parameters.length)
			return null;

		return parameters[index];
	}

	/**
	 * Returns the bytes of the NetworkMessage.
	 * 
	 * @return the bytes of the NetworkMessage
	 */
	public byte[] getBytes()
	{
		final byte[] splitBytes = SPLIT.getBytes();

		int size = 1;
		for (String parameter : parameters)
			size += (splitBytes.length + parameter.length());

		byte[] bytes = new byte[size];

		bytes[0] = tag;
		int pointer = 1;
		for (String parameter : parameters)
		{
			for (byte b : parameter.getBytes())
				bytes[pointer++] = b;

			if (pointer < size)
				for (byte b : splitBytes)
					bytes[pointer++] = b;
		}

		System.err.println(new String(bytes));

		return bytes;
	}
}