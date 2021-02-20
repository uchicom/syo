// (c) 2015 uchicom
package com.uchicom.syo.action.script;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import com.uchicom.syo.ui.EditorFrame;
import com.uchicom.ui.util.DialogUtil;
import com.uchicom.ui.util.UIStore;

import jdk.jshell.JShell;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;

/**
 * スクリプトを実行するアクション.
 * 
 * @author shigeki
 *
 */
public class ScriptAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File file;
	private UIStore<EditorFrame> uiStore;
	private String param;

	public ScriptAction(UIStore<EditorFrame> uiStore, File file, String name, String param) {
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
	private String base64;
	private int selectionStart;
	private int selectionEnd;

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (file.isDirectory())
			return;
		JTextArea textArea = uiStore.getMainComponent().getTextArea();
		File propertiesDir = new File("./properties");
		if (!propertiesDir.exists()) {
			propertiesDir.mkdir();
		}
		File propertyFile = new File(propertiesDir, file.getName() + ".properties");
		Properties properties = new Properties();
		// プロパティのロード
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
		try (FileInputStream fis = new FileInputStream(file); JShell js = JShell.create()) {

			StringBuilder script = new StringBuilder(1024);
			script.append("import java.util.*;");
			script.append("import java.nio.charset.StandardCharsets;");
			script.append("int selectionStart = ").append(textArea.getSelectionStart()).append(";");
			script.append("int selectionEnd = ").append(textArea.getSelectionEnd()).append(";");
			script.append("String base64 = null;");
			script.append("String selectedText = null;");
			script.append("String text = null;");
			if (textArea.getSelectedText() != null) {
				script.append("base64 = \"")
						.append(Base64.getEncoder().encodeToString(textArea.getSelectedText().getBytes()))
						.append("\";");
				script.append(
						"selectedText = new String(Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);");
			}
			if (textArea.getText().isEmpty()) {
				script.append("text = \"\";");
			} else {
				script.append("base64 = \"").append(Base64.getEncoder().encodeToString(textArea.getText().getBytes()))
						.append("\";");
				script.append(
						"text = new String(Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);");
			}
			script.append("base64 = null;");
			script.append(new String(fis.readAllBytes(), StandardCharsets.UTF_8));
			script.append(";if (text.length() > 0) {");
			script.append("base64 = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));");
			script.append("}");

			int offset = 0;
			for (int i = 0; i < script.length(); i++) {
				if (script.charAt(i) == ';' || script.charAt(i) == '}') {
					String input = script.substring(offset, i + 1);
					var ci = js.sourceCodeAnalysis().analyzeCompletion(input);
					if (ci.completeness().isComplete()) {
						offset = i + 1;
						List<SnippetEvent> events = js.eval(input);
						for (SnippetEvent e : events) {
							StringBuilder sb = new StringBuilder();
							if (e.causeSnippet() == null) {
								// We have a snippet creation event
								switch (e.status()) {
								case VALID:
									sb.append("Successful ");
									break;
								case RECOVERABLE_DEFINED:
									sb.append("With unresolved references ");
									break;
								case RECOVERABLE_NOT_DEFINED:
									sb.append("Possibly reparable, failed  ");
									break;
								case REJECTED:
									sb.append("Failed ");
									break;
								default:
									sb.append(e.status());
									sb.append(" ");
									break;
								}
								if (e.previousStatus() == Status.NONEXISTENT) {
									sb.append("addition");
								} else {
									sb.append("modification");
								}
								sb.append(" of ");
								sb.append(e.snippet());
								System.out.println(sb.toString());
								if (e.exception() != null) {
									throw e.exception();
								}
							}
						}
					}
				}
			}

			js.variables().forEach(key -> {
				switch (key.name()) {
				case "base64":
					base64 = js.varValue(key);
					break;
				case "selectionStart":
					selectionStart = Integer.parseInt(js.varValue(key));
					break;
				case "selectionEnd":
					selectionEnd = Integer.parseInt(js.varValue(key));
					break;
				}
			});
			if (base64 != null && !"null".equals(base64)) {
				if (base64.startsWith("\"")) {
					base64 = base64.substring(1, base64.length() - 1);
				}
				textArea.setText(new String(Base64.getDecoder().decode(base64.getBytes()), StandardCharsets.UTF_8));
				textArea.setSelectionStart(selectionStart);
				textArea.setSelectionEnd(selectionEnd);
			} else {
				textArea.setText(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.showMessageDialog((Component) uiStore, e.toString());
		}
	}
}
