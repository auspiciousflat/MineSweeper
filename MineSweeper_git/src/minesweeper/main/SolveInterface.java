package minesweeper.main;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.JFrame;

import minesweeper.Solver;
import minesweeper.Solver2;
import minesweeper.Solver3;
import minesweeper.Solver4;
import minesweeper.Solver41;
import minesweeper.Solver5;
import minesweeper.Solver6;
import minesweeper.Solver60;
import minesweeper.Solver62;
import minesweeper.frame.GameFrame;
import minesweeper.frame.GameModel;
import minesweeper.frame.GamePanel;

public class SolveInterface {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getGlobal();
		logger.addHandler(new StreamHandler(){
			{
				setOutputStream(System.out); 
				setLevel(Level.INFO);
			}
			public void publish(LogRecord record) {
				super.publish(record);
				flush();
			}
		});
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
		
		GameModel model = new GameModel();
		model.addSolver(new Solver());
		model.addSolver(new Solver2());
		model.addSolver(new Solver3());
		model.addSolver(new Solver4());
		model.addSolver(new Solver41());
		model.addSolver(new Solver5());
		model.addSolver(new Solver6());
		model.addSolver(new Solver62());
		model.addSolver(new Solver60());
		
		GamePanel p = new GamePanel(model);
		GameFrame f = new GameFrame(p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
