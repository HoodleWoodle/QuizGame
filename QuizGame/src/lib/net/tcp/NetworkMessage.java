package lib.net.tcp;

/**
 * @author Stefan
 * @version 29.04.2016
 */
public final class NetworkMessage
{
	private static final String SPLIT = ";";

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
		String msg = new String(message, 1, message.length - 1);

		System.out.println("RECEIVING: " + message[0] + msg); // TODO

		tag = message[0];

		if (msg.isEmpty())
		{
			parameters = new String[0];
			return;
		}

		String[] columns = msg.split(SPLIT);
		int parameterSize = columns.length;
		parameters = new String[parameterSize];
		for (int i = 0; i < parameterSize; i++)
			parameters[i] = columns[i];
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
		if (index < 0 || index >= parameters.length)
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
			size += parameter.length();
		if (parameters.length > 0)
			size += (parameters.length - 1) * SPLIT.length();

		byte[] bytes = new byte[size];

		bytes[0] = tag;
		int pointer = 1;
		for (String parameter : parameters)
		{
			for (byte b : parameter.getBytes())
				bytes[pointer++] = b;

			if (pointer < size - 1)
				for (byte b : splitBytes)
					bytes[pointer++] = b;
		}

		System.out.println("SENDING: " + new String(bytes)); // TODO

		return bytes;
	}
}