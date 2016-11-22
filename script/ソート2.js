var val = textArea.getText();

var rows = val.split(/\r\n|\r|\n/);

rows.sort(compare);
textArea.setText(out(rows));

function compare(val1, val2) {
	var cells1 = val1.split("\t");
	var cells2 = val2.split("\t");
	if (cells1[1] < cells2[1]) {
		return 1;
	} else if (cells1[1] > cells2[1]) {
		return -1;
	} else {
		return 0;
	}
}
function out(rows) {
	var string = "";
	for (var i = 0; i < rows.length; i++) {
		string += rows[i];
		if (i + 1 < rows.length) {
			string += "\n";
		}
	}
	return string;
}