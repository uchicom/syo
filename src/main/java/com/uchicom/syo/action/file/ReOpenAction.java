// (c) 2016 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ReOpenAction extends AbstractResourceAction<JTextArea> {

	/**
	 * @param uiStore
	 */
	public ReOpenAction(UIStore<JTextArea> uiStore) {
		super(uiStore);
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

}
