package quiz.client.view;

import quiz.client.IControl;
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
	 * On a change of model.
	 */
	void onChange();
}