// (c) 2017 uchicom
package com.uchicom.syo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.PlainView;
import javax.swing.text.View;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class BoxPlainView extends PlainView {

    /**
     * Constructs a new PlainView wrapped on an element.
     *
     * @param elem the element
     */
    public BoxPlainView(Element elem) {
        super(elem);
    }

    /**
     * Renders using the given rendering surface and area on that surface.
     * The view may need to do layout and create child views to enable
     * itself to render into the given allocation.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     *
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
    	super.paint(g, a);
        Shape originalA = a;
        Rectangle alloc = (Rectangle) a;
        tabBase = alloc.x;
        JTextComponent host = (JTextComponent) getContainer();
        System.out.println(host);
        Highlighter h = host.getHighlighter();
        g.setFont(host.getFont());
        sel0 = host.getSelectionStart();
        sel1 = host.getSelectionEnd();
        unselected = (host.isEnabled()) ?
            host.getForeground() : host.getDisabledTextColor();
        Caret c = host.getCaret();
        selected = c.isSelectionVisible() && h != null ?
                       host.getSelectedTextColor() : unselected;
        updateMetrics();

        // If the lines are clipped then we don't expend the effort to
        // try and paint them.  Since all of the lines are the same height
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
        LayeredHighlighter dh = (h instanceof LayeredHighlighter) ?
                           (LayeredHighlighter)h : null;
                           System.out.println(linesAbove + ":" + endLine);
        for (int line = linesAbove; line < endLine; line++) {
            if (dh != null) {
                Element lineElement = map.getElement(line);
            	System.out.println("offset:" + lineElement.getStartOffset() +":" +
                        lineElement.getEndOffset());
                if (line == lineCount) {
                	System.out.println("a");
                    dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
                                              lineElement.getEndOffset(),
                                              originalA, host, this);
                }
                else {
                	System.out.println("b");
                    dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
                                              lineElement.getEndOffset() - 1,
                                              originalA, host, this);
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


    // --- member variables -----------------------------------------------


    int tabBase;

    int sel0;
    int sel1;
    Color unselected = Color.BLACK;
    Color selected = Color.RED;

    /**
     * Offset of where to draw the first character on the first line.
     * This is a hack and temporary until we can better address the problem
     * of text measuring. This field is actually never set directly in
     * PlainView, but by FieldView.
     */
    int firstLineOffset;
}
