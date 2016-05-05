package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;

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
	 * On change of the model.
	 * 
	 * @param types
	 *            the types of models which were changed
	 */
	void onChange(ChangeType[] types);
}