package com.crazytech.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.crazytech.swing.browser.SimpleSwingBrowser;

public class BetterTabbedPane extends JPanel {
	JTabbedPane tabbedPane;
    Integer numTabs = 0;
    JPanel component;
 
    public BetterTabbedPane(JPanel firstComponent) {
        this.component = firstComponent;
    	createGUI(firstComponent);
        setDisplay();
    }
 
    /** set diplay for JFrame */
    private void setDisplay() {
        setSize(450, 300);
        setVisible(true);
    }
 
    /** set title and add JTabbedPane into JFrame */
    private void createGUI(JPanel firstComponent) {
        createJTabbedPane(firstComponent);
        add(tabbedPane);
    }
 
    /** create JTabbedPane contain 2 tab */
    private void createJTabbedPane(Component component) {
        /* create JTabbedPane */
        tabbedPane = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.SCROLL_TAB_LAYOUT);
 
        /* add first tab */
        tabbedPane.add(component, "Tab " + String.valueOf(numTabs),
                numTabs++);
        tabbedPane.setTabComponentAt(0, new BetterTab(this));
 
        /* add tab to add new tab when click */
        tabbedPane.add(new JPanel(), "+", numTabs++);
 
        tabbedPane.addChangeListener(changeListener);
    }
 
    /** create JPanel contain a JLabel */
    private static JPanel createJPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JScrollPane(createTextArea(10, 40)));
        return panel;
    }
    
    public ChangeListener changeListener = new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			addNewTab(createJPanel());
		}
	};
 
    private static JTextArea createTextArea(int row, int col) {
        JTextArea ta = new JTextArea(row, col);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setForeground(Color.BLUE);
        return ta;
    }
 
    public void addNewTab(Component panel) {
        int index = numTabs - 1;
        if (tabbedPane.getSelectedIndex() == index) { /* if click new tab */
            /* add new tab */
            tabbedPane.add(panel, "Tab " + String.valueOf(index),
                    index);
            /* set tab is custom tab */
            tabbedPane.setTabComponentAt(index, new BetterTab(this));
            tabbedPane.removeChangeListener(changeListener);
            tabbedPane.setSelectedIndex(index);
            tabbedPane.addChangeListener(changeListener);
            numTabs++;
        }
    }
 
    public void removeTab(int index) {
        tabbedPane.remove(index);
        numTabs--;
 
        if (index == numTabs - 1 && index > 0) {
            tabbedPane.setSelectedIndex(numTabs - 2);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
 
        if (numTabs == 1) {
            addNewTab(component);
        }
    }
 
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setLocationRelativeTo(null);
        frame.add(new BetterTabbedPane(createJPanel()));
        frame.setVisible(true);
    }
}
