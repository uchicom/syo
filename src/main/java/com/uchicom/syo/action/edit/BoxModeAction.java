// (c) 2018 uchicom
package com.uchicom.syo.action.edit;

import java.awt.event.ActionEvent;

import javax.swing.plaf.TextUI;

import com.uchicom.syo.ui.BoxSelectionTextAreaUI;
import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 矩形選択メニュー.
 * @author hex
 *
 */
public class BoxModeAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextUI textUI;
	
	public BoxModeAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
		putValue(SELECTED_KEY, "false");
	}

	public void actionPerformed(ActionEvent e) {
		if ((Boolean)getValue(SELECTED_KEY)) {
			textUI = uiStore.getMainComponent().getTextArea().getUI();
			uiStore.getMainComponent().getTextArea().setUI(new BoxSelectionTextAreaUI());//矩形選択設定
		} else {
			uiStore.getMainComponent().getTextArea().setUI(textUI);//矩形選択解除
		}
	}
}
