// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import com.uchicom.syo.Context;
import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 新規作成アクション
 * @author shigeki
 *
 */
public class CreateAction extends AbstractResourceAction<EditorFrame> {

	public CreateAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Context.getInstance().start();
	}

}
