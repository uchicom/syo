// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

public class CutAction extends AbstractDoAction {

	public CutAction(UIStore<JTextArea> uiStore) {
		super(uiStore, "cut");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().cut();
	}

}
