// (c) 2015 uchicom
package com.uchicom.syo.action.help;

import java.awt.event.ActionEvent;

import com.uchicom.syo.Constants;
import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.DialogUtil;
import com.uchicom.ui.util.UIStore;

public class VersionAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public VersionAction(UIStore<EditorFrame> uiStore) {
    	super(uiStore);
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		DialogUtil.showMessageDialog(uiStore.getMainComponent(), Constants.VERSION);

	}

}
