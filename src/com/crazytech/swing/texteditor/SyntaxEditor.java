package com.crazytech.swing.texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.rsta.ac.java.JavaCompletionProvider;
import org.fife.rsta.ac.js.JsDocCompletionProvider;
import org.fife.rsta.ac.php.PhpCompletionProvider;
import org.fife.rsta.ac.xml.XmlCompletionProvider;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.SizeGripIcon;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import org.jdesktop.xswingx.PromptSupport;

import res.locale.LangMan;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.LocaleChangeListener;

public class SyntaxEditor extends JPanel implements LocaleChangeListener{
	protected JTextArea textArea;
	private RSyntaxTextArea rtextArea;
	private JMenu mnFile,mnEdit;
	private JMenuItem mntmNew, mntmOpen, mntmSave, mntmSaveAs,mntmRefresh,
		mntmUndo, mntmRedo, mntmSelectAll, mntmCut, mntmCopy, mntmPaste;
	private LangMan lang;
	private Locale locale;
	private UndoManager undoMan;
	private String currFilePath,defaultPath,hint,loadedContent;
	private JLabel lblStatus;
	private Component parentComponent;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private JFrame frame;
	private JMenuBar menuBar;
	
	private boolean contentLoaded;
	
	/**
	 * Create the panel.
	 * @param hint
	 * @param locale
	 * @param default Path
	 */
	
	public SyntaxEditor(JFrame frame,Component parent, String hint, Locale locale, String defPath) {
		defaultPath = defPath;
		this.frame = frame;
		this.parentComponent = parent;
		this.locale = locale;
		this.hint = hint;
		init();
	}
	
	public SyntaxEditor(JFrame frame, Component parent, String hint, Locale locale){
		defaultPath = new File("").getPath();
		this.frame = frame;
		this.parentComponent = parent;
		this.locale = locale;
		this.hint = hint;
		init();
	}
	
