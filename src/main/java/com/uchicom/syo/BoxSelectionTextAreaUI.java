// (c) 2017 uchicom
package com.uchicom.syo;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.View;
import javax.swing.text.WrappedPlainView;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class BoxSelectionTextAreaUI extends BasicTextAreaUI {

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
				System.out.println("box3view");
				v = new BoxView(elem);
			}
			return v;
		}
		return null;
	}

	@Override
	protected Highlighter createHighlighter() {
		return new BasicHighlighter() {
			/**
			 * ハイライトを消す
			 */
			public void removeHighlight(Object tag) {
				super.removeHighlight(tag);
				getComponent().repaint(x, y, width, height);
			}
			/**
			 * ハイライトを描画
			 */
			@Override
			public void paintLayeredHighlights(Graphics g, int p0, int p1, Shape viewBounds, JTextComponent editor,
					View view) {

				for (int counter = getHighlights().length - 1; counter >= 0; counter--) {
					union(((LayeredHighlighter.LayerPainter) getHighlights()[counter].getPainter()).paintLayer(g, p0, p1,
							viewBounds, editor, view));
				}
			}

			//クリア領域
			int x;
			int y;
			int width;
			int height;

			public void union(Shape bounds) {
				if (bounds == null)
					return;

				Rectangle alloc;
				if (bounds instanceof Rectangle) {
					alloc = (Rectangle) bounds;
				} else {
					alloc = bounds.getBounds();
				}
				if (width == 0 || height == 0) {
					x = alloc.x;
					y = alloc.y;
					width = alloc.width;
					height = alloc.height;
				} else {
					width = Math.max(x + width, alloc.x + alloc.width);
					height = Math.max(y + height, alloc.y + alloc.height);
					x = Math.min(x, alloc.x);
					width -= x;
					y = Math.min(y, alloc.y);
					height -= y;
				}
			}

		};
	}


}