var val = textArea.getText();

var rows = val.split(/\r\n|\r|\n/);

textArea.setText(out(uniq(rows)));

/*
 * [uniqValue: count]の連想配列を取得します
 */
function uniq(rows) {
	var values = [];
	for (var i = 0; i < rows.length; i++) {
		if (values[rows[i]] == null) {
			values[rows[i]] = 1;
		} else {
			values[rows[i]] += 1;
		}
	}
	return values;
}
/**
 * 連想配列を取得して、count uniqValue形式で出力します
 * @param rows
 * @returns {String}
 */
function out(rows) {
	var string = "";
	for (var i in rows) {
		string += i + "\t" + rows[i] + "\n";
	}
	return string;
}