	private void init(){
		setLayout(new GridLayout(1, 0, 0, 0));
		loadedContent = "";
		lang = new LangMan(locale);
		
		
		undoMan = new UndoManager();
		
		rtextArea = new RSyntaxTextArea(15, 30);
		rtextArea.setTabSize(4);
		rtextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		rtextArea.setCodeFoldingEnabled(true);
		
		/*rtextArea.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				checkAndConfirmFileChange();
			}
		});
		*/
		PromptSupport.init(hint, Color.GRAY, null, rtextArea);
		
		setAutocompletion(RstaAC.AC_DEF);
		
		RTextScrollPane rscrollPane = new RTextScrollPane(rtextArea);
		add(rscrollPane);
		//scrollPane.setViewportView(textArea);
		
		Document doc = rtextArea.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoMan.addEdit(e.getEdit());
			}
		});
		
		JPanel panel = new JPanel();
		rscrollPane.setColumnHeaderView(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		initSearchDialogs(frame);
		
		
		lblStatus = new JLabel("");
		panel.add(lblStatus, BorderLayout.EAST);
	}
	
	public ActionListener mnActNew() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rtextArea.setText("");
			}
		};
	}
	
	public ActionListener mnActRefresh(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String fileContent = IOUtil.readFile(currFilePath);
					if (isContentLoaded()&&isFileChanged()&&!fileContent.equals(loadedContent)) {
						switch (optionDialog(parentComponent,lang.getString("content_changed")+"\n"+currFilePath, "Warning")) {
						case 0:
							rtextArea.setText(fileContent);
							break;
						case 1:
							loadedContent = getText();
						default:
							break;
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
	}
	
	public ActionListener mnActOpen(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					openFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	public ActionListener mnActSave(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					if(currFilePath!=null)saveFile();
					else saveAsFile();
				} catch (IOException e) {
					setStatus(lang.getString("savefailed"), null, 5000);
					e.printStackTrace();
				}
			}
		};
	}
	
	public ActionListener mnActSaveAs(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					saveAsFile();
				} catch (IOException e) {
					setStatus(lang.getString("savefailed"), null, 5000);
					e.printStackTrace();
				}
			}
		};
	}
	
	public ActionListener mnActUndo(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (undoMan.canUndo()) undoMan.undo();
			}
		};
	}
	
	public ActionListener mnActRedo(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (undoMan.canRedo()) undoMan.redo();
			}
		};
	}
	
	public ActionListener mnActSelectAll(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rtextArea.selectAll();
			}
		};
	}
	
	public Action mnActCut(){
		return new DefaultEditorKit.CutAction();
	}
	
	public Action mnActCopy(){
		return new DefaultEditorKit.CopyAction();
	}
	
	public Action mnActPaste(){
		return new DefaultEditorKit.PasteAction();
	}
	
	
	private void initSearchDialogs(JFrame frame){
		findDialog = new FindDialog(frame, searchListener());
		replaceDialog = new ReplaceDialog(frame, searchListener());
		
		SearchContext srCtx = findDialog.getSearchContext();
		replaceDialog.setSearchContext(srCtx);
	}
	
	private SearchListener searchListener(){
		return new SearchListener() {
			
			@Override
			public void searchEvent(SearchEvent e) {
				SearchEvent.Type type = e.getType();
				SearchContext ctx = e.getSearchContext();
				SearchResult result = null;
				
				switch (type) {
				case MARK_ALL:
					result = SearchEngine.markAll(rtextArea, ctx);
					break;
				case FIND:
					result = SearchEngine.find(rtextArea, ctx);
					if (result.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(rtextArea);
					break;
				case REPLACE:
					result = SearchEngine.replace(rtextArea, ctx);
					if (result.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(rtextArea);
					break;
				case REPLACE_ALL:
					result = SearchEngine.replaceAll(rtextArea, ctx);
					JOptionPane.showMessageDialog(getRootPane(), result.getCount()+" occurences replaced.");
					break;
				default:
					break;
				}
				
				String text = null;
				if (result.wasFound()) {
					text = "Text found; occurrences marked: " + result.getMarkedCount();
				}
				else if (type==SearchEvent.Type.MARK_ALL) {
					if (result.getMarkedCount()>0) {
						text = "Occurrences marked: " + result.getMarkedCount();
					}
					else {
						text = "";
					}
				}
				else {
					text = "Text not found";
				}
				//statusBar.setLabel(text);

			}
			
			@Override
			public String getSelectedText() {
				// TODO Auto-generated method stub
				return rtextArea.getSelectedText();
			}
		};
	}
	
	public static Map<String,String> getThemeMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Default-Alt", "/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml");
		map.put("Default", "/org/fife/ui/rsyntaxtextarea/themes/default.xml");
		map.put("Dark", "/org/fife/ui/rsyntaxtextarea/themes/dark.xml");
		map.put("Eclipse", "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml");
		map.put("Idea", "/org/fife/ui/rsyntaxtextarea/themes/idea.xml");
		map.put("VS", "/org/fife/ui/rsyntaxtextarea/themes/vs.xml");
		return map;
	}
	
	public void setTheme(RSyntaxTextArea rsta, String themeName) {
		try {
			Theme theme = Theme.load(getClass().getResourceAsStream(getThemeMap().get(themeName)));
			theme.apply(rsta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setAutocompletion(int type) {
		AutoCompletion ac = new AutoCompletion(new DefaultCompletionProvider());
		switch (type) {

		case RstaAC.AC_JAVA: ac = new AutoCompletion(new JavaCompletionProvider());break;
		case RstaAC.AC_XML: ac = new AutoCompletion(new XmlCompletionProvider());break;
		case RstaAC.AC_HTML: ac = new AutoCompletion(new HtmlCompletionProvider());break;
		case RstaAC.AC_JS: ac = new AutoCompletion(new JsDocCompletionProvider());break;
		case RstaAC.AC_PHP: ac = new AutoCompletion(new PhpCompletionProvider());break;
		default:break;
		}
		ac.install(rtextArea);
	}
	
	private boolean isFileChanged() throws IOException {
		String fileContent = IOUtil.readFile(currFilePath);
		if(fileContent==null) return false;
		if(!getText().equals(fileContent)) return true;
		return false;
	}
	
	public String getText(){
		return rtextArea.getText();
	}
	
	public void setText(String content){
		rtextArea.setText(content);
	}
	
	public void setHint(String hint){
		PromptSupport.init(hint, Color.GRAY, null, rtextArea);
	}
	
	private void setStatus(String status, final String defautValue, Integer time) {
		lblStatus.setText(status);
		Timer t = new Timer(time, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lblStatus.setText(defautValue);
            }
        });
        t.setRepeats(false);
        t.start();
	}
	
	public void saveFile() throws IOException{
		switch (optionDialog(parentComponent,lang.getString("overwrite_existing")+"\n"+currFilePath, lang.getString("save"))) {
		case 0:
			IOUtil.overwriteFile(getText(), currFilePath);
			setStatus(lang.getString("saved"), null, 5000);
			setLoadedContent(getText());
			break;

		default:
			break;
		}
	}
	
	public void saveAsFile() throws IOException{
		UIManager.put("FileChooser.saveDialogTitleText",lang.getString("saveas"));
		if (!(new File(defaultPath).exists())) new File(defaultPath).mkdirs();
		JFileChooser chooser = new JFileChooser(defaultPath);
		if(currFilePath!=null)chooser.setCurrentDirectory(new File(currFilePath));
		chooser.setLocale(locale);
		int response = chooser.showSaveDialog(null);
		if(response==chooser.APPROVE_OPTION) {
			IOUtil.overwriteFile(getText(), chooser.getSelectedFile().getCanonicalPath());
			currFilePath = chooser.getSelectedFile().getCanonicalPath();
			setStatus(lang.getString("saved"), null, 5000);
			setLoadedContent(getText());
		}
	}
	
	public void openFile() throws IOException {
		if (!(new File(defaultPath).exists())) new File(defaultPath).mkdirs();
		JFileChooser chooser = new JFileChooser(defaultPath);
		if (currFilePath !=null) chooser.setCurrentDirectory(new File(currFilePath));
		chooser.setLocale(locale);
		int response = chooser.showOpenDialog(null);
		if(response==chooser.APPROVE_OPTION){
			setText(IOUtil.readFile(chooser.getSelectedFile().getCanonicalPath()));
			setLoadedContent(getText());
		}
	}
	
	private int optionDialog(Component component, String msg, String title) {
		return JOptionPane.showConfirmDialog(component, msg, title, JOptionPane.OK_CANCEL_OPTION);
	}

	@Override
	public void onLocaleChange(Locale locale) {
		this.locale = locale;
		lang = new LangMan(locale);
		/*
		mnFile.setText(lang.getString("file"));
		mnEdit.setText(lang.getString("edit"));
		mntmOpen.setText(lang.getString("open"));
		mntmRefresh.setText(lang.getString("refresh"));
		mntmSave.setText(lang.getString("save"));
		mntmSaveAs.setText(lang.getString("saveas"));
		mntmNew.setText(lang.getString("new"));
		mntmUndo.setText(lang.getString("undo"));
		mntmRedo.setText(lang.getString("redo"));
		mntmSelectAll.setText(lang.getString("selectall"));
		mntmCut.setText(lang.getString("cut"));
		mntmCopy.setText(lang.getString("copy"));
		mntmPaste.setText(lang.getString("paste"));
		*/
	}
	
	private void checkAndConfirmFileChange(){
		try {
			String fileContent = IOUtil.readFile(currFilePath);
			if (isContentLoaded()&&isFileChanged()&&!fileContent.equals(loadedContent)) {
				switch (optionDialog(parentComponent,lang.getString("content_changed")+"\n"+currFilePath, "Warning")) {
				case 0:
					rtextArea.setText(fileContent);
					break;
				case 1:
					loadedContent = getText();
				default:
					break;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	 public String getCurrFilePath() {
		return currFilePath;
	}

	public void setCurrFilePath(String currFilePath) {
		this.currFilePath = currFilePath;
	}

	public RSyntaxTextArea getRtextArea() {
		return rtextArea;
	}

	public void setRtextArea(RSyntaxTextArea rtextArea) {
		this.rtextArea = rtextArea;
	}
	
	public void setLoadedContent(String loadedContent){
		this.loadedContent = loadedContent;
		setContentLoaded(true);
	}

	public boolean isContentLoaded() {
		return contentLoaded;
	}

	public void setContentLoaded(Boolean contentLoaded) {
		this.contentLoaded = contentLoaded;
	}
	
	public class RstaAC {
		public static final int AC_DEF = 0;
		public static final int AC_JAVA = 1;
		public static final int AC_XML = 2;
		public static final int AC_HTML = 3;
		public static final int AC_JS = 4;
		public static final int AC_PHP = 5;
	}
	
	public ShowFindDialogAction showFindDialogAction(){
		return new ShowFindDialogAction();
	}
	
	private class ShowFindDialogAction extends AbstractAction {
		
		public ShowFindDialogAction() {
			super(lang.getString("find")+"...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}
	
	public ShowReplaceDialogAction showReplaceDialogAction(){
		return new ShowReplaceDialogAction();
	}


	private class ShowReplaceDialogAction extends AbstractAction {
		
		public ShowReplaceDialogAction() {
			super(lang.getString("replace")+"...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}
	
	public GoToLineAction goToLineAction(){
		return new GoToLineAction(frame);
	}
	
	private class GoToLineAction extends AbstractAction {
		private JFrame frame;
		public GoToLineAction(JFrame frame) {
			super("Go To Line...");
			this.frame = frame;
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(frame);
			dialog.setMaxLineNumberAllowed(rtextArea.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line>0) {
				try {
					rtextArea.setCaretPosition(rtextArea.getLineStartOffset(line-1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(rtextArea);
					ble.printStackTrace();
				}
			}
		}

	}

	
	private static class StatusBar extends JPanel {

		private JLabel label;

		public StatusBar() {
			label = new JLabel("Ready");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
		}

		public void setLabel(String label) {
			this.label.setText(label);
		}

	}
}
