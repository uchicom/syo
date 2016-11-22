var excludeCondition = javax.swing.JOptionPane.showInputDialog(new javax.swing.JLabel("入力してください"));

if (excludeCondition != null) {

	var val = textArea.getText();
	var rows = val.split(/\r\n|\r|\n/);
	textArea.setText(out(exclude(rows)));

}
function exclude(rows) {
	var values = [];
	var index = 0;
	for (var i = 0; i < rows.length; i++) {
		if (!rows[i].contains(excludeCondition)) {
			values[index++] = rows[i];
		}
	}
	return values;
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