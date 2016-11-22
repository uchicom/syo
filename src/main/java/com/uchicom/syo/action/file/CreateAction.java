/* (c) 2015 uchicom */
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import com.uchicom.syo.Context;
import com.uchicom.syo.action.AbstractResourceAction;
import com.uchicom.syo.util.UIStore;

/**
 * 新規作成アクション
 * @author shigeki
 *
 */
public class CreateAction extends AbstractResourceAction {

	public CreateAction(UIStore uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Context.getInstance().start();
	}

}
