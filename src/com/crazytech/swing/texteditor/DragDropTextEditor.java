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

import com.crazytech.io.IOUtil;

public class DragDropTextEditor extends TextEditor{

	public DragDropTextEditor(String hint, Locale locale, String defPath) {
		super(hint, locale, defPath);
		textArea.setDropTarget(new MyDropTarget(textArea));
	}
	
	public DragDropTextEditor(String hint, Locale locale) {
		super(hint, locale);
		textArea.setDropTarget(new MyDropTarget(textArea));
	}
	
	private class MyDropTarget extends DropTarget {
		private JTextArea ta; 
		
		
		public MyDropTarget(JTextArea ta) throws HeadlessException {
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
