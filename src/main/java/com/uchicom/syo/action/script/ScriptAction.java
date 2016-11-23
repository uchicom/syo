// (c) 2015 uchicom
package com.uchicom.syo.action.script;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.uchicom.ui.util.UIStore;

/**
 * スクリプトを実行するアクション.
 * @author shigeki
 *
 */
public class ScriptAction extends AbstractAction {

	private File file;
	private UIStore<JTextArea> uiStore;
	private String param;

	public ScriptAction(UIStore<JTextArea> uiStore, File file, String name, String param) {
		this.file = file;
		this.uiStore = uiStore;
		this.param = param;
		String fileName = file.getName();
		if (fileName.contains("[i]")) {
			actionPerformed(null);
		}
		if (name == null) {
			name = fileName.replaceAll("\\[.*\\]|\\..*", "");
		}
		putValue(NAME, name);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (file.isDirectory()) return;
		ScriptEngineManager sem = new ScriptEngineManager();

		// JavaScriptのScriptEngineを取得する
		ScriptEngine se = sem.getEngineByName("JavaScript");
		JTextArea textArea = uiStore.getMainComponent();
		File propertiesDir = new File("./properties");
		if (!propertiesDir.exists()) {
			propertiesDir.mkdir();
		}
		File propertyFile = new File(propertiesDir, file.getName() + ".properties");
		Properties properties = new Properties();
		//プロパティのロード
		if (propertyFile.exists()) {
			try (FileInputStream fis = new FileInputStream(propertyFile)) {
				if (propertyFile.exists()) {
					properties.load(fis);
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "utf-8")) {
			se.put("name",  getValue(NAME));
			se.put("param", param);
			se.put("properties", properties);
			se.put("selectionStart", textArea.getSelectionStart());
			se.put("selectionEnd", textArea.getSelectionEnd());
			se.put("selectedText", textArea.getSelectedText());
			se.put("text",  textArea.getText());
			se.put("textArea", textArea);
			se.eval(reader);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(textArea, e.toString());
		}
		//プロパティの保存
		if (properties.isEmpty()) {
			if (propertyFile.exists()) {
				propertyFile.delete();
			}
		} else {
			if (!propertyFile.exists()) {
				try {
					propertyFile.createNewFile();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(textArea, e.toString());
				}
			}
			try (FileOutputStream fos = new FileOutputStream(propertyFile)) {
				properties.store(fos, "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
