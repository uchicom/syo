// (c) 2015 uchicom
package com.uchicom.syo.action.help;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.uchicom.syo.Constants;
import com.uchicom.syo.action.AbstractResourceAction;
import com.uchicom.syo.util.UIStore;

public class VersionAction extends AbstractResourceAction {
    public VersionAction(UIStore uiStore) {
    	super(uiStore);
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(uiStore.getMainComponent(), Constants.VERSION);

	}

}
