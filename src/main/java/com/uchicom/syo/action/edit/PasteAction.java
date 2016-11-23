// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

public class PasteAction extends AbstractDoAction {

	public PasteAction(UIStore<JTextArea> uiStore) {
		super(uiStore, "paste");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().paste();
	}

}
