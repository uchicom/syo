// (c) 2015 uchicom
package com.uchicom.syo.action.help;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

public class HelpAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelpAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Desktop desktop = Desktop.getDesktop();

		try {
			desktop.browse(new URI("http://labs.uchicom.com/help/syo/"));
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
	}

}
