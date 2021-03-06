// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 開く.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class OpenCharsetAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenCharsetAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().openCharset();
	}

}
