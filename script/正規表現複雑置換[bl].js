var CollectionsAndFiles = new JavaImporter(
	javax.swing);

with (CollectionsAndFiles) {
	var reg = JOptionPane.showInputDialog(textArea, "検索する正規表現");
	var value = JOptionPane.showInputDialog(textArea, "置換後の文字列");
	java.lang.System.out.println(value);
	textArea.setText(text.replaceAll(reg, value.replaceAll("\\\\n","\n")));
}
