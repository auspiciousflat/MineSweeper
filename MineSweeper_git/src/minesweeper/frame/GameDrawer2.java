package minesweeper.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import minesweeper.Point;
import minesweeper.Game;
import minesweeper.IGame;
import minesweeper.Marks;

public class GameDrawer2 implements IGameDrawer {
	static final int SIZE = 16;
	
	static final Image panel = getImg("/minesweeper/resource/panel.png"); 
	static final Image minepanel = getImg("/minesweeper/resource/minepanel.png");
	static final Image flaggedpanel = getImg("/minesweeper/resource/flaggedpanel.png");
	static final Image flaggedminepanel = getImg("/minesweeper/resource/flaggedminepanel.png"); 
	static final Image mine = getImg("/minesweeper/resource/mine.png");
	static final Image safe = getImg("/minesweeper/resource/safe.png"); 
	static final Image nums[] = new Image[9];
	static {
		for (int i = 0; i < nums.length; i++) nums[i] = getImg("/minesweeper/resource/" + i + ".png");
	}
	
	boolean isDispMine = true;
	
	public static Image getImg(String imagePath) {
		Image image;
		try {
			InputStream s = GameDrawer2.class.getResourceAsStream(imagePath);
			if (s == null) {
				throw new IOException();
			}
			image = ImageIO.read(s);
		} catch (IOException e) {
			throw new IllegalArgumentException("File not found. val=" + imagePath, e);
		}
		return image;
	}
	
	public static void drawString(Graphics2D g, String s, int x, int y) {
		g.drawString(s, x * SIZE + 24, y * SIZE + 32);
	}
	
	public static void drawStringleft(Graphics2D g, String s, int x, int y) {
		Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x * SIZE + 24 + SIZE - (int) r.getWidth(), y * SIZE + 32);
	}
	
	public static void drawImage(Graphics2D g, Image img, int x, int y) {
		g.drawImage(img, x * SIZE + 24 + 2, y * SIZE + 16 + 4, null);
	}
	
	public static Dimension getDrawingSize(IGame g, Marks s) {
		int x = g.getWidth() * SIZE + 16;
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

	@Override
	public void printAnswer(Graphics2D gr, Game g, Marks s) {
		
		Graphics2D gr2 = (Graphics2D)gr.create();
		gr2.setColor(Color.LIGHT_GRAY);
		for (int x = 0; x < g.getWidth(); x++)
			drawStringleft(gr2, Integer.toString(x), x, -1);
		for (int y = 0; y < g.getHeight(); y++)
			drawStringleft(gr2, Integer.toString(y), -1, y);
		gr2.dispose();
		
		for (int y = 0; y < g.getHeight(); y++) {
			for (int x = 0; x < g.getWidth(); x++) {
				Image img = null;
				String str = null;
				if (g.isOpened(x, y)) {
					if (g.getPanelInfo(x, y) == -1) {
						img = mine;
					} else {
						img = nums[g.getPanelInfo(x, y)];
					}
				} else {
					if (s.get(x, y) == Marks.F.MINE) {
						if (isDispMine && g.isMine(x, y)) {
							img = flaggedminepanel;
						} else {
							img = flaggedpanel;
						}
					} else if (s.get(x, y) == Marks.F.SAFE) {
						img = safe;
					} else {
						if (isDispMine && g.isMine(x, y)) {
							img = minepanel;
						} else {
							img = panel;
						}
					}
				}
				
				gr2 = (Graphics2D)gr.create();
				if (g.isMine(x, y)) gr2.setColor(Color.RED);
				if (img != null) {
					drawImage(gr2, img, x, y);
				} else {
					drawStringleft(gr2, str, x, y);
				}
				gr2.dispose();
			}
		}
	}

	@Override
	public void setDispMine(boolean flg) {
		isDispMine = flg;
	}

	@Override
	public Point getCoordinate(double x, double y) {
		//g.drawImage(img, x * SIZE + 24 + 2, y * SIZE + 16 + 4, null);
		return new Point((int) (x - 24 - 2) / SIZE, (int) (y - 16 - 4) / SIZE);
	}
}
