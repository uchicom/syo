// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

import com.uchicom.syo.EditorFrame;
import com.uchicom.syo.util.FileUtil;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * 開く.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class OpenAction extends AbstractResourceAction<EditorFrame> {

	public OpenAction(UIStore<EditorFrame> uiStore) {
	    super(uiStore);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextArea textArea = uiStore.getMainComponent().getTextArea();
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(uiStore.getMainComponent());
		if (result != JFileChooser.CANCEL_OPTION) {
			File file = chooser.getSelectedFile();
			textArea.setText(FileUtil.readFile(file));
			textArea.setCaretPosition(0);
			uiStore.getMainComponent().setTitle(file.getName());
		}
	}

}
