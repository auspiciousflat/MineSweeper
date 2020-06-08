package minesweeper.main;

import java.util.ArrayList;
import java.util.List;

import minesweeper.Game;
import minesweeper.GamePrinter;
import minesweeper.IGame;
import minesweeper.ISolver;
import minesweeper.Marks;
import minesweeper.Solver4;
import minesweeper.Solver5;

public class SolveCompare {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<ISolver> solvers = new ArrayList<>();
		solvers.add(new Solver4());
		solvers.add(new Solver5());
		
		Game g = new Game(30,16,99);
		g.open(0, 0);
		
		List<Game> games = new ArrayList<>();
		for (int i = 0; i < solvers.size(); i++) {
			games.add(g.clone());
		}
		
		List<Marks> marks = new ArrayList<>();
		for (int i = 0; i < solvers.size(); i++) {
			ISolver solver = solvers.get(i);
			IGame game = games.get(i);
			marks.add(solver.solve(game, new Marks(game)));
		}
		
		//result
		for (int i = 0; i < solvers.size(); i++) {
			ISolver solver = solvers.get(i);
			Game game = games.get(i);
			Marks mark = marks.get(i);
			
			System.out.println("solver=" + solver.getClass().toString());
			GamePrinter.printAnswer(game, mark);
			solver.printSummary();
			System.out.println("open Count=" + game.getOpenCount());
		}
	}
}
