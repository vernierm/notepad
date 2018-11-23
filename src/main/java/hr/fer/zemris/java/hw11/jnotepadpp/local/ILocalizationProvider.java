package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * An interface that defines what every localization provider should implement.
 * 
 * @author Marin
 *
 */
public interface ILocalizationProvider {

	/**
	 * Registers the given listener to the provider.
	 * 
	 * @param listener
	 *            is the given {@link ILocalizationListener}.
	 */
	void addLocalizationListener(ILocalizationListener listener);

	/**
	 * Detaches the given listener from the provider.
	 * 
	 * @param listener
	 *            is the specified {@link ILocalizationListener}.
	 */
	void removeLocalizationListener(ILocalizationListener listener);

	/**
	 * Gets the string stored under given key.
	 * 
	 * @param key
	 *            is the given key.
	 * @return is the reached value.
	 */
	String getString(String key);
}
