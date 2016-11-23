// (c) 2015 uchicom
package com.uchicom.syo;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

public class TextPrinter implements Printable {

	JTextArea textArea;
	public TextPrinter(UIStore<EditorFrame> uiStore) {
		textArea = uiStore.getMainComponent().getTextArea();
	}
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		System.out.println(pageIndex);
		if (pageIndex == 0) {
			textArea.print(graphics);

			return PAGE_EXISTS;
		} else {
			return NO_SUCH_PAGE;
		}
	}

}
