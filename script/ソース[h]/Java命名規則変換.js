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
		if (prefix == "var") {
			if (index2 == 0) {
				line = items[index2];
			} else {
				line = line + items[index2].charAt(0).toUpperCase() + items[index2].substring(1);
			}
		} else {
			line = line + items[index2].charAt(0).toUpperCase() + items[index2].substring(1);
		}
	}

	if (prefix == "var") {
		changedText = changedText + line + "\n";
	} else {
		changedText = changedText + prefix + line + "\n";
	}
}
textArea.setText(changedText);