package com.uchicom.syo.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Action;
import javax.swing.JButton;

public class JButton2 extends JButton {

	private Dimension now;
	public JButton2(Action action) {
		super(action);
		now = getPreferredSize();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g2d);
        g2d.rotate(Math.PI / 2, now.getWidth() / 2, now.getHeight() / 2);
        g2d.translate(now.getWidth(), now.getHeight());
		
		if (ui != null) {
			Graphics scratchGraphics = (g == null) ? null : g.create();
			try {
				ui.update(scratchGraphics, this);
			} finally {
				scratchGraphics.dispose();
			}
		}
	}
}
