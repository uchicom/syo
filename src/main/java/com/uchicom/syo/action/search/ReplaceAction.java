// (c) 2018 uchicom
package com.uchicom.syo.action.search;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 置換.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ReplaceAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReplaceAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().replace();
	}

}
