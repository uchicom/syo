/* (c) 2015 uchicom */
package com.uchicom.syo;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

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
	 * @param file
	 */
	public void start(File file) {
		synchronized (fileFrameMap) {
			if (fileFrameMap.containsKey(file)) {
				SwingUtilities.invokeLater(() -> {
					EditorFrame frame = fileFrameMap.get(file);
					frame.setVisible(true);
				});
			} else {
				SwingUtilities.invokeLater(() -> {
					EditorFrame frame = new EditorFrame(file);
					fileFrameMap.put(file, frame);
					frame.setVisible(true);
				});
			}
		}
	}
	public void start(File file, Rectangle rectangle) {
		synchronized (fileFrameMap) {
			if (fileFrameMap.containsKey(file)) {
				SwingUtilities.invokeLater(() -> {
					EditorFrame frame = fileFrameMap.get(file);
					frame.setBounds(rectangle);
					frame.setVisible(true);
				});
			} else {
				SwingUtilities.invokeLater(() -> {
					EditorFrame frame = new EditorFrame(file, rectangle);
					fileFrameMap.put(file, frame);
					frame.setVisible(true);
				});
			}
		}
	}
	public void start() {
		SwingUtilities.invokeLater(() -> {
			EditorFrame frame = new EditorFrame(notitleList.size());
			notitleList.add(frame);
			frame.setVisible(true);
		});
	}
}
