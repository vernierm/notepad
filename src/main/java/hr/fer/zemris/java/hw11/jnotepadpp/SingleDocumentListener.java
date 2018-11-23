package hr.fer.zemris.java.hw11.jnotepadpp;

/**
 * An interface that provides what a {@link SingleDocumentListener} should
 * provide.
 * 
 * @author Marin
 *
 */
public interface SingleDocumentListener {

	/**
	 * Used to notify the observer that the modify status updated.
	 * 
	 * @param model
	 *            is the updated model.
	 */
	void documentModifyStatusUpdated(SingleDocumentModel model);

	/**
	 * Used to notify the observer that the file path updated.
	 * 
	 * @param model
	 *            is the updated model.
	 */
	void documentFilePathUpdated(SingleDocumentModel model);
	
}
