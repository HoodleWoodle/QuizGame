package quiz.net;

/**
 * @author Stefan
 * @version 14.07.2016
 */
@SuppressWarnings("javadoc")
public interface NetworkKeys
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
	public static final byte TAG_SET_SENT_REQUESTS = 3;
	public static final byte TAG_SET_MATCH = 4;
	public static final byte TAG_SET_QUESTION = 5;

	public static final byte TAG_INVALID_REGISTER_DETAILS = 6;
	public static final byte TAG_INVALID_LOGIN_DETAILS = 7;
	public static final byte TAG_NO_OPPONENTS_AVAILABLE = 8;
	public static final byte TAG_OPPONENT_NOT_AVAILABLE = 9;
	public static final byte TAG_OPPONENT_DISCONNECTED = 10;
	public static final byte TAG_ALREADY_LOGGED_IN = 11;
	public static final byte TAG_ALREADY_REQUESTED = 12;

	public static final String TAG_REQUEST_0 = "0";
	public static final String TAG_REQUEST_1 = "1";
	public static final String TAG_REQUEST_2 = "2";
	public static final String TAG_REQUEST_3 = "3";

	public static final String SPLIT_SUB = ",";
	public static final String SPLIT_SUB_SUB = ">";
	public static final String SPLIT_SUB_SUB_SUB = "<";
}