// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.util.UIStore;

public class PasteAction extends AbstractDoAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasteAction(UIStore<EditorFrame> uiStore) {
		super(uiStore, "paste");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().getTextArea().paste();
	}

}
