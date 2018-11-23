package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class representing a model for working with multiple documents at once.
 * Extends {@link JTabbedPane} so those documents can be visualized as a tabbed
 * pane. Offers methods for opening, creating, saving... documents. Also offers
 * methods to attach listener that can listen to the change of the model state.
 * 
 * @author Marin
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A sign used to represent a new document, without name.
	 */
	public static final String ASTERISK = "*";
	
	/**
	 * A red diskette, used to represent a file that is not modified.
	 */
	private final ImageIcon RED_DISKETTE;
	/**
	 * A green diskette, used to represent a file that is modified.
	 */
	private final ImageIcon GREEN_DISKETTE;
	
	/**
	 * A list of currently opened models.
	 */
	private List<SingleDocumentModel> models;
	/**
	 * The model in focus.
	 */
	private SingleDocumentModel currentModel;
	/**
	 * A list of listener attached to the model.
	 */
	private List<MultipleDocumentListener> listeners;
	/**
	 * An iterator over listeners. Used to avoid concurrency.
	 */
	private Iterator<MultipleDocumentListener> listenersIterator;
	
	/**
	 * A multiple document model constructor.
	 */
	public DefaultMultipleDocumentModel() {
		RED_DISKETTE = loadIcon("icons/redDiskette.png");
		GREEN_DISKETTE = loadIcon("icons/greenDiskette.png");
		
		models = new ArrayList<>();
		listeners = new ArrayList<>();
		
		this.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				SingleDocumentModel previous = currentModel;
				int index = getSelectedIndex();
				if (index == -1) {
					currentModel = null;
				} else {
					currentModel = models.get(index);
				}
				notifyListenersDocumentChanged(previous, currentModel);
			}
		});
	}

	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return models.iterator();
	}

	@Override
	public SingleDocumentModel createNewDocument() {
		return addModelToPane(null, "");
	}
	
	@Override
	public SingleDocumentModel getCurrentDocument() {
		return currentModel;
	}

	@Override
	public SingleDocumentModel loadDocument(Path path) {
		Objects.requireNonNull(path);
		
		int index = getPathIndexIfExists(path);
		if (index != -1) {
			SingleDocumentModel old = getCurrentDocument();
			setSelectedIndex(index);
			notifyListenersDocumentChanged(old, currentModel);
			return currentModel;
		} else {
			byte[] bytes = null;
			try {
				bytes = Files.readAllBytes(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String text = new String(bytes, StandardCharsets.UTF_8);
			
			return addModelToPane(path, text);
		}
	}

	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {
		Path toSave;
		if (newPath != null) {
			toSave = newPath;
		} else {
			toSave = model.getFilePath();
		}

		byte[] bytes = model.getTextComponent().getText().getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(toSave, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		model.setModified(false);
		model.setFilePath(toSave);
		notifyListenersDocumentChanged(model, model);
	}

	@Override
	public void closeDocument(SingleDocumentModel model) {
		int index = models.indexOf(model);
		models.remove(index);
		remove(index);
		notifyListenersDocumentRemoved(model);

		notifyListenersDocumentChanged(model, currentModel);
	}

	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		if (listenersIterator == null) {
			listeners.remove(l);
		} else {
			listenersIterator.remove();
		}
	}

	@Override
	public int getNumberOfDocuments() {
		return models.size();
	}

	@Override
	public SingleDocumentModel getDocument(int index) {
		if(index == -1)
			return null;
		return models.get(index);
	}

	/**
	 * Notifies listeners that a document has been added.
	 * 
	 * @param model
	 *            is the new model.
	 */
	private void notifyListenersDocumentAdded(SingleDocumentModel model) {
		listenersIterator = listeners.iterator();
		while (listenersIterator.hasNext()) {
			MultipleDocumentListener l = listenersIterator.next();
			l.documentAdded(model);
		}
		listenersIterator = null;
	}
	
	/**
	 * Notifies listeners that a model has been removed.
	 * 
	 * @param model
	 *            is the removed model.
	 */
	private void notifyListenersDocumentRemoved(SingleDocumentModel model) {
		listenersIterator = listeners.iterator();
		while (listenersIterator.hasNext()) {
			MultipleDocumentListener l = listenersIterator.next();
			l.documentRemoved(model);
		}
		listenersIterator = null;
	}
	
	/**
	 * Notifies listeners that a document has been changed.
	 * 
	 * @param old
	 *            is the previous model.
	 * @param cur
	 *            is the new model.
	 */
	private void notifyListenersDocumentChanged(SingleDocumentModel old, SingleDocumentModel cur) {
		listenersIterator = listeners.iterator();
		while (listenersIterator.hasNext()) {
			MultipleDocumentListener l = listenersIterator.next();
			l.currentDocumentChanged(old, cur);
		}
		listenersIterator = null;
	}
	
	/**
	 * An auxiliary method that check if given path exists among the currently
	 * opened documents.
	 * 
	 * @param path
	 *            is the given path.
	 * @return index of model if exists, -1 if such path does not exist.
	 */
	private int getPathIndexIfExists(Path path) {
		for (int i = 0; i < models.size(); ++i) {
			if (path.equals(models.get(i).getFilePath()))
				return i;
		}
		return -1;
	}
	
	/**
	 * Adds model specified by given path and text to the tabbed pane.
	 * 
	 * @param path
	 *            is the path of the model to add, can be null if a new file is
	 *            being created.
	 * @param text
	 *            is the text to be set to the new model.
	 * @return the generated new model.
	 */
	private SingleDocumentModel addModelToPane(Path path, String text) {
		SingleDocumentModel newModel = new DefaultSingleDocumentModel(path, text);
		models.add(newModel);
		
		int index = models.indexOf(newModel);
		newModel.addSingleDocumentListener(new SingleDocumentListener() {
			
			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				int index = models.indexOf(model);
				if (model.isModified()) {
					setIconAt(index, GREEN_DISKETTE);
				} else {
					setIconAt(index, RED_DISKETTE);
				}
			}
			
			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				int index = models.indexOf(model);

				setTitleAt(index, model.getFilePath().getFileName().toString());
				setToolTipTextAt(index, model.getFilePath().toString());
			}
		});
		
		addTab(path == null ? ASTERISK : path.getFileName().toString(), 
				new JScrollPane(newModel.getTextComponent()));
		setToolTipTextAt(index, path == null ? ASTERISK : path.toString());
		setIconAt(index, RED_DISKETTE);
		setSelectedIndex(index);
		notifyListenersDocumentAdded(newModel);
		
		return newModel;
	}
	
	/**
	 * An auxiliary method used to load an {@link ImageIcon} from given name.
	 * 
	 * @param name
	 *            is the name of the file to load.
	 * @return the loaded icon.
	 */
	private ImageIcon loadIcon(String name) {
		byte[] bytes = null;
		try (InputStream is = this.getClass().getResourceAsStream(name)) {
			bytes = is.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Image im = new ImageIcon(bytes).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
		return new ImageIcon(im); 
	}
	
}
