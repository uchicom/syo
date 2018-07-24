// (c) 2017 uchicom
package com.uchicom.syo.util;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.LayeredHighlighter;

import com.uchicom.syo.ui.BoxSelectionTextAreaUI;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class JBoxTextArea extends JTextArea {
	@Override
	public void paste() {
		if (getUI() instanceof BoxSelectionTextAreaUI) {
			// カーソルの位置から入れ替える。
			Caret caret = getCaret();
			int p0 = Math.min(caret.getDot(), caret.getMark());
			int p1 = Math.max(caret.getDot(), caret.getMark());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
				String value = (String) clipboard.getData(DataFlavor.stringFlavor);
				if (value.length() <= 0) {
					return;
				}
				String[] lines = value.split("\n");

				Document doc = getDocument();
				Element map = doc.getDefaultRootElement();
				if (ui instanceof BoxSelectionTextAreaUI) {
					FontMetrics metrics = getFontMetrics(getFont());
					int lineCount = map.getElementCount();

					int startIndex = -1;
					int endIndex = -1;
					int startWidth = -1;
					int endWidth = -1;
					Highlighter h = getHighlighter();
					LayeredHighlighter dh = (h instanceof LayeredHighlighter) ? (LayeredHighlighter) h : null;
					int startLine = 0;
					int endLine = 0;

					try {
						for (int line = 0; line < lineCount; line++) {
							if (dh != null) {
								Element lineElement = map.getElement(line);
								System.out.println("offset:" + lineElement.getStartOffset() + ":"+ lineElement.getEndOffset());

								if (lineElement.getStartOffset() <= p0 && lineElement.getEndOffset() > p0) {
									startLine = line;
									startIndex = p0 - lineElement.getStartOffset();
									startWidth = metrics.stringWidth(lineElement.getDocument()
											.getText(lineElement.getStartOffset(), p0 - lineElement.getStartOffset()));
								}
								if (lineElement.getStartOffset() <= p1 && lineElement.getEndOffset() > p1) {
									endLine = line;
									endIndex = p1 - lineElement.getStartOffset();
									endWidth = metrics.stringWidth(lineElement.getDocument()
											.getText(lineElement.getStartOffset(), p1 - lineElement.getStartOffset()));

									break;
								}
							}
						}
						if (startIndex > -1 && endIndex > -1) {
							if (startWidth > endWidth) {
								int tmp = startWidth;
								startWidth = endWidth;
								endWidth = tmp;
							}
							lineCount--;
							int index = 0;
							endLine = startLine + lines.length;
							if (endLine > lineCount) {
								endLine = lineCount;
							}
							System.out.println(startWidth + ":" + endWidth);
							System.out.println(startLine + ";" + endLine);

							for (int line = startLine; line <= endLine; line++) {
								if (index >= lines.length) {

									System.out.println("break:" + index);

									break;
								}
								if (dh != null) {
									Element lineElement = map.getElement(line);
									try {
										int startOffset = lineElement.getStartOffset();
										String text = lineElement.getDocument().getText(startOffset,
												lineElement.getEndOffset() - startOffset);
										if (line == endLine && index < lines.length - 1) {
											StringBuffer strBuff = new StringBuffer(value.length());
											for (int i = index; i < lines.length; i++) {
												if (strBuff.length() > 0) {
													strBuff.append("\n");
												}
												strBuff.append(lines[i]);
											}
											System.out.println(strBuff.toString());
											System.out.println("getIndex:" + TextUtil.getIndex(metrics, startWidth, text));
											doc.insertString(startOffset + TextUtil.getIndex(metrics, startWidth, text),
													strBuff.toString(), null);
											System.out.println("行" + line + ":" + strBuff.toString());
										} else {
											System.out.println("getIndex:" + TextUtil.getIndex(metrics, startWidth, text));

											System.out.println("行" + line + ":" + lines[index]);
											doc.insertString(startOffset + TextUtil.getIndex(metrics, startWidth, text),
													lines[index++], null);
										}

									} catch (BadLocationException e) {
										System.err.println(lineElement.toString());
										System.err.println(e.getMessage());
									}
								}
							}

						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			super.paste();
		}
	}

	@Override
	public void cut() {
		if (getUI() instanceof BoxSelectionTextAreaUI) {
			// カーソルの位置から切り取る。
			Caret caret = getCaret();
			int p0 = Math.min(caret.getDot(), caret.getMark());
			int p1 = Math.max(caret.getDot(), caret.getMark());
			if (p0 != p1) {
				String txt = null;
				try {
					Document doc = getDocument();
					Element map = doc.getDefaultRootElement();
					if (ui instanceof BoxSelectionTextAreaUI) {
						FontMetrics metrics = getFontMetrics(getFont());
						int lineCount = map.getElementCount();

						int startIndex = -1;
						int endIndex = -1;
						int startWidth = -1;
						int endWidth = -1;
						Highlighter h = getHighlighter();
						LayeredHighlighter dh = (h instanceof LayeredHighlighter) ? (LayeredHighlighter) h : null;
						int startLine = 0;
						int endLine = 0;

						try {
							for (int line = 0; line < lineCount; line++) {
								if (dh != null) {
									Element lineElement = map.getElement(line);

									if (lineElement.getStartOffset() <= p0 && lineElement.getEndOffset() > p0) {
										startLine = line;
										startIndex = p0 - lineElement.getStartOffset();
										startWidth = metrics.stringWidth(lineElement.getDocument().getText(
												lineElement.getStartOffset(), p0 - lineElement.getStartOffset()));
									}
									if (lineElement.getStartOffset() <= p1 && lineElement.getEndOffset() > p1) {
										endLine = line;
										endIndex = p1 - lineElement.getStartOffset();
										endWidth = metrics.stringWidth(lineElement.getDocument().getText(
												lineElement.getStartOffset(), p1 - lineElement.getStartOffset()));

										break;
									}
								}
							}
						} catch (BadLocationException e) {
							e.printStackTrace();
						}

						if (startIndex > -1 && endIndex > -1) {
							if (startWidth > endWidth) {
								int tmp = startWidth;
								startWidth = endWidth;
								endWidth = tmp;
							}
							lineCount--;
							System.out.println(startWidth + ":" + endWidth);
							StringBuffer strBuff = new StringBuffer(1024);
							for (int line = startLine; line <= endLine; line++) {
								if (dh != null) {
									Element lineElement = map.getElement(line);
									try {
										int startOffset = lineElement.getStartOffset();
										String text = lineElement.getDocument().getText(startOffset,
												lineElement.getEndOffset() - startOffset);
										if (strBuff.length() > 0) {
											strBuff.append("\n");
										}
										System.out.println(
												"最終行:" + TextUtil.getString(metrics, startWidth, endWidth, text));
										strBuff.append(
												TextUtil.getString(metrics, startWidth, endWidth, text, lineElement));
									} catch (BadLocationException e) {
										System.err.println(lineElement.toString());
										System.err.println(e.getMessage());
									}
								}
							}
							txt = strBuff.toString();
						} else {
							txt = doc.getText(p0, p1 - p0);
						}
					} else {
						txt = doc.getText(p0, p1 - p0);
					}
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection ss = new StringSelection(txt);
					clipboard.setContents(ss, ss);
				} catch (BadLocationException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			}
		} else {
			super.paste();
		}
	}

	@Override
	public String getSelectedText() {
		Caret caret = getCaret();
		String txt = null;
		int p0 = Math.min(caret.getDot(), caret.getMark());
		int p1 = Math.max(caret.getDot(), caret.getMark());
		if (p0 != p1) {
			try {
				Document doc = getDocument();
				Element map = doc.getDefaultRootElement();
				if (getUI() instanceof BoxSelectionTextAreaUI) {
					FontMetrics metrics = getFontMetrics(getFont());
					int sel0 = getSelectionStart();
					int sel1 = getSelectionEnd();
					int lineCount = map.getElementCount();

					int startIndex = -1;
					int endIndex = -1;
					int startWidth = -1;
					int endWidth = -1;
					Highlighter h = getHighlighter();
					LayeredHighlighter dh = (h instanceof LayeredHighlighter) ? (LayeredHighlighter) h : null;
					int startLine = 0;
					int endLine = 0;

					try {
						for (int line = 0; line < lineCount; line++) {
							if (dh != null) {
								Element lineElement = map.getElement(line);

								if (lineElement.getStartOffset() <= sel0 && lineElement.getEndOffset() > sel0) {
									startLine = line;
									startIndex = sel0 - lineElement.getStartOffset();
									startWidth = metrics.stringWidth(lineElement.getDocument().getText(
											lineElement.getStartOffset(), sel0 - lineElement.getStartOffset()));
								}
								if (lineElement.getStartOffset() <= sel1 && lineElement.getEndOffset() > sel1) {
									endLine = line;
									endIndex = sel1 - lineElement.getStartOffset();
									endWidth = metrics.stringWidth(lineElement.getDocument().getText(
											lineElement.getStartOffset(), sel1 - lineElement.getStartOffset()));

									break;
								}
							}
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (startWidth > endWidth) {
						int tmp = startWidth;
						startWidth = endWidth;
						endWidth = tmp;
					}
					lineCount--;
					System.out.println(startWidth + ":" + endWidth);
					StringBuffer strBuff = new StringBuffer(1024);
					for (int line = startLine; line <= endLine; line++) {
						if (dh != null) {
							Element lineElement = map.getElement(line);
							if (startIndex > -1 && endIndex > -1) {
								try {
									int startOffset = lineElement.getStartOffset();
									String text = lineElement.getDocument().getText(startOffset,
											lineElement.getEndOffset() - startOffset);
									if (strBuff.length() > 0) {
										strBuff.append("\n");
									}
									System.out
											.println("最終行:" + TextUtil.getString(metrics, startWidth, endWidth, text));
									strBuff.append(TextUtil.getString(metrics, startWidth, endWidth, text));
								} catch (BadLocationException e) {
									System.err.println(lineElement.toString());
									System.err.println(e.getMessage());
								}
							}
						}
					}
					txt = strBuff.toString();
				} else {
					txt = doc.getText(p0, p1 - p0);
				}
			} catch (BadLocationException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		return txt;
	}
}
