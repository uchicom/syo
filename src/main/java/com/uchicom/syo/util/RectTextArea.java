/* (c) 2015 uchicom */
package com.uchicom.syo.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

public class RectTextArea extends JTextArea {

	public RectTextArea() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RectTextArea(String text) {
		super(text);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RectTextArea(Document doc) {
		super(doc);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RectTextArea(int rows, int columns) {
		super(rows, columns);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RectTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RectTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics m = g.getFontMetrics();

		String str = getSelectedText();

		if (str == null)
			return;
		int width = m.stringWidth(str);

		g2d.setXORMode(Color.WHITE);
		DefaultCaret caret = (DefaultCaret) getCaret();
		Point point = caret.getMagicCaretPosition();
		int dot = caret.getDot();
		int mark = caret.getMark();
		if (point == null)
			return;
		if (dot < mark) {
			g2d.fillRect(point.x, point.y, width, caret.height);
		} else {
			g2d.fillRect(point.x - width, point.y, width, caret.height);
		}

	}
}
