// (c) 2017 uchicom
package com.uchicom.syo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;

import com.uchicom.syo.util.TextUtil;

//import sun.swing.SwingUtilities2;


/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class BoxView extends PlainView {

	/**
	 * @param elem
	 */
	public BoxView(Element elem) {
		super(elem);
	}

	/**
	 * Renders a line of text, suppressing whitespace at the end and expanding
	 * any tabs. This is implemented to make calls to the methods
	 * <code>drawUnselectedText</code> and <code>drawSelectedText</code> so that
	 * the way selected and unselected text are rendered can be customized.
	 *
	 * @param lineIndex
	 *            the line to draw &gt;= 0
	 * @param g
	 *            the <code>Graphics</code> context
	 * @param x
	 *            the starting X position &gt;= 0
	 * @param y
	 *            the starting Y position &gt;= 0
	 * @see #drawUnselectedText
	 * @see #drawSelectedText
	 */
	protected void drawLine(int lineIndex, Graphics g, int x, int y) {
		Element line = getElement().getElement(lineIndex);
		Element elem;

		try {
			if (line.isLeaf()) {
				drawElement(lineIndex, line, g, x, y);
			} else {
				// this line contains the composed text.
				int count = line.getElementCount();
				for (int i = 0; i < count; i++) {
					elem = line.getElement(i);
					x = drawElement(lineIndex, elem, g, x, y);
				}
			}
		} catch (BadLocationException e) {
			throw new RuntimeException("Can't render line: " + lineIndex);
		}
	}

	private int drawElement(int lineIndex, Element elem, Graphics g, int x, int y) throws BadLocationException {
		int p0 = elem.getStartOffset();
		int p1 = elem.getEndOffset();
		p1 = Math.min(getDocument().getLength(), p1);

		if (lineIndex == 0) {
			x += firstLineOffset;
		}
		AttributeSet attr = elem.getAttributes();
		if (isComposedTextAttributeDefined(attr)) {
			g.setColor(unselected);
//			x = drawComposedText(this, attr, g, x, y, p0 - elem.getStartOffset(), p1 - elem.getStartOffset());
		} else {
			if (sel0 == sel1 || selected == unselected) {
				// no selection, or it is invisible
				g.setColor(unselected);
				x = drawUnselectedText(g, x, y, p0, p1);
			} else if ((p0 >= sel0 && p0 <= sel1) && (p1 >= sel0 && p1 <= sel1)) {
				g.setColor(selected);
				x = drawSelectedText(g, x, y, p0, p1);
			} else if (sel0 >= p0 && sel0 <= p1) {
				if (sel1 >= p0 && sel1 <= p1) {
					g.setColor(unselected);
					x = drawUnselectedText(g, x, y, p0, sel0);
					g.setColor(selected);
					x = drawSelectedText(g, x, y, sel0, sel1);
					g.setColor(unselected);
					x = drawUnselectedText(g, x, y, sel1, p1);
				} else {
					g.setColor(unselected);
					x = drawUnselectedText(g, x, y, p0, sel0);
					g.setColor(selected);
					x = drawSelectedText(g, x, y, sel0, p1);
				}
			} else if (sel1 >= p0 && sel1 <= p1) {
				g.setColor(selected);
				x = drawSelectedText(g, x, y, p0, sel1);
				g.setColor(unselected);
				x = drawUnselectedText(g, x, y, sel1, p1);
			} else {
				g.setColor(unselected);
				x = drawUnselectedText(g, x, y, p0, p1);
			}
		}

		return x;
	}

	static boolean isComposedTextAttributeDefined(AttributeSet as) {
		return ((as != null) && (as.isDefined(StyleConstants.ComposedTextAttribute)));
	}

//	static int drawComposedText(View view, AttributeSet attr, Graphics g, int x, int y, int p0, int p1)
//			throws BadLocationException {
//		Graphics2D g2d = (Graphics2D) g;
//		AttributedString as = (AttributedString) attr.getAttribute(StyleConstants.ComposedTextAttribute);
//		as.addAttribute(TextAttribute.FONT, g.getFont());
//
//		if (p0 >= p1)
//			return x;
//
//		AttributedCharacterIterator aci = as.getIterator(null, p0, p1);
//		return x + (int) SwingUtilities2.drawString(getJComponent(view), g2d, aci, x, y);
//	}

	static JComponent getJComponent(View view) {
		if (view != null) {
			Component component = view.getContainer();
			if (component instanceof JComponent) {
				return (JComponent) component;
			}
		}
		return null;
	}

	/**
	 * Renders using the given rendering surface and area on that surface. The
	 * view may need to do layout and create child views to enable itself to
	 * render into the given allocation.
	 *
	 * @param g
	 *            the rendering surface to use
	 * @param a
	 *            the allocated region to render into
	 *
	 * @see View#paint
	 */
	public void paint(Graphics g, Shape a) {
		Shape originalA = a;
		Rectangle alloc = (Rectangle) a;
		tabBase = alloc.x;
		JTextComponent host = (JTextComponent) getContainer();
		Highlighter h = host.getHighlighter();
		g.setFont(host.getFont());
		sel0 = host.getSelectionStart();
		sel1 = host.getSelectionEnd();
		unselected = (host.isEnabled()) ? host.getForeground() : host.getDisabledTextColor();
		Caret c = host.getCaret();
		selected = c.isSelectionVisible() && h != null ? host.getSelectedTextColor() : unselected;
		updateMetrics();

		// If the lines are clipped then we don't expend the effort to
		// try and paint them. Since all of the lines are the same height
		// with this object, determination of what lines need to be repainted
		// is quick.
		Rectangle clip = g.getClipBounds();
		int fontHeight = metrics.getHeight();
		int heightBelow = (alloc.y + alloc.height) - (clip.y + clip.height);
		int heightAbove = clip.y - alloc.y;
		int linesBelow, linesAbove, linesTotal;

		if (fontHeight > 0) {
			linesBelow = Math.max(0, heightBelow / fontHeight);
			linesAbove = Math.max(0, heightAbove / fontHeight);
			linesTotal = alloc.height / fontHeight;
			if (alloc.height % fontHeight != 0) {
				linesTotal++;
			}
		} else {
			linesBelow = linesAbove = linesTotal = 0;
		}

		// update the visible lines
		Rectangle lineArea = lineToRect(a, linesAbove);
		int y = lineArea.y + metrics.getAscent();
		int x = lineArea.x;
		Element map = getElement();
		int lineCount = map.getElementCount();

		int endLine = Math.min(lineCount, linesTotal - linesBelow);
		lineCount--;
		int startIndex = -1;
		int endIndex = -1;
		int startWidth = -1;
		int endWidth = -1;
		LayeredHighlighter dh = (h instanceof LayeredHighlighter) ? (LayeredHighlighter) h : null;
		for (int line = 0; line < endLine; line++) {
			if (dh != null) {
				Element lineElement = map.getElement(line);

				if (lineElement.getStartOffset() <= sel0 && lineElement.getEndOffset() > sel0) {
					startIndex = sel0 - lineElement.getStartOffset();
					try {
						startWidth = metrics.stringWidth(lineElement.getDocument().getText(lineElement.getStartOffset(),
								sel0 - lineElement.getStartOffset()));
//						System.out.println(lineElement.getDocument().getText(lineElement.getStartOffset(),
//								sel0 - lineElement.getStartOffset()));
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
//					System.out.println("start:" + startIndex);
//					System.out.println("startWidth:" + startWidth);
				}
				if (lineElement.getStartOffset() <= sel1 && lineElement.getEndOffset() > sel1) {
					endIndex = sel1 - lineElement.getStartOffset();
					try {
						endWidth = metrics.stringWidth(lineElement.getDocument().getText(lineElement.getStartOffset(),
								sel1 - lineElement.getStartOffset()));
//						System.out.println(lineElement.getDocument().getText(lineElement.getStartOffset(),
//								sel1 - lineElement.getStartOffset()));
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
//					System.out.println("end:" + endIndex);
//					System.out.println("endWidth:" + endWidth);
				}
			}
		}
		for (int line = linesAbove; line < endLine; line++) {
			if (dh != null) {
				Element lineElement = map.getElement(line);

				if (line == lineCount) {// 最終行チェック
					if (startIndex > -1 && endIndex > -1) {
						try {
							int startOffset = lineElement.getStartOffset();
							String text = lineElement.getDocument().getText(startOffset,
									lineElement.getEndOffset() - startOffset);
							int s = TextUtil.getIndex(metrics, startWidth, text);
							int e = TextUtil.getIndex(metrics, endWidth, text);
							if (s > e) {
								int tmp = s;
								s = e;
								e = tmp;
							}
							dh.paintLayeredHighlights(g, startOffset + s,
									startOffset + e, originalA, host,
									this);
						} catch (BadLocationException e) {
							System.err.println(lineElement.toString());
							System.err.println(e.getMessage());
						}
					} else {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(), lineElement.getEndOffset(),
								originalA, host, this);
					}
				} else {
					if (startIndex > -1 && endIndex > -1) {
						try {
							int startOffset = lineElement.getStartOffset();
							int endOffset = lineElement.getEndOffset();
							String text = lineElement.getDocument().getText(startOffset,
									lineElement.getEndOffset() - startOffset);

							int s = TextUtil.getIndex(metrics, startWidth, text);
							int e = TextUtil.getIndex(metrics, endWidth, text);
							if (s > e) {
								int tmp = s;
								s = e;
								e = tmp;
							}
							if (startOffset + e >= endOffset) {
								e--;
							}
							dh.paintLayeredHighlights(g, startOffset + s,
									startOffset + e, originalA, host,
									this);
						} catch (BadLocationException e) {
							System.err.println(lineElement.toString());
							System.err.println(e.getMessage());
						}
					} else {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(), lineElement.getEndOffset() - 1,
								originalA, host, this);
					}
				}
			}
			drawLine(line, g, x, y);
			y += fontHeight;
			if (line == 0) {
				// This should never really happen, in so far as if
				// firstLineOffset is non 0, there should only be one
				// line of text.
				x -= firstLineOffset;
			}
		}
	}


	/**
	 * The current longest line. This is used to calculate the preferred width
	 * of the view. Since the calculation is potentially expensive we try to
	 * avoid it by stashing which line is currently the longest.
	 */
	Element longLine;

	/**
	 * Font used to calculate the longest line... if this changes we need to
	 * recalculate the longest line
	 */
	Font font;

	Segment lineBuffer;
	int tabSize;
	int tabBase;

	int sel0;
	int sel1;
	Color unselected = Color.WHITE;
	Color selected = Color.BLACK;

	/**
	 * Offset of where to draw the first character on the first line. This is a
	 * hack and temporary until we can better address the problem of text
	 * measuring. This field is actually never set directly in PlainView, but by
	 * FieldView.
	 */
	int firstLineOffset;

}
