// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import com.uchicom.syo.util.UIStore;

public class CopyAction extends AbstractDoAction {

	public CopyAction(UIStore uiStore) {
		super(uiStore, "copy");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getTextArea().copy();
	}

}
