// (c) 2017 uchicom
package com.uchicom.syo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxPlainView;
import javax.swing.text.Caret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.WrappedPlainView;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class BoxSelectionTextAreaUI extends BasicTextAreaUI {

	private final JTextArea textArea;

	/**
	 * Creates the view for an element. Returns a WrappedPlainView or PlainView.
	 *
	 * @param elem
	 *            the element
	 * @return the view
	 */
	@Override
	public View create(Element elem) {
		JTextComponent c = getComponent();
		if (c instanceof JTextArea) {
			JTextArea area = (JTextArea) c;
			View v;
			if (area.getLineWrap()) {
				v = new WrappedPlainView(elem, area.getWrapStyleWord());
			} else {
				v = new BoxPlainView(elem);
			}
			return v;
		}
		return null;
	}

	@Override
	public void damageRange(JTextComponent t, int p0, int p1) {

		System.out.println("dr;" + p0 + "," + p1);
		super.paint(null, t);
		super.damageRange(t, p0, p1);
	}

	@Override
	protected Highlighter createHighlighter() {
		return new BasicHighlighter() {
			@Override
			public void paint(Graphics g) {
				// PENDING(prinz) - should cull ranges not visible
				Highlight[] hs = getHighlights();
				if (hs != null) {
					System.out.println("paint:highlights:" + hs.length);

				}
				super.paint(g);
			}

			@Override
			public Object addHighlight(int p0, int p1, HighlightPainter p) throws BadLocationException {
				Object obj = super.addHighlight(p0, p1, p);

				System.out.println("a:" + obj + ":" + p0 + ":" + p1);
				return obj;
			}

			@Override
			public void changeHighlight(Object tag, int p0, int p1) throws BadLocationException {
				System.out.println("c:" + tag + ":" + p0 + "," + p1);
				super.changeHighlight(tag, p0, p1);
			}
		};
	}

	@Override
	protected Caret createCaret() {

		return new BasicCaret() {

			@Override
			protected HighlightPainter getSelectionPainter() {
				System.out.println("getSelectionPainter");
				return new DefaultHighlighter.DefaultHighlightPainter(null) {
					@Override
					public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
						// System.out.println(offs0 + ":" + offs1 + ";" +
						// bounds);
						Rectangle alloc = bounds.getBounds();
						try {
							// --- determine locations ---
							TextUI mapper = c.getUI();
							Rectangle p0 = mapper.modelToView(c, offs0);
							Rectangle p1 = mapper.modelToView(c, offs1);

							// --- render ---
							Color color = getColor();
							color = Color.BLUE;
							if (color == null) {
								g.setColor(c.getSelectionColor());
							} else {
								g.setColor(color);
							}
							if (p0.y == p1.y) {
								// same line, render a rectangle
								Rectangle r = p0.union(p1);
								g.fillRect(r.x, r.y, r.width, r.height);
							} else {
								// different lines
								int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
								g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
								if ((p0.y + p0.height) != p1.y) {
									g.fillRect(alloc.x, p0.y + p0.height, alloc.width, p1.y - (p0.y + p0.height));
								}
								g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
							}
						} catch (BadLocationException e) {
							// can't render
						}

					}

					public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c,
							View view) {
						// System.out.println("paintLayer:" + offs0 + "," +
						// offs1 + ":" + bounds);
						Color color = getColor();

						color = Color.BLUE;
						if (color == null) {
							g.setColor(c.getSelectionColor());
						} else {
							g.setColor(color);
						}

						Rectangle r;

						if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
							// Contained in view, can just use bounds.
							if (bounds instanceof Rectangle) {
								r = (Rectangle) bounds;
							} else {
								r = bounds.getBounds();
							}
						} else {
							// Should only render part of View.
							try {
								// --- determine locations ---
								Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,
										Position.Bias.Backward, bounds);
								r = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
							} catch (BadLocationException e) {
								// can't render
								r = null;
							}
						}

						if (r != null) {
							// If we are asked to highlight, we should draw
							// something even
							// if the model-to-view projection is of zero width
							// (6340106).
							r.width = Math.max(r.width, 1);

							g.fillRect(r.x, r.y, r.width, r.height);
						}

						return r;
					}

				};
			}

			@Override
			public void paint(Graphics g) {

				// System.out.println(x + ":" + y + ":" + width + ";" + height);
				super.paint(g);
			}
		};
	}

	public BoxSelectionTextAreaUI(JTextArea textArea) {
		this.textArea = textArea;

		textArea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println(e);

				// if (textArea.getText().length() > 10) {
				// System.out.println(textArea.getText().length());
				// // 検索時にハイライトを実施
				// Highlighter highlighter = textArea.getHighlighter();
				// // highlighter.install(textArea);
				// HighlightPainter hp = new
				// DefaultHighlighter.DefaultHighlightPainter(Color.RED);
				// try {
				// highlighter.addHighlight(0, 3, hp);
				// } catch (BadLocationException ef) {
				// // TODO 自動生成された catch ブロック
				// ef.printStackTrace();
				// }
				// }
			}

		});

	}

	@Override
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);

	}
}