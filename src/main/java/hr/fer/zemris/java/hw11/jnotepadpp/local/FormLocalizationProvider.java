package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * A class derived from the {@link LocalizationProviderBridge} and is used to
 * automatically connect and disconnect the {@link LocalizationProviderBridge}
 * from the {@link LocalizationProvider}. The connecting and disconnecting is
 * dependent on whether the {@link JFrame} is opened.
 * 
 * @author Marin
 *
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * The {@link FormLocalizationProvider} constructor.
	 * 
	 * @param provider
	 *            is the given {@link ILocalizationProvider}.
	 * @param frame
	 *            is the given {@link JFrame}.
	 */
	public FormLocalizationProvider(ILocalizationProvider provider, JFrame frame) {
		super(provider);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				FormLocalizationProvider.this.connect();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				FormLocalizationProvider.this.disconnect();
			}
		});
	}
}
