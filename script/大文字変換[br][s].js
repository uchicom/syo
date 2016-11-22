textArea.setText(text.substring(0,selectionStart)
		+ selectedText.toUpperCase()
		+ text.substring(selectionEnd));

textArea.setSelectionStart(selectionStart);
textArea.setSelectionEnd(selectionEnd);