package minesweeper.frame;

import java.awt.Graphics2D;

import minesweeper.Game;
import minesweeper.Marks;
import minesweeper.Point;

public interface IGameDrawer {
	public void printAnswer(Graphics2D gr, Game g, Marks s);
	public void setDispMine(boolean flg);
	public Point getCoordinate(double x, double y);
}
