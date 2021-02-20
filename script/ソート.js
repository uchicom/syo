String[] rows = text.split("\n");
java.util.Arrays.sort(rows);
text = String.join("\n", rows);