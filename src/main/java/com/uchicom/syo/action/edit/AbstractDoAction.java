// (c) 2015 uchicom
package com.uchicom.syo.action.edit;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;
public abstract class AbstractDoAction extends AbstractResourceAction<JTextArea> {

	UndoManager undoManager;

	public AbstractDoAction(UIStore<JTextArea> uiStore, String key) {
		super(uiStore);
		JTextArea textArea = uiStore.getMainComponent();
		ActionMap actionMap = textArea.getActionMap();
		InputMap inputMap = textArea.getInputMap();
		if (actionMap.get(key) == null) {
			actionMap.put(key, this);
			inputMap.put((KeyStroke) getValue(Action.ACCELERATOR_KEY), key);
		}
		undoManager = uiStore.getUndoManager();
	}

}
