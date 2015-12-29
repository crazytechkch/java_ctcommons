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

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
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
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.xswingx.PromptSupport;

import res.locale.LangMan;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.LocaleChangeListener;

public class SyntaxEditor extends JPanel implements LocaleChangeListener{
	protected JTextArea textArea;
	private RSyntaxTextArea rtextArea;
	private JMenu mnFile,mnEdit;
	private JMenuItem mntmNew, mntmOpen, mntmSave, mntmSaveAs,
		mntmUndo, mntmRedo, mntmSelectAll, mntmCut, mntmCopy, mntmPaste;
	private LangMan lang;
	private Locale locale;
	private UndoManager undoMan;
	private String currFilePath,defaultPath,hint,loadedContent;
	private JLabel lblStatus;
	private Component parentComponent;
	
	private boolean contentLoaded;
	
	/**
	 * Create the panel.
	 * @param hint
	 * @param locale
	 * @param default Path
	 */
	
	public SyntaxEditor(Component parent, String hint, Locale locale, String defPath) {
		defaultPath = defPath;
		this.parentComponent = parent;
		this.locale = locale;
		this.hint = hint;
		init();
	}
	
	public SyntaxEditor(Component parent, String hint, Locale locale){
		defaultPath = new File("").getPath();
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
		rtextArea.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
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
		});
		PromptSupport.init(hint, Color.GRAY, null, rtextArea);
		
		AutoCompletion autocomplete = new AutoCompletion(rtextAreaComplProvider());
		autocomplete.install(rtextArea);
		
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
		
		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar);
		
		mnFile = new JMenu(lang.getString("file"));
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);
		
		mntmNew = new JMenuItem(lang.getString("new"));
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rtextArea.setText("");
			}
		});
		mnFile.add(mntmNew);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		mntmOpen = new JMenuItem(lang.getString("open"));
		mntmOpen.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/folder_open_icon&16.png")));
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					openFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmOpen);
		
		mntmSave = new JMenuItem(lang.getString("save"));
		mntmSave.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/save_icon&16.png")));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					if(currFilePath!=null)saveFile();
					else saveAsFile();
				} catch (IOException e) {
					setStatus(lang.getString("savefailed"), null, 5000);
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem(lang.getString("saveas"));
		mntmSaveAs.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/save_icon&16.png")));
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					saveAsFile();
				} catch (IOException e) {
					setStatus(lang.getString("savefailed"), null, 5000);
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmSaveAs);
		
		
		mnEdit = new JMenu(lang.getString("edit"));
		mnEdit.setMnemonic('E');
		mnEdit.setIcon(null);
		menuBar.add(mnEdit);
		
		mntmUndo = new JMenuItem(lang.getString("undo"));
		mntmUndo.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/undo_icon&16.png")));
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mntmUndo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoMan.canUndo()) undoMan.undo();
			}
		});
		mnEdit.add(mntmUndo);
		
		mntmRedo = new JMenuItem(lang.getString("redo"));
		mntmRedo.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/redo_icon&16.png")));
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		mntmRedo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoMan.canRedo()) undoMan.redo();
			}
		});
		mnEdit.add(mntmRedo);
		
		JSeparator separator_1 = new JSeparator();
		mnEdit.add(separator_1);
		
		mntmSelectAll = new JMenuItem(lang.getString("selectall"));
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmSelectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rtextArea.selectAll();
			}
		});
		mnEdit.add(mntmSelectAll);
		
		JSeparator separator_2 = new JSeparator();
		mnEdit.add(separator_2);
		
		mntmCut = new JMenuItem(new DefaultEditorKit.CutAction());
		mntmCut.setText(lang.getString("cut"));
		mntmCut.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/clipboard_cut_icon&16.png")));
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mnEdit.add(mntmCut);
		
		mntmCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
		mntmCopy.setText(lang.getString("copy"));
		mntmCopy.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/clipboard_copy_icon&16.png")));
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mnEdit.add(mntmCopy);
		
		mntmPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
		mntmPaste.setText(lang.getString("paste"));
		mntmPaste.setIcon(new ImageIcon(SyntaxEditor.class.getResource("/res/toolbaricons/black/png/clipboard_past_icon&16.png")));
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mnEdit.add(mntmPaste);
		
		lblStatus = new JLabel("");
		panel.add(lblStatus, BorderLayout.EAST);
		
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
	
	private CompletionProvider rtextAreaComplProvider() {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		return provider;
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
	
	private void saveFile() throws IOException{
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
	
	private void saveAsFile() throws IOException{
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
	
	private void openFile() throws IOException {
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
		mnFile.setText(lang.getString("file"));
		mnEdit.setText(lang.getString("edit"));
		mntmOpen.setText(lang.getString("open"));
		mntmSave.setText(lang.getString("save"));
		mntmSaveAs.setText(lang.getString("saveas"));
		mntmNew.setText(lang.getString("new"));
		mntmUndo.setText(lang.getString("undo"));
		mntmRedo.setText(lang.getString("redo"));
		mntmSelectAll.setText(lang.getString("selectall"));
		mntmCut.setText(lang.getString("cut"));
		mntmCopy.setText(lang.getString("copy"));
		mntmPaste.setText(lang.getString("paste"));
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
}
