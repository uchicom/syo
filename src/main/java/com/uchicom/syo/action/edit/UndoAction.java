// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

public class UndoAction extends AbstractDoAction {

	public UndoAction(UIStore<JTextArea> uiStore) {
		super(uiStore, "undo");
	}

	public void actionPerformed(ActionEvent e) {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}
}
