package hr.fer.zemris.java.hw11.jnotepadpp;

/**
 * An interface that specifies what a multiple document listener should provide.
 * 
 * @author Marin
 *
 */
public interface MultipleDocumentListener {

	/**
	 * This method is used to notify the listener that the current model has been
	 * changed.
	 * 
	 * @param previousModel
	 *            is the previous model.
	 * @param currentModel
	 *            is the new current model.
	 */
	void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel);

	/**
	 * A method used to notify the listener that a model has been added.
	 * 
	 * @param model
	 *            is the added model.
	 */
	void documentAdded(SingleDocumentModel model);

	/**
	 * A method used to notify the listener that a model has been removed.
	 * 
	 * @param model
	 *            is the removed model.
	 */
	void documentRemoved(SingleDocumentModel model);
}
