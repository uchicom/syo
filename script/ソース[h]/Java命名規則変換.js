var changedText = "";
//行に分割
var lines = text.split(/\n/);
for (var index in lines) {
	//アンダースコアで分割
	var items = lines[index].split("_");
	var prefix = "";
	if (param != null) {
		prefix = param;
	}
	var line = "";
	for (var index2 in items) {
		line = line + items[index2].charAt(0).toUpperCase() + items[index2].substring(1);
	}
	changedText = changedText + prefix + line + "\n";
}
textArea.setText(changedText);