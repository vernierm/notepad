package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.components.StatusBar;
import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LJMenu;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizableAction;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;

/**
 * The {@link JNotepadPP} is a text editor, supporting editing multiple
 * documents at a time. Provides standard options for editing documents, from
 * loading, saving, creating new documents, to editing tools like sorting lines.
 * Supports localization in english, german and croatian.
 * 
 * @author Marin
 *
 */
public class JNotepadPP extends JFrame {
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The application title.
	 */
	private static final String APP_TITLE = "JNotepad++";
	/**
	 * Colon used in text output.
	 */
	private static final String COLON = " : ";
	
	/**
	 * The model used to support multiple documents at a time.
	 */
	private DefaultMultipleDocumentModel documentsModel;
	/**
	 * The current {@link SingleDocumentModel} that user is focused on.
	 */
	private SingleDocumentModel currentModel;

	/**
	 * The localization provider.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);

	/**
	 * The main panel. The document model is placed in it.
	 */
	private JPanel mainPanel;
	/**
	 * The panel representing the {@link JNotepadPP} status bar.
	 */
	private StatusBar statusBar;
	
	/**
	 * The {@link JNotepadPP} constructor. Initializes the GUI.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitAppAction.actionPerformed(null);
			}
		});
		
		setTitle(APP_TITLE);
		setLocation(0, 0);
		setSize(1000, 600);
		
		initGUI();
	}

	/**
	 * From this method the whole GUI is initialized. Creates the document area,
	 * actions, menus, tool bars and the status bar.
	 */
	private void initGUI() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		createDocumentArea();
		createActions();
		createMenus();
		createToolbars();
		createStatusbar();
	}

	/**
	 * Initializes the document area. Adds a {@link MultipleDocumentListener} to the
	 * documents model. This listener is used to update the current model, frame
	 * title and the status bar.
	 */
	private void createDocumentArea() {
		documentsModel = new DefaultMultipleDocumentModel();
		documentsModel.addMultipleDocumentListener(new MultipleDocumentListener() {
			
			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}
			
			@Override
			public void documentAdded(SingleDocumentModel model) {
				currentModel = model;
				updateTitle();
				statusBar.updateDocument(currentModel == null ? null : currentModel.getTextComponent());
			}
			
			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				JNotepadPP.this.currentModel = currentModel;
				updateTitle();
				statusBar.updateDocument(currentModel == null ? null : currentModel.getTextComponent());
			}
		});

		mainPanel.add(documentsModel, SwingConstants.CENTER);
	}
	
	/**
	 * Updates the program title.
	 */
	private void updateTitle() {
		String title = "";
		if (currentModel != null) {
			if (currentModel.getFilePath() != null) {
				title = currentModel.getFilePath().toString();
			} else {
				title = DefaultMultipleDocumentModel.ASTERISK;
			}
			title += " - ";
		}

		setTitle(title + APP_TITLE);
	}

	/**
	 * Sets relevant action values like ACCELERATOR_KEY, DESCRIPTION, ... 
	 * Sets whether the action is enabled.
	 */
	private void createActions() {
		createDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveAsDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
		closeDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
		cutDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		copyDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		pasteDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		statisticsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		exitAppAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F4"));
		
		setEnableToDocumentsDependentActions(false);
		setEnableToSelectionDependentActions(false);
		saveDocumentAction.setEnabled(false);
		documentsModel.addMultipleDocumentListener(new MultipleDocumentListener() {
			
			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}
			
			@Override
			public void documentAdded(SingleDocumentModel model) {
				setEnableToDocumentsDependentActions(true);
				setEnableToSelectionDependentActions(false);
				saveDocumentAction.setEnabled(model.isModified());
				
				attachCaretListener(model);
				attachModifiedListener(model);
			}
			
			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				if (currentModel == null) {
					setEnableToDocumentsDependentActions(false);
				}
				setEnableToSelectionDependentActions(false);
				
				if (previousModel != null) {
					detachCaretListener(previousModel);
					detachModifiedListener(previousModel);
				}
				
				if (currentModel != null) {
					saveDocumentAction.setEnabled(currentModel.isModified());
					attachCaretListener(currentModel);
					attachModifiedListener(currentModel);
				}
			}
		});
	}

	/**
	 * Sets if the actions dependent on the document are enabled. If there is no
	 * document this actions should be disabled.
	 * 
	 * @param b
	 *            if true the actions will be set enabled, otherwise disabled.
	 */
	private void setEnableToDocumentsDependentActions(boolean b) {
		statisticsAction.setEnabled(b);
		saveAsDocumentAction.setEnabled(b);
		closeDocumentAction.setEnabled(b);
		pasteDocumentAction.setEnabled(b);
	}
	
	/**
	 * Sets if the actions dependent on the document are enabled. If there is no
	 * document this actions should be disabled.
	 * 
	 * @param b
	 *            if true the actions will be set enabled, otherwise disabled.
	 */
	private void setEnableToSelectionDependentActions(boolean b) {
		cutDocumentAction.setEnabled(b);
		copyDocumentAction.setEnabled(b);
		toUpperCaseAction.setEnabled(b);
		toLowerCaseAction.setEnabled(b);
		invertCaseAction.setEnabled(b);
		ascendingAction.setEnabled(b);
		descendingAction.setEnabled(b);
		uniqueAction.setEnabled(b);
	}
	
	/**
	 * A listener that tracks whether current model is modified. Used to set if the
	 * save action is enabled.
	 */
	private SingleDocumentListener modifiedListener = new SingleDocumentListener() {
		
		@Override
		public void documentModifyStatusUpdated(SingleDocumentModel model) {
			if(model.isModified()) {
				saveDocumentAction.setEnabled(true);
			} else {
				saveDocumentAction.setEnabled(false);
			}
		}
		
		@Override
		public void documentFilePathUpdated(SingleDocumentModel model) {
		}
	};
	
	/**
	 * Attaches the {@link SingleDocumentListener} to the given model.
	 * 
	 * @param model
	 *            the given model.
	 */
	private void attachModifiedListener(SingleDocumentModel model) {
		model.addSingleDocumentListener(modifiedListener);
	}
	
	/**
	 * Detaches the {@link SingleDocumentListener} from the given model.
	 * 
	 * @param model
	 *            the given model.
	 */
	private void detachModifiedListener(SingleDocumentModel model) {
		model.removeSingleDocumentListener(modifiedListener);
	}
	
	/**
	 * The {@link CaretListener} used to set if actions dependent on selection are
	 * enabled.
	 */
	private CaretListener caretListener = new CaretListener() {
		
		@Override
		public void caretUpdate(CaretEvent e) {
			Caret caret = currentModel.getTextComponent().getCaret();
			if (caret.getDot() != caret.getMark()) {
				setEnableToSelectionDependentActions(true);
			} else {
				setEnableToSelectionDependentActions(false);
			}
		}
	};
	
	/**
	 * Attaches the {@link CaretListener} to the given model.
	 * 
	 * @param model
	 *            is the given model.
	 */
	private void attachCaretListener(SingleDocumentModel model) {
		model.getTextComponent().addCaretListener(caretListener);
	}
	
	/**
	 * Detaches the {@link CaretListener} from the given model.
	 * 
	 * @param model
	 *            is the given model.
	 */
	private void detachCaretListener(SingleDocumentModel model) {
		model.getTextComponent().removeCaretListener(caretListener);
	}

	/**
	 * Creates the menus and sets the menu bar.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new LJMenu("file", flp);
		menuBar.add(fileMenu);
		
		fileMenu.add(new JMenuItem(createDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.add(new JMenuItem(exitAppAction));
		
		JMenu editMenu = new LJMenu("edit", flp);
		menuBar.add(editMenu);
		editMenu.add(new JMenuItem(cutDocumentAction));
		editMenu.add(new JMenuItem(copyDocumentAction));
		editMenu.add(new JMenuItem(pasteDocumentAction));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(statisticsAction));
		
		JMenu languagesMenu = new LJMenu("languages", flp);
		menuBar.add(languagesMenu);
		languagesMenu.add(new JMenuItem(setEnglishAction));
		languagesMenu.add(new JMenuItem(setCroatianAction));
		languagesMenu.add(new JMenuItem(setGermanAction));
		
		JMenu toolsMenu = new LJMenu("tools", flp);
		menuBar.add(toolsMenu);
		JMenu changeCaseMenu = new LJMenu("change-case", flp);
		toolsMenu.add(changeCaseMenu);
		changeCaseMenu.add(new JMenuItem(toUpperCaseAction));
		changeCaseMenu.add(new JMenuItem(toLowerCaseAction));
		changeCaseMenu.add(new JMenuItem(invertCaseAction));
		JMenu sortMenu = new LJMenu("sort", flp);
		toolsMenu.add(sortMenu);
		sortMenu.add(new JMenuItem(ascendingAction));
		sortMenu.add(new JMenuItem(descendingAction));
		toolsMenu.addSeparator();
		toolsMenu.add(new JMenuItem(uniqueAction));
		
		this.setJMenuBar(menuBar);		
	}

	/**
	 * Creates a floatable tool bar and adds it to the document area panel.
	 */
	private void createToolbars() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(true);
		
		toolBar.add(new JButton(createDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(saveAsDocumentAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.add(new JButton(exitAppAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(cutDocumentAction));
		toolBar.add(new JButton(copyDocumentAction));
		toolBar.add(new JButton(pasteDocumentAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(statisticsAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(setEnglishAction));
		toolBar.add(new JButton(setCroatianAction));
		toolBar.add(new JButton(setGermanAction));

		mainPanel.add(toolBar, BorderLayout.PAGE_START);
	}
	
	/**
	 * Creates the {@link StatusBar} and adds it to the frame end.
	 */
	private void createStatusbar() {
		statusBar = new StatusBar(flp);
		getContentPane().add(statusBar, BorderLayout.PAGE_END);
	}

	/**
	 * From this method the program starts.
	 * 
	 * @param args
	 *            are the command line arguments, not used here.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JNotepadPP();
			frame.setVisible(true);
		});
	}

	/**
	 * This action creates a new document.
	 */
	private final Action createDocumentAction = new LocalizableAction("create-new", "create-new-mn", "create-new-desc",
			flp) {

		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			documentsModel.createNewDocument();
		}
	};

	/**
	 * This action gives the option to choose a file and load it as document.
	 */
	private final Action openDocumentAction = new LocalizableAction("open", "open-mn", "open-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			String fileChooserTitle = flp.getString("open");
			String errorMessage = flp.getString("open-error-message");
			String error = flp.getString("error");
			
			Path filepath = chooseFilepath(fileChooserTitle);
			if (filepath == null)
				return;
			
			if(!Files.isReadable(filepath)) {
				JOptionPane.showMessageDialog(
						JNotepadPP.this,
						errorMessage + filepath.toAbsolutePath().toString(),
						error,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			documentsModel.loadDocument(filepath);
		}
	};
	
	/**
	 * This action saves the document. If the document doesn't have a file
	 * specified, the actions acts like 'save as' action.
	 */
	private final Action saveDocumentAction = new LocalizableAction("save", "save-mn", "save-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			SingleDocumentModel model = documentsModel.getCurrentDocument();
			save(model);
		}
	};
	
	/**
	 * Saves the file with a option to choose the new file name and location.
	 */
	private final Action saveAsDocumentAction = new LocalizableAction("save-as", "save-as-mn", "save-as-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs(documentsModel.getCurrentDocument());
		}
	};

	/**
	 * This action closes the current document and checks if is modified. If
	 * document is modified ask the user to save it before closing.
	 */
	private final Action closeDocumentAction = new LocalizableAction("close", "close-mn", "close-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			SingleDocumentModel model = documentsModel.getCurrentDocument();
			if(!model.isModified() || canBeClosed(model)) {
				documentsModel.closeDocument(model);
			}
		}
	};
	
	/**
	 * This method cuts the selected text from the current model.
	 */
	private Action cutDocumentAction = new LocalizableAction("cut", "cut-mn", "cut-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			documentsModel.getCurrentDocument().getTextComponent().cut();
		}
	};
	
	/**
	 * This method copies the selected text from the current document.
	 */
	private Action copyDocumentAction =  new LocalizableAction("copy", "copy-mn", "copy-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			documentsModel.getCurrentDocument().getTextComponent().copy();
		}
	};
	
	/**
	 * This model pastes to the current document the text in the clipboard.
	 */
	private Action pasteDocumentAction =  new LocalizableAction("paste", "paste-mn", "paste-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			documentsModel.getCurrentDocument().getTextComponent().paste();
		}
	};
	
	/**
	 * This actions outputs the statistics of the current document.
	 */
	private Action statisticsAction = new LocalizableAction("statistics", "stat-mn", "stat-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			SingleDocumentModel model = documentsModel.getCurrentDocument();
			String content = model.getTextComponent().getText();
			
			int numberOfCharacters = content.length();
			int numberOfNonBlank = numberOfCharacters;
			int numberOfLines = 1;
			
			for (char c : content.toCharArray()) {
				if (Character.isWhitespace(c)) {
					numberOfNonBlank--;
					if (c == '\n') {
						numberOfLines++;
					}
				}
			}
			
			String totalCharacters = flp.getString("total-characters");
			String nonBlank = flp.getString("non-blank");
			String totalLines = flp.getString("total-lines");
			String statistics = flp.getString("statistics");
			
			StringJoiner stats = new StringJoiner(System.lineSeparator());
			stats.add(totalCharacters + COLON + numberOfCharacters)
				.add(nonBlank + COLON + numberOfNonBlank)
				.add(totalLines + COLON + numberOfLines);
			
			JOptionPane.showMessageDialog(
					JNotepadPP.this,
					stats.toString(),
					statistics,
					JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * This action is used to exit the program. Before exiting checks if there are
	 * any unsaved documents.
	 */
	private Action exitAppAction = new LocalizableAction("exit", "exit-mn", "exit-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			while (documentsModel.getNumberOfDocuments() > 0) {
				SingleDocumentModel model = documentsModel.getCurrentDocument();
				
				if(model.isModified()) {
					if (!canBeClosed(model)) {
						return;
					}
				}
				documentsModel.closeDocument(model);
			}
			dispose();
			statusBar.stopTimer();
		}
	};

	/**
	 * This action performs the to upper case modification on the selected text.
	 */
	private Action toUpperCaseAction = new LocalizableAction("to-upper", "to-upper-mn", "to-upper-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performCaseOperation(s -> s.toUpperCase());
		}
	};

	/**
	 * This action performs the to lower case modification to the selected text.
	 */
	private Action toLowerCaseAction = new LocalizableAction("to-lower", "to-lower-mn", "to-lower-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performCaseOperation(s -> s.toLowerCase());
		}
	};

	/**
	 * This action performs the to invert case modification to the selected text.
	 */
	private Action invertCaseAction = new LocalizableAction("invert-case", "invert-case-mn", "invert-case-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performCaseOperation(s -> {
				char[] array = s.toCharArray();
				for (int i = 0; i < array.length; ++i) {
					char c = array[i];
					if (Character.isLowerCase(c)) {
						array[i] = Character.toUpperCase(c);
					} else {
						array[i] = Character.toLowerCase(c);
					}
				}
				return new String(array);
			});
		}
	};
	
	/**
	 * Performs given function on the selected range of the text.
	 * 
	 * @param function
	 *            is the given function.
	 */
	private void performCaseOperation(Function<String, String> function) {
		Document document = currentModel.getTextComponent().getDocument();
		Caret caret = currentModel.getTextComponent().getCaret();
		
		int length = Math.abs(caret.getDot() - caret.getMark());
		int offset = Math.min(caret.getDot(), caret.getMark());
	
		try {
			String selected = document.getText(offset, length);
			selected = function.apply(selected);
			document.remove(offset, length);
			document.insertString(offset, selected, null);
		} catch (BadLocationException ev) {
			ev.printStackTrace();
		}
	}
	
	/**
	 * Sorts the selected range of lines(if the selection starts in the middle of
	 * the line, the whole line is affected). The sort is ascending.
	 */
	private Action ascendingAction = new LocalizableAction("ascending", "ascending-mn", "ascending-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performLineModification(lines -> {
				Locale locale = new Locale(flp.getString("locale"));
				Collator collator = Collator.getInstance(locale);
				Collections.sort(lines, (f, s) -> collator.compare(f, s));
				return lines;
			});
		}
	};

	/**
	 * Sorts the selected range of lines(if the selection starts in the middle of
	 * the line, the whole line is affected). The sort is descending.
	 */
	private Action descendingAction = new LocalizableAction("descending", "descending-mn", "descending-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performLineModification(lines -> {
				Locale locale = new Locale(flp.getString("locale"));
				Collator collator = Collator.getInstance(locale);
				Collections.sort(lines, (f, s) -> collator.compare(s, f));
				return lines;
			});
		}
	};
	
	/**
	 * From the selected range of lines(if the selection starts in the middle of the
	 * line, the whole line is affected) removes the redundant lines.
	 */
	private Action uniqueAction = new LocalizableAction("unique", "unique-mn", "unique-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			performLineModification(lines -> {
				for(int i = 0; i < lines.size() - 1; ++i) {
					for(int j = i + 1; j < lines.size();) {
						if(lines.get(i).equals(lines.get(j))) {
							lines.remove(j);
						} else {
							++j;
						}
					}
				}
				return lines;
			});
		}
	};
	
	/**
	 * Applies the given function on the selected range of lines.
	 * 
	 * @param function
	 *            is the function to apply.
	 */
	private void performLineModification(Function<List<String>, List<String>> function) {
		try {
			JTextArea textArea = currentModel.getTextComponent();
			Document document = textArea.getDocument();
			Caret caret = textArea.getCaret();

			int startSelection = Math.min(caret.getDot(), caret.getMark());
			int startLine = textArea.getLineOfOffset(startSelection);
			int endSelection = Math.max(caret.getDot(), caret.getMark());
			int endLine = textArea.getLineOfOffset(endSelection);
			startSelection = textArea.getLineStartOffset(startLine);
			endSelection = textArea.getLineEndOffset(endLine);

			List<String> lines = new ArrayList<>();
			int lineNumber = startLine;
			while (lineNumber <= endLine) {
				int currentLineStart = textArea.getLineStartOffset(lineNumber);
				int currentLineEnd = textArea.getLineEndOffset(lineNumber);
				
				String line = textArea.getText(currentLineStart, currentLineEnd - currentLineStart);
				lines.add(line);

				lineNumber++;
			}
			
			String last = lines.get(lines.size() - 1);
			boolean isTheLastLine = !last.endsWith("\n");
			if (isTheLastLine) {
				lines.remove(lines.size() - 1);
				lines.add(last + '\n');
			}

			lines = function.apply(lines);
			
			StringBuilder builder = new StringBuilder();
			for (String line : lines) {
				builder.append(line);
			}
			if (isTheLastLine) {
				builder.deleteCharAt(builder.length() - 1);
			}
			
			document.remove(startSelection, endSelection - startSelection);
			document.insertString(startSelection, builder.toString(), null);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Checks if the given document can be closed, i.e. if is modified, the program
	 * asks the user if he wants to save or discard the changes.
	 * 
	 * @param model
	 *            is the given model to close
	 * @return true if closing can be performed, false otherwise.
	 */
	private boolean canBeClosed(SingleDocumentModel model) {
		Object[] options = { 
				"Save", 
				"Don't save", 
				"Cancel" };

		String question = flp.getString("close-message");
		String title = flp.getString("close-title");
		int option = JOptionPane.showOptionDialog(
				JNotepadPP.this, 
				question,
				title, 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE, 
				null,
				options, 
				options[2]);

		if (option == JOptionPane.YES_OPTION) {
			return save(model);
		} else if (option == JOptionPane.CANCEL_OPTION || option == -1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sets the current language to english.
	 */
	private final Action setEnglishAction = new LocalizableAction("english", "eng-mn", "eng-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("en");
		}
	};
	
	/**
	 * Sets the current language to croatian.
	 */
	private final Action setCroatianAction = new LocalizableAction("croatian", "cro-mn", "cro-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("hr");
		}
	};

	/**
	 * Sets the current language to german.
	 */
	private final Action setGermanAction = new LocalizableAction("german", "ger-mn", "ger-desc", flp) {
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage("de");
		}
	};
	
	/**
	 * Performs the save operation.
	 * 
	 * @param model is the given {@link SingleDocumentModel}.
	 * @return true if saving succeeded, false otherwise.
	 */
	private boolean save(SingleDocumentModel model) {
		Path filepath = model.getFilePath();
		if (filepath != null) {
			documentsModel.saveDocument(model, filepath);
			return true;
		} else {
			return saveAs(model);
		}
	}
	
	/**
	 * Performs the save as operation. For given model asks the user under which
	 * path he wants to save the document and saves it. If the user decides to abort
	 * saving method returns false, if saving is performed returns true.
	 * 
	 * @param model
	 *            is the given model.
	 * @return true if saving succeeded, false otherwise.
	 */
	private boolean saveAs(SingleDocumentModel model) {
		String fileChooserTitle = flp.getString("save-as");
		
		Path filepath = chooseFilepath(fileChooserTitle);
		if (filepath != null && !alreadyOpened(filepath)) {
			if(Files.exists(filepath) && !filepath.equals(model.getFilePath())) {
				Object[] options = {
						"Yes",
						"No"
				};
				
				String confirmTitle = flp.getString("confirm-save-as");
				String overwrite = flp.getString("overwrite");
				int option = JOptionPane.showOptionDialog(
						JNotepadPP.this, 
						overwrite + "? " + filepath.toAbsolutePath().toString(), 
						confirmTitle, 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.WARNING_MESSAGE, 
						null, 
						options, 
						options[1]);
				
				if (option != JOptionPane.YES_OPTION)
					return false;
			}
			
			documentsModel.saveDocument(model, filepath);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the specified path is already opened within the
	 * {@link MultipleDocumentModel}.
	 * 
	 * @param filepath
	 *            is the given {@link Path}.
	 * @return true if such path already exists, false otherwise.
	 */
	private boolean alreadyOpened(Path filepath) {
		for(SingleDocumentModel m : documentsModel) {
			if(filepath.equals(m.getFilePath())) {
				JOptionPane.showMessageDialog(
						JNotepadPP.this,
						flp.getString("already-exists") + ": " + filepath.toAbsolutePath().toString(),
						flp.getString("error"),
						JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
		return false;
	}

	/**
	 * Asks the user to choose a path.
	 * 
	 * @param title
	 *            is the title to be set to the {@link JFileChooser}.
	 * @return the chosen path or null if user aborts the choosing.
	 */
	private Path chooseFilepath(String title) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		if (fileChooser.showOpenDialog(JNotepadPP.this)!=JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		return fileChooser.getSelectedFile().toPath();
	}
}