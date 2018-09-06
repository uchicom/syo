// (c) 2015 uchicom
package com.uchicom.syo;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.uchicom.syo.ui.EditorFrame;

public class Context {
	private static Context context = new Context();

	private Map<File, EditorFrame> fileFrameMap = new HashMap<>();
	private List<EditorFrame> notitleList = new ArrayList<>();

	private Context() {

	}

	public static Context getInstance() {
		return context;
	}

	/**
	 * ファイルを指定して起動する.
	 * 
	 * @param file
	 */
	public void start(File file) {
		start(file, null);
	}

	public void start(File file, Rectangle rectangle) {
		SwingUtilities.invokeLater(() -> {
			synchronized (fileFrameMap) {
				EditorFrame frame = null;
				if (fileFrameMap.containsKey(file)) {
					frame = fileFrameMap.get(file);
				} else {
					frame = new EditorFrame(file, rectangle);
					fileFrameMap.put(file, frame);
				}
				if (rectangle != null) {
					frame.setBounds(rectangle);
				}
				frame.setVisible(true);
			}
		});
	}

	public void start() {
		SwingUtilities.invokeLater(() -> {
			EditorFrame frame = new EditorFrame(notitleList.size());
			notitleList.add(frame);
			frame.setVisible(true);
		});
	}
}
