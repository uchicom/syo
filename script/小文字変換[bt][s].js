
textArea.setText(text.substring(0,selectionStart)
		+ selectedText.toLowerCase()
		+ text.substring(selectionEnd));
textArea.setSelectionStart(selectionStart);
textArea.setSelectionEnd(selectionEnd);