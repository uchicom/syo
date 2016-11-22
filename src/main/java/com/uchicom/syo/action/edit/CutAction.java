// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.util.UIStore;

public class CutAction extends AbstractDoAction {

	public CutAction(UIStore uiStore) {
		super(uiStore, "cut");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getTextArea().cut();
	}

}
