// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.syo.Context;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 新規作成アクション
 * @author shigeki
 *
 */
public class CreateAction extends AbstractResourceAction<JTextArea> {

	public CreateAction(UIStore<JTextArea> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Context.getInstance().start();
	}

}
