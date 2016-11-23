// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

public class CopyAction extends AbstractDoAction {

	public CopyAction(UIStore<JTextArea> uiStore) {
		super(uiStore, "copy");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().copy();
	}

}
