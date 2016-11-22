// (c) 2015 uchicom
package com.uchicom.syo.util;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

public interface UIStore {

	public JTextArea getTextArea();
	public Component getMainComponent();

	public UndoManager getUndoManager();

	public Properties getActionResource();
}
