package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.JMenu;

/**
 * An implementation of the {@link JMenu} that supports localization.
 * 
 * @author Marin
 *
 */
public class LJMenu extends JMenu {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A key used to get the name from the {@link LocalizationProvider}.
	 */
	private String key;
	/**
	 * The given {@link LocalizationProvider}.
	 */
	private ILocalizationProvider lp;

	/**
	 * The {@link LJMenu} contructor.
	 * 
	 * @param key
	 *            is the given key for the name.
	 * @param lp
	 *            is the given {@link ILocalizationProvider}.
	 */
	public LJMenu(String key, ILocalizationProvider lp) {
		this.key = key;
		this.lp = lp;
		updateMenu();
		lp.addLocalizationListener(new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				updateMenu();
			}
		});
	}

	/**
	 * An auxiliary method that updates the {@link JMenu} values.
	 */
	private void updateMenu() {
		setText(lp.getString(key));
	}

}
