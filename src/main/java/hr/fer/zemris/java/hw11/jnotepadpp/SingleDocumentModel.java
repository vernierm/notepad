package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;

import javax.swing.JTextArea;

/**
 * Represents the model of a single document. Contains all informations relevant
 * for working with the model, like: file path, modification status, ...
 * 
 * @author Marin
 *
 */
public interface SingleDocumentModel {

	/**
	 * A getter for the text component used for editing the document model.
	 * 
	 * @return the text component.
	 */
	JTextArea getTextComponent();

	/**
	 * A getter for the file path associated with the model.
	 * 
	 * @return the file path.
	 */
	Path getFilePath();

	/**
	 * A setter for the file path of the model.
	 * 
	 * @param path
	 *            the given {@link Path}.
	 */
	void setFilePath(Path path);

	/**
	 * Method used for checking if the model is modified.
	 * 
	 * @return true if is modified, false otherwise.
	 */
	boolean isModified();

	/**
	 * Sets the modified flag.
	 * 
	 * @param modified
	 *            is the flag to set.
	 */
	void setModified(boolean modified);

	/**
	 * Registers the given {@link SingleDocumentListener} to the model.
	 * 
	 * @param l
	 *            is the given listener.
	 */
	void addSingleDocumentListener(SingleDocumentListener l);

	/**
	 * Detaches the given listener from the model.
	 * 
	 * @param l
	 *            is the given listener.
	 */
	void removeSingleDocumentListener(SingleDocumentListener l);

}
