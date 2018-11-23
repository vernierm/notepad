package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A abstract implementation of the {@link ILocalizationProvider}. Implements
 * the methods associated with the listeners.
 * 
 * @author Marin
 *
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {

	/**
	 * A list of listeners attached to a provider.
	 */
	private List<ILocalizationListener> listeners;
	/**
	 * An iterator used for working with listeners. Used for the purpose of
	 * concurrency problem
	 */
	private Iterator<ILocalizationListener> listenersIterator;

	/**
	 * An {@link AbstractLocalizationProvider} constructor.
	 */
	public AbstractLocalizationProvider() {
		listeners = new ArrayList<>();
	}

	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		if (listenersIterator == null) {
			listeners.remove(listener);
		} else {
			listenersIterator.remove();
		}
	}

	/**
	 * Used to notify all the attached listeners that a localization change
	 * occurred.
	 */
	public void fire() {
		listenersIterator = listeners.iterator();
		while (listenersIterator.hasNext()) {
			ILocalizationListener l = listenersIterator.next();
			l.localizationChanged();
		}
		listenersIterator = null;
	}
}
