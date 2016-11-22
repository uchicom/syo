/* (c) 2015 uchicom */
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

import com.uchicom.syo.util.UIStore;

/**
 * スクリプトを実行するアクション.
 * @author shigeki
 *
 */
public class ScriptAction extends AbstractAction {

	private File file;
	private UIStore uiStore;
	private String[] params;

	public ScriptAction(UIStore uiStore) {
		this(uiStore,new File(uiStore.getActionResource().getProperty(ScriptAction.class.getCanonicalName() + ".FILE")) , null);
	}

	public ScriptAction(UIStore uiStore, File file, String[] params) {
		this.file = file;
		this.uiStore = uiStore;
		this.params = params;
		String name = file.getName();
		if (name.contains("[i]")) {
			actionPerformed(null);
		}
		putValue(NAME, name.replaceAll("\\[.*\\]|\\..*", ""));
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (file.isDirectory()) return;
		ScriptEngineManager sem = new ScriptEngineManager();

		// JavaScriptのScriptEngineを取得する
		ScriptEngine se = sem.getEngineByName("JavaScript");
		JTextArea textArea = uiStore.getTextArea();
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
			if (params != null) {
				se.put("params", params);
			}
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
