package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;

/**
 * An interface used to represent a model that has zero, one ore more documents.
 * Offers view of the current document. The document that the user modifies.
 * 
 * @author Marin
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {

	/**
	 * Creates a new {@link SingleDocumentModel}.
	 * 
	 * @return a new model.
	 */
	SingleDocumentModel createNewDocument();

	/**
	 * Returns the {@link SingleDocumentModel} that is currently focused.
	 * 
	 * @return the current model.
	 */
	SingleDocumentModel getCurrentDocument();

	/**
	 * Loads the document from the given path.
	 * 
	 * @param path
	 *            is the given path.
	 * @return a model representing the loaded file.
	 */
	SingleDocumentModel loadDocument(Path path);

	/**
	 * Saves the model to the path specified.
	 * 
	 * @param model
	 *            is the model to save.
	 * @param newPath
	 *            is the path to save to.
	 */
	void saveDocument(SingleDocumentModel model, Path newPath);

	/**
	 * Closes the specified document model.
	 * 
	 * @param model
	 *            is the given model.
	 */
	void closeDocument(SingleDocumentModel model);

	/**
	 * Attached {@link MultipleDocumentListener} to the model.
	 * 
	 * @param l
	 *            is the listener to attach.
	 */
	void addMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * Removes {@link MultipleDocumentListener} from the model.
	 * 
	 * @param l
	 *            is the listener do detach.
	 */
	void removeMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * Returns the number of documents.
	 * 
	 * @return the number.
	 */
	int getNumberOfDocuments();

	/**
	 * A getter for document at index specified.
	 * 
	 * @param index
	 *            is the index specified.
	 * @return the wanted model.
	 */
	SingleDocumentModel getDocument(int index);
}
