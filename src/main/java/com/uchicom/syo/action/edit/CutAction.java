// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.util.UIStore;

public class CutAction extends AbstractDoAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CutAction(UIStore<EditorFrame> uiStore) {
		super(uiStore, "cut");
		uiStore.getMainComponent().getNotifyList().add(this);
		enabled = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().getTextArea().cut();
	}

}
