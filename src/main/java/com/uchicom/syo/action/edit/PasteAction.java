/* (c) 2015 uchicom */
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.util.UIStore;

public class PasteAction extends AbstractDoAction {

	public PasteAction(UIStore uiStore) {
		super(uiStore, "paste");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getTextArea().paste();
	}

}
