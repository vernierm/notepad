package hr.fer.zemris.java.hw11.jnotepadpp.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationListener;
import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;

/**
 * The class that encapsulates all the logic behind the status bar.
 * 
 * @author Marin
 *
 */
public class StatusBar extends JPanel {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A colon used in the output.
	 */
	private static final String COLON = " : ";
	
	/**
	 * The 'length' word used in output, separated for the purpose of localization.
	 */
	private String length;
	/**
	 * The 'line' word used in output, separated for the purpose of localization.
	 */
	private String line;
	/**
	 * The 'column' word used in output, separated for the purpose of localization.
	 */
	private String column;
	/**
	 * The 'selection' word used in output, separated for the purpose of localization.
	 */
	private String selection;
	
	/**
	 * The currently opened text area.
	 */
	private JTextArea currentTextArea;
	/**
	 * The label that outputs the current length.
	 */
	private JLabel lengthLabel;
	/**
	 * The label that outputs the current selection informations.
	 */
	private JLabel selectionLabel;
	/**
	 * The label that outputs the current time.
	 */
	private JLabel timeLabel;
	
	/**
	 * The timer object.
	 */
	private Timer t;
	
	/**
	 * The constructor of the {@link StatusBar}.
	 * 
	 * @param flp
	 *            is the given {@link ILocalizationProvider}.
	 */
	public StatusBar(ILocalizationProvider flp) {
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		setLayout(new GridLayout(1, 0));
		
		lengthLabel = new JLabel();
		selectionLabel = new JLabel();
		timeLabel = new JLabel();
		timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		add(lengthLabel);
		add(selectionLabel);
		add(timeLabel);

		updateNames(flp);
		updateDocument(null);
		
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		t = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Calendar c = Calendar.getInstance();
				timeLabel.setText(format.format(c.getTime()));
			}
		});
		t.start();

		flp.addLocalizationListener(new ILocalizationListener() {
			
			@Override
			public void localizationChanged() {
				updateNames(flp);
				updateDocument(currentTextArea);
			}
		});
		
	}
	
	/**
	 * The method used to update the names, depending on the current {@link Locale}.
	 * 
	 * @param flp
	 *            is the {@link ILocalizationProvider}.
	 */
	private void updateNames(ILocalizationProvider flp) {
		length = flp.getString("length");
		line = flp.getString("ln");
		column = flp.getString("col");
		selection = flp.getString("sel");
	}

	/**
	 * Updates the document area, whether the document is changed or the
	 * localization is changed.
	 * 
	 * @param textArea
	 *            is the given text area.
	 */
	public void updateDocument(JTextArea textArea) {
		this.currentTextArea = textArea;
		
		if(textArea == null) {
			lengthLabel.setText(length + COLON);
			selectionLabel.setText(String.format("%s : %s : %s : ", line, column, selection));
		} else {
			Document document = currentTextArea.getDocument();
			lengthLabel.setText(length + COLON + document.getLength());
			document.addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent arg0) {
					lengthLabel.setText(length + COLON + document.getLength());
				}
				
				@Override
				public void insertUpdate(DocumentEvent arg0) {
					lengthLabel.setText(length + COLON + document.getLength());
				}
				
				@Override
				public void changedUpdate(DocumentEvent arg0) {
					lengthLabel.setText(length + COLON + document.getLength());
				}
			});
			
			
			updateSelectionLabel(textArea);
			currentTextArea.addCaretListener(new CaretListener() {
				
				@Override
				public void caretUpdate(CaretEvent r) {
					updateSelectionLabel(textArea);
				}
			});
			currentTextArea.getCaret().setDot(currentTextArea.getCaretPosition());
		}
	}
	
	/**
	 * Stops the {@link Timer}.
	 */
	public void stopTimer() {
		t.stop();
	}
	
	/**
	 * An auxiliary method used to update the selection label.
	 * 
	 * @param textArea
	 *            is the given textArea used to update..
	 */
	private void updateSelectionLabel(JTextArea textArea) {
		int dotPosition = textArea.getCaret().getDot();
		int markPosition = textArea.getCaret().getMark();
		
		String untilCaret = textArea.getText().substring(0, dotPosition);
		int lineNumber = 1;
		for (char c : untilCaret.toCharArray()) {
			if (c == '\n') {
				lineNumber++;
			}
		}
		
		int col = Math.abs(untilCaret.lastIndexOf('\n') - dotPosition);
		int sel = Math.abs(dotPosition - markPosition);
		
		selectionLabel.setText(String.format("%s : %d %s : %d %s : %d", 
				line, lineNumber, 
				column, col,
				selection, sel));
	}
}
