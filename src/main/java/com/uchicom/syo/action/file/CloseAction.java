// (c) 2016 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 閉じる.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class CloseAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().dispose();
	}

}
