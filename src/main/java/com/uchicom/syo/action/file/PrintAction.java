// (c) 2015 uchicom
package com.uchicom.syo.action.file;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import com.uchicom.syo.print.TextPrinter;
import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

public class PrintAction extends AbstractResourceAction<EditorFrame> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PrintAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new TextPrinter(uiStore));

		boolean doPrint = job.printDialog();
//		System.out.println(doPrint);
		if (doPrint) {
		    try {
		        job.print();
		    } catch (PrinterException pe) {
		        // The job did not successfully
		        // complete
		    }
		}
	}

}
