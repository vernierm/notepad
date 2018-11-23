package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.AbstractAction;

/**
 * The implementation of the {@link AbstractAction} that supports the
 * localization.
 * 
 * @author Marin
 *
 */
public abstract class LocalizableAction extends AbstractAction {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The key used to access the action name depending on localization.
	 */
	private String nameKey;
	/**
	 * The key used to access the action description depending on localization.
	 */
	private String descriptionKey;
	/**
	 * The key used to access the action mnemonic key depending on localization.
	 */
	private String mnemonicKey;
	/**
	 * The given {@link ILocalizationProvider}.
	 */
	private ILocalizationProvider lp;

	/**
	 * The {@link LocalizableAction} constructor.
	 * 
	 * @param nameKey
	 *            is the given name key.
	 * @param mnemonic
	 *            is the given mnemonic key key.
	 * @param descriptionKey
	 *            is the given description key.
	 * @param lp
	 *            is the given provider.
	 */
	public LocalizableAction(String nameKey, String mnemonic, String descriptionKey, ILocalizationProvider lp) {
		this.nameKey = nameKey;
		this.mnemonicKey = mnemonic;
		this.descriptionKey = descriptionKey;
		this.lp = lp;
		
		updateActionValues();
		lp.addLocalizationListener(new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				updateActionValues();
			}
		});
	}

	/**
	 * Updates the action name depending on the language.
	 */
	private void updateActionValues() {
		putValue(NAME, lp.getString(nameKey));
		putValue(SHORT_DESCRIPTION, lp.getString(descriptionKey));
		putValue(MNEMONIC_KEY, (int)lp.getString(mnemonicKey).charAt(0));
	}
}
