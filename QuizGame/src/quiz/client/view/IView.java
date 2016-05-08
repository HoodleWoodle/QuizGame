package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;

/**
 * @author Alex
 * @version 03.05.2016
 */
public interface IView
{
	/**
	 * Initializes the IView.
	 * 
	 * @param model
	 *            the IModel instance
	 * @param control
	 *            the IControl instance
	 */
	void init(IModel model, IControl control);

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