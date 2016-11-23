// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.util.UIStore;

public class RedoAction extends AbstractDoAction {

	public RedoAction(UIStore<EditorFrame> uiStore) {
		super(uiStore, "redo");
	}

	public void actionPerformed(ActionEvent e) {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}
}
