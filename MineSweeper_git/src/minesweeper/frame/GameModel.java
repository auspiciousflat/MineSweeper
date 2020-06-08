package minesweeper.frame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import minesweeper.Game;
import minesweeper.IGame;
import minesweeper.ISolver;
import minesweeper.Marks;

public class GameModel {

	private List<Game> _games = new ArrayList<>();
	private List<Marks> _marks = new ArrayList<>();
	private List<ISolver> _solvers = new ArrayList<>();
	private List<Boolean> _isEnable = new ArrayList<>();

	private CustomGameInfo _customGameInfo = 
			new CustomGameInfo(30, 16, 99, CustomGameInfo.OpenPoint.LEFTUP, true);
	
	private Game _g;
	
	public GameModel() {
		createGameImpl();
	}
	
	public int getCount() {
		return _solvers.size();
	}
	
	public void addSolver(ISolver solver) {
		_solvers.add(solver);
		_games.add(_g.clone());
		_marks.add(new Marks(_g));
		_isEnable.add(true);
	}
	
	public void createGame() {
		createGameImpl();
		initSolvers();
	}

	private void createGameImpl() {
		int px = 0;
		int py = 0;
		if (_customGameInfo.getOpenPoint() == CustomGameInfo.OpenPoint.CENTER) {
			px = _customGameInfo.getX() / 2;
			py = _customGameInfo.getY() / 2;
		}
		if (_customGameInfo.isSafeOpen()) {
			_g = new Game(_customGameInfo.getX(), _customGameInfo.getY(), _customGameInfo.getMine(), px, py);
		} else {
			_g = new Game(_customGameInfo.getX(), _customGameInfo.getY(), _customGameInfo.getMine());
			_g.open(px, py);
		}
	}

	private void initSolvers() {
		for (int i = 0; i < _solvers.size(); i++) {
			_games.set(i, _g.clone());
			_marks.set(i, new Marks(_g));
		}
	}
	
	public void resetAll() {
		for (int i = 0; i < _solvers.size(); i++) {
			reset(i);
		}
	}
	
	public void reset(int i) {
		_games.set(i, _g.clone());
		_marks.set(i, new Marks(_g));
	}
	
	public Game getGame(int i) {
		return _games.get(i);
	}
	
	public Marks getMark(int i) {
		return _marks.get(i);
	}
	
	public ISolver getSolver(int i) {
		return _solvers.get(i);
	}
	
	public boolean isEnable(int i) {
		return _isEnable.get(i);
	}
	
	public void setIsEnable(int i, boolean flg) {
		_isEnable.set(i, flg);
	}
	
	public void solve(int i,  Observer observer) {
		ISolver solver = _solvers.get(i);
		IGame game = _games.get(i);
		Marks m = _marks.get(i);
		if (observer != null) solver.obs().addObserver(observer);
		try {
			_marks.set(i, solver.solve(game, m));
		} finally {
			if (observer != null) solver.obs().deleteObserver(observer);
		}
	}
	
	public void solveAll() {
		for (int i = 0; i < _solvers.size(); i++) {
			if (_isEnable.get(i)) {
				solve(i, null);
			}
		}
		
//		//result
//		for (int i = 0; i < solvers.size(); i++) {
//			ISolver solver = solvers.get(i);
//			Game game = games.get(i);
//			Marks mark = marks.get(i);
//			
//			System.out.println("solver=" + solver.getClass().toString());
//			GamePrinter.printAnswer(game, mark);
//			solver.printSummary();
//			System.out.println("open Count=" + game.getOpenCount());
//		}
	}
	
	public void open(int i, int x, int y) {
		Marks m = _marks.get(i);
		Game g = _games.get(i);
		m.setMark(x, y, Marks.F.OPEN);
		g.open(x, y);
	}
	
	public void save(File f) {
		_g.save(f);
	}
	
	public void load(File f) {
		_g = Game.load(f);
		initSolvers();
	}
	
	public CustomGameInfo getCustomGameInfo() {
		return _customGameInfo;
	}
}
