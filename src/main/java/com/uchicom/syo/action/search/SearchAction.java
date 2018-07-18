// (c) 2018 uchicom
package com.uchicom.syo.action.search;

import java.awt.event.ActionEvent;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 検索.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SearchAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SearchAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		uiStore.getMainComponent().search();
	}

}
