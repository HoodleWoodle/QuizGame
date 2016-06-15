package quiz.client.view;

import quiz.client.model.ChangeType;
import quiz.client.model.Status;

/**
 * @author Alex
 * @version 03.05.2016
 */
public interface IView
{
	/**
	 * On change of the IModel.
	 * 
	 * @param type
	 *            the type of model which has changed
	 * @param status
	 *            the Status of the change
	 */
	void onChange(ChangeType type, Status status);
}