// (c) 2016 uchicom
package com.uchicom.syo.action.display;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.DialogUtil;
import com.uchicom.ui.util.UIStore;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class DiffAction extends AbstractResourceAction<EditorFrame> {

	/**
	 * @param uiStore
	 */
	public DiffAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		uiStore.getMainComponent().getTextArea().setText("");
		JFileChooser chooser1 = new JFileChooser("比較元を選択してください。");
		chooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser1.showOpenDialog(uiStore.getMainComponent());
		File file1 = chooser1.getSelectedFile();
		if (file1 == null) {
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較元が選択されませんでした");
			return;
		}
		JFileChooser chooser2 = new JFileChooser("比較先を選択してください。");
		chooser2.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser2.showOpenDialog(uiStore.getMainComponent());
		File file2 = chooser2.getSelectedFile();
		if (file2 == null) {
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較先が選択されませんでした");
			return;
		}
		if (file1.equals(file2)) {
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較元と比較先が同じです");
			return;
		}

		Map<String, File> file1Map = new HashMap<>();
		try {
			createFileMap(file1.getCanonicalPath().length(), file1Map, file1);
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較元のファイル検索でエラーが発生しました。\n" + e.getMessage());
			return;
		}
		Map<String, File> file2Map = new HashMap<>();
		try {
			createFileMap(file2.getCanonicalPath().length(), file2Map, file2);
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較先のファイル検索でエラーが発生しました。\n" + e.getMessage());
			return;
		}
		Set<String> file1Set = file1Map.keySet();
		Set<String> file1Set2 = new HashSet<>(file1Set);
		Set<String> file1Set3 = new HashSet<>(file1Set);
		Set<String> file2Set = file2Map.keySet();
		Set<String> file2Set2 = new HashSet<>(file2Set);
		file1Set2.removeAll(file2Set);
		file1Set2.removeIf((a) -> {
			return a.contains("svn") || a.contains("bin") || a.contains("classes");
		});
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("比較元:");
		try {
			strBuff.append(file1.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較元のファイル検索でエラーが発生しました。\n" + e.getMessage());
			return;
		}
		strBuff.append("\n比較先:");
		try {
			strBuff.append(file2.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showMessageDialog(uiStore.getMainComponent(), "比較先のファイル検索でエラーが発生しました。\n" + e.getMessage());
			return;
		}
		strBuff.append("\n\n比較元のみ存在\n");
		file1Set2.forEach((a) -> {
			strBuff.append(a);
			strBuff.append("\n");
		});

		file2Set2.removeAll(file1Set);
		file2Set2.removeIf((a) -> {
			return a.contains("svn") || a.contains("bin") || a.contains("classes");
		});
		strBuff.append("\n比較先のみ存在\n");
		file2Set2.forEach((a) -> {
			strBuff.append(a);
			strBuff.append("\n");
		});

		strBuff.append("\n両方に存在するが差分あり\n");
		file1Set3.retainAll(file2Set);

		file1Set3.removeIf((a) -> {
			return a.contains("svn") || a.contains("bin") || a.contains("classes");
		});
		List<String> equalsList = new ArrayList<>();
		file1Set3.forEach((a) -> {
			File child1 = new File(file1, a);
			File child2 = new File(file2, a);
			try {
				if (!equals(child1, child2)) {
					strBuff.append(a);
					strBuff.append("\n");
				} else {
					equalsList.add(a);
				}
			} catch (IOException e) {
				e.printStackTrace();
				DialogUtil.showMessageDialog(uiStore.getMainComponent(), "ファイル比較でエラーが発生しました。\n" + e.getMessage());
				return;
			}
		});

		strBuff.append("\n両方に存在し差分なし\n");
		equalsList.forEach((a) -> {
			strBuff.append(a);
			strBuff.append("\n");
		});

		strBuff.append("\nsvnやbin,classesを含むものは除外しています\n");
		uiStore.getMainComponent().getTextArea().setText(strBuff.toString());

	}

	/**
	 * ファイルマップを作成する
	 * @param startIndex
	 * @param fileMap
	 * @param file
	 * @throws IOException
	 */
	private void createFileMap(int startIndex, Map<String, File> fileMap, File file) throws IOException {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				createFileMap(startIndex, fileMap, child);
			}
		} else {
			String path = file.getCanonicalPath().substring(startIndex);
			fileMap.put(path, file);
		}
		return;
	}
	/**
	 * ファイルの中身をチェックする
	 * @param file1
	 * @param file2
	 * @return
	 * @throws IOException
	 */
	private boolean equals(File file1, File file2) throws IOException {
		if (file1.length() != file2.length()) {
			return false;
		}
		try (FileInputStream fis1 = new FileInputStream(file1);
			FileInputStream fis2 = new FileInputStream(file2);) {
			byte[] bytes1 = new byte[4 * 1024];
			byte[] bytes2 = new byte[4 * 1024];
			int length1 = 0;
			int length2 = 0;
			while ((length1 = fis1.read(bytes1)) > 0) {
				int index = 0;
				while (index < length1) {
					length2 = fis2.read(bytes2, index, length1 - index);
					index += length2;
				}
				if (!Arrays.equals(Arrays.copyOf(bytes1, index), Arrays.copyOf(bytes2, index))) {
					return false;
				}
			}
		}
		return true;
	}

}
