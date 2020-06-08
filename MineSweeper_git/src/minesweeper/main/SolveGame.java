package minesweeper.main;

import minesweeper.Game;
import minesweeper.GamePrinter;
import minesweeper.ISolver;
import minesweeper.Marks;
import minesweeper.Solver5;

public class SolveGame {
	private static int[] data1 = {
		0, 0, 1,
		0, 0, 0,
		1, 1, 0		
	};
	
	private static int[] data2 = {
		0, 1, 0, 0, 0,
		0, 0, 0, 0, 0,
		0, 0, 0, 1, 0,		
	};
	
	private static int[] data3 = {
		0, 0, 1,
		0, 0, 0,
		1, 0, 0,		
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game g = new Game(30,16,99);
		g.open(0, 0);
		
//		Game g = new Game(3, 3, data3);
//		g.open(0, 0);
//		g.open(2, 1);
//		g.open(3, 1);
		
		ISolver s = new Solver5();
		Marks m = s.solve(g, new Marks(g));
		GamePrinter.printAnswer(g, m);
		s.printSummary();
	}
}
