// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

import com.uchicom.syo.action.AbstractResourceAction;
import com.uchicom.syo.util.UIStore;

public class SaveAction extends AbstractResourceAction {

	private JTextArea textArea;
	public SaveAction(UIStore uiStore) {
	    super(uiStore);
	    textArea = uiStore.getTextArea();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showSaveDialog(textArea);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectFile = chooser.getSelectedFile();
			if (selectFile.exists()) {
				//JOptionPane.showOptionDialog(textArea, "ファイルが存在します.上書きしますか?", "上書き確認", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane., ImageIcon., options, initialValue)
			}

			try (FileOutputStream fos = new FileOutputStream(selectFile);) {
				fos.write(textArea.getText().getBytes());
				fos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

}
