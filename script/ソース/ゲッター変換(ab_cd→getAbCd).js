var changedText = "";
var lines = text.split(/\n/);
for (var index in lines) {
	var items = lines[index].split("_");
	var line = params[0];
	for (var index2 in items) {
		line = line + items[index2].charAt(0).toUpperCase() + items[index2].substring(1);
	}
	changedText = changedText + line + "\n";
}
textArea.setText(changedText);