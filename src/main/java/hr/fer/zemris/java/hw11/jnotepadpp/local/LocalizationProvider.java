package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The implementation of the {@link AbstractLocalizationProvider}. Implemented
 * like a singleton object. Provides localization, by the currently set
 * language.
 * 
 * @author Marin
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {

	/**
	 * The {@link LocalizationProvider}. Instanced only once.
	 */
	private static LocalizationProvider provider = new LocalizationProvider();
	/**
	 * The currently set language.
	 */
	private String language;
	/**
	 * The resource bundle used to reach the {@link Locale} specific values.
	 */
	private ResourceBundle bundle;
	
	/**
	 * The {@link LocalizationProvider} constructor. Notice it is private(singleton).
	 */
	private LocalizationProvider() {
		setLanguage("en");
	}
	
	/**
	 * A static getter for the {@link LocalizationProvider} instance.
	 * 
	 * @return the instance of the {@link LocalizationProvider}.
	 */
	public static LocalizationProvider getInstance() {
		return provider;
	}
	
	/**
	 * A setter for the current language.
	 * 
	 * @param language
	 *            is the specified language. Must be given in a form that
	 *            {@link Locale} can parse, i.e. for english use "en".
	 */
	public void setLanguage(String language) {
		this.language = language;
		Locale locale = Locale.forLanguageTag(this.language);
		bundle = ResourceBundle.getBundle("hr.fer.zemris.java.hw11.jnotepadpp.translations", locale);
		fire();
	}
	
	@Override
	public String getString(String key) {
		return bundle.getString(key);
	}
}
