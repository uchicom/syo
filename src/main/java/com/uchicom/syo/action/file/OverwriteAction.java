// (c) 2017 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class OverwriteAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param uiStore
	 */
	public OverwriteAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().overwrite();
	}

}
