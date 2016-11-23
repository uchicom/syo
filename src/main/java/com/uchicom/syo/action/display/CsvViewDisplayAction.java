// (c) 2016 uchicom
package com.uchicom.syo.action.display;

import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import com.uchicom.syo.EditorFrame;
import com.uchicom.ui.action.AbstractResourceAction;
import com.uchicom.ui.util.UIStore;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class CsvViewDisplayAction extends AbstractResourceAction<EditorFrame> {

	/**
	 * @param uiStore
	 */
	public CsvViewDisplayAction(UIStore<EditorFrame> uiStore) {
		super(uiStore);
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextArea textArea = uiStore.getMainComponent().getTextArea();
		String input = JOptionPane.showInputDialog(uiStore.getMainComponent(), "列分割する区切り文字を正規表現で指定してください。", "正規表現入力", JOptionPane.INFORMATION_MESSAGE);
		if (input != null && !"".equals(input)) {
			String text = textArea.getText();
			Pattern pattern =  Pattern.compile("$", Pattern.MULTILINE);
			String[] rows = pattern.split(text);
			String[][] cells = new String[rows.length][];
			int maxColumn = 0;
			for (int i = 0; i < rows.length; i++) {
				cells[i] = rows[i].split(input);
				int column = cells[i].length;
				if (maxColumn < column) {
					maxColumn = column;
				}
			}
			for (int i = 0; i < rows.length; i++) {
				if (cells[i].length < maxColumn) {
					String[] string = new String[maxColumn];
					for (int j = 0; j < cells[i].length; j++) {
						string[j] = cells[i][j];
					}
					cells[i] = string;
				}
			}
			String[] columns = new String[maxColumn];
			for (int i = 0; i < maxColumn; i++) {
				char[] chars = new char[2];
				int j = i / 26;
				int k = i % 26;
				if (j > 0) {
					chars[0] = (char)((j-1) + 'A');
					chars[1] = (char)(k + 'A');
					columns[i] = String.valueOf(chars);
				} else {
					columns[i] = String.valueOf((char)(k + 'A'));
				}
			}
			JTable table = new JTable(cells, columns);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JFrame frame = new JFrame("CSV Viewer");
			frame.getContentPane().add(new JScrollPane(table));
			frame.pack();
			frame.setVisible(true);
		}
	}

}
