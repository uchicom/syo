Map uniq(String[] rows) {
	java.util.Map<String, Integer> map = new java.util.HashMap<>();
	for (int i = 0; i < rows.length; i++) {
		Integer value = map.get(rows[i]);
		if (value == null) {
			map.put(rows[i], 1);
		} else {
			map.put(rows[i], value + 1);
		}
	}
	return map;
}
/**
 * 連想配列を取得して、count uniqValue形式で出力します
 * @param rows
 * @returns {String}
 */
String out(Map<String, Integer> map) {
	StringBuilder sb = new StringBuilder(1024);
	for (Map.Entry<String, Integer> entry : map.entrySet()) {
		sb.append(entry.getKey() + "\t").append(entry.getValue() + "\n");
	}
	return sb.toString();
}

var rows = text.split("\r\n|\r|\n");

text = (out(uniq(rows)));
