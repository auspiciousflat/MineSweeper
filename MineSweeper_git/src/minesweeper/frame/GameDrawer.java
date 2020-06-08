package minesweeper.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import minesweeper.Game;
import minesweeper.IGame;
import minesweeper.Marks;

public class GameDrawer {
	static final int SIZE = 16;
	
	public static void drawString(Graphics2D g, String s, int x, int y) {
		g.drawString(s, x * SIZE + 32, y * SIZE + 32);
	}
	
	public static void drawStringleft(Graphics2D g, String s, int x, int y) {
		Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x * SIZE + 32 + SIZE - (int) r.getWidth(), y * SIZE + 32);
	}
	
	public static Dimension getDrawingSize(IGame g, Marks s) {
		int x = g.getWidth() * SIZE + 32;
		int y = g.getHeight() * SIZE + 32;
		return new Dimension(x, y);
	}
	
	public static void paintComponent(Graphics2D gr, IGame g, Marks s) {
		String str;
		Graphics2D gr2 = (Graphics2D)gr.create();
		gr2.setColor(Color.LIGHT_GRAY);
		for (int x = 0; x < g.getWidth(); x++)
			drawStringleft(gr2, Integer.toString(x), x, -1);
		for (int y = 0; y < g.getHeight(); y++)
			drawStringleft(gr2, Integer.toString(y), -1, y);
		gr2.dispose();
		
		for (int y = 0; y < g.getHeight(); y++) {
			for (int x = 0; x < g.getWidth(); x++) {
				if (g.isOpened(x, y)) {
					if (g.getPanelInfo(x, y) == -1) {
						str = "@";
					} else {
						if (g.getPanelInfo(x, y) > 0) {
							str = String.format("%01d", g.getPanelInfo(x, y));
						} else {
							str = ".";
						}
					}
				} else {
					if (s.get(x, y) == Marks.F.MINE) {
						str = "*";
					} else if (s.get(x, y) == Marks.F.SAFE) {
						str = "_";
					} else {
						str = "#";
					}
				}
				drawString(gr, str, x, y);
			}
		}
	}
	
	public static void printAnswer(Graphics2D gr, Game g, Marks s) {
		String str;
		Graphics2D gr2 = (Graphics2D)gr.create();
		gr2.setColor(Color.LIGHT_GRAY);
		for (int x = 0; x < g.getWidth(); x++)
			drawStringleft(gr2, Integer.toString(x), x, -1);
		for (int y = 0; y < g.getHeight(); y++)
			drawStringleft(gr2, Integer.toString(y), -1, y);
		gr2.dispose();
		
		for (int y = 0; y < g.getHeight(); y++) {
			for (int x = 0; x < g.getWidth(); x++) {
				if (g.isOpened(x, y)) {
					if (g.getPanelInfo(x, y) == -1) {
						str = "@";
					} else {
						if (g.getPanelInfo(x, y) > 0) {
							str = String.format("%01d", g.getPanelInfo(x, y));
						} else {
							str = ".";
						}
					}
				} else {
					if (s.get(x, y) == Marks.F.MINE) {
						str = "Å°";
					} else if (s.get(x, y) == Marks.F.SAFE) {
						str = "_";
					} else {
						str = "Å†";
					}
				}
				
				gr2 = (Graphics2D)gr.create();
				if (g.isMine(x, y)) gr2.setColor(Color.RED);
				drawStringleft(gr2, str, x, y);
				gr2.dispose();
			}
		}
	}
}
