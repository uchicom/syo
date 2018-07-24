// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.util.UIStore;

public class UndoAction extends AbstractDoAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UndoAction(UIStore<EditorFrame> uiStore) {
		super(uiStore, "undo");
	}

	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().undo();
	}
}
