package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The implementation of the {@link SingleDocumentModel}. Represents one
 * document in the {@link JNotepadPP}.
 * 
 * @author Marin
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {
	
	/**
	 * The specified document file path. Can be <code>null</code> if the document is
	 * not saved yet.
	 */
	private Path filepath;
	/**
	 * The flag that represent if the document is modified.
	 */
	private boolean modified;
	/**
	 * The {@link JTextArea} of the model.
	 */
	private JTextArea textArea;
	/**
	 * The {@link SingleDocumentListener} attached to the model.
	 */
	private List<SingleDocumentListener> listeners;
	/**
	 * The {@link Iterator} used when working with listeners, used to avoid concurrency.
	 */
	private Iterator<SingleDocumentListener> listenersIterator;
	
	/**
	 * The model constructor.
	 * 
	 * @param filepath
	 *            is the given file path.
	 * @param textContent
	 *            is the given initial content of the document.
	 */
	public DefaultSingleDocumentModel(Path filepath, String textContent) {
		this.filepath = filepath;
		modified = false;
		listeners = new ArrayList<>();
		
		textArea = new JTextArea(textContent);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setModified(true);
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setModified(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setModified(true);
			}
		});
	}

	@Override
	public JTextArea getTextComponent() {
		return textArea;
	}

	@Override
	public Path getFilePath() {
		return filepath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException
	 *             if the given path is null.
	 */
	@Override
	public void setFilePath(Path path) {
		Objects.requireNonNull("Given path should not be null.");
		
		this.filepath = path;
		notifyPathChanged();
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	@Override
	public void setModified(boolean modified) {
		this.modified = modified;
		shareModificationStatus();
	}

	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		if (listeners.contains(l))
			return;
		
		listeners.add(l);
	}

	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		if (listenersIterator == null) {
			listeners.remove(l);
		} else {
			listenersIterator.remove();
		}
	}

	/**
	 * Used to notify the listeners about that the model is modified.
	 */
	private void shareModificationStatus() {
		listenersIterator = listeners.iterator();
		while(listenersIterator.hasNext()) {
			SingleDocumentListener l = listenersIterator.next();
			l.documentModifyStatusUpdated(this);
		}
		listenersIterator = null;
	}

	/**
	 * Used to notify the listeners that the model's path changed.
	 */
	private void notifyPathChanged() {
		listenersIterator = listeners.iterator();
		while(listenersIterator.hasNext()) {
			SingleDocumentListener l = listenersIterator.next();
			l.documentFilePathUpdated(this);
		}
		listenersIterator = null;
	}
}
