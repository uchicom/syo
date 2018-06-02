// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.util.UIStore;

public class RedoAction extends AbstractDoAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RedoAction(UIStore<EditorFrame> uiStore) {
		super(uiStore, "redo");
	}

	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().redo();
	}
}
