package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * The {@link LocalizationProviderBridge} is the proxy object used to provide
 * the functionality of localization, but assures that all the components that
 * are mutually dependent with the {@link ILocalizationProvider} access the real
 * {@link LocalizationProvider} through this proxy object. This way the
 * attaching and detaching all the dependent components is easily done by
 * attaching or detaching this object to the real {@link LocalizationProvider}.
 * 
 * SEE: the proxy design pattern
 * 
 * @author Marin
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {

	/**
	 * The flag that provides information if the bridge is connected to the real
	 * {@link LocalizationProvider}.
	 */
	private boolean connected;
	/**
	 * The real {@link LocalizationProvider} subject.
	 */
	private ILocalizationProvider parent;
	/**
	 * The {@link ILocalizationListener} used to track if the localization has
	 * changed.
	 */
	private ILocalizationListener parentListener = new ILocalizationListener() {
		@Override
		public void localizationChanged() {
			fire();
		}
	};

	/**
	 * The {@link LocalizationProviderBridge} constructor.
	 * 
	 * @param parent
	 *            is the real subject.
	 */
	public LocalizationProviderBridge(ILocalizationProvider parent) {
		this.parent = parent;
	}

	/**
	 * Used to connect the this proxy object to the parent.
	 */
	public void connect() {
		if (connected)
			return;

		parent.addLocalizationListener(parentListener);
	}

	/**
	 * Used to disconnect from the parent.
	 */
	public void disconnect() {
		if (!connected)
			return;

		parent.removeLocalizationListener(parentListener);
	}

	/**
	 * Gets the value for the given string key, by delegating the job to the parent.
	 */
	@Override
	public String getString(String key) {
		return parent.getString(key);
	}
}
