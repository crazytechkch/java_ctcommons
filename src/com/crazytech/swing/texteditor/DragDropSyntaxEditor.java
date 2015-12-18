package com.crazytech.swing.texteditor;

import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.swing.JTextArea;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.crazytech.io.IOUtil;

public class DragDropSyntaxEditor extends SyntaxEditor{

	public DragDropSyntaxEditor(String hint, Locale locale, String defPath) {
		super(hint, locale, defPath);
		getRtextArea().setDropTarget(new MyDropTarget(getRtextArea()));
	}
	
	public DragDropSyntaxEditor(String hint, Locale locale) {
		super(hint, locale);
		getRtextArea().setDropTarget(new MyDropTarget(getRtextArea()));
	}
	
	private class MyDropTarget extends DropTarget {
		private RSyntaxTextArea ta; 
		
		
		public MyDropTarget(RSyntaxTextArea ta) throws HeadlessException {
			super();
			this.ta = ta;
		}

		@Override
		public synchronized void drop(DropTargetDropEvent dtde) {
			try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>) dtde
                        .getTransferable().getTransferData(
                                DataFlavor.javaFileListFlavor);
                for (File file : droppedFiles) {
                    /*
                     * NOTE:
                     *  When I change this to a println,
                     *  it prints the correct path
                     */
                	String content = IOUtil.readFile(file.getAbsolutePath());
                    ta.setText(content);
                    setCurrFilePath(file.getAbsolutePath());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
			super.drop(dtde);
		}
	}

}
