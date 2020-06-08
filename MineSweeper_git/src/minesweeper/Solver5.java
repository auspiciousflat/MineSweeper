package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Solver5 implements ISolver {
	
	private MyObservable o = new MyObservable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	
	public Marks solve(IGame game, Marks m) {
		IExpressionHolder holder = new ExpressionHolder(game.getWidth(), game.getHeight());
		List<Point> newOpened = m.getAllMarked(Marks.F.OPEN);
		while(! newOpened.isEmpty()) {
			List<Point> opened = new ArrayList<>(newOpened);
			newOpened.clear();
			for (Point p : opened) {
				System.out.println("opened={" + p._x + "," + p._y + "}");
				
				// 式の生成
				List<Point> unmarked = m.getAroundMarks(p._x, p._y, Marks.F.UNMARKED);
				int mines = m.getAroundMarksCount(p._x, p._y, Marks.F.MINE);
				IExpression e = new Expression(unmarked, game.getPanelInfo(p._x, p._y) - mines);
				
				// 式の追加と決定パネル取得
				List<IExpression> determined = holder.addExpression(e);
				
				// 安全パネルマーク
				for (IExpression d : determined) {
					Point dp = d.getPointList().get(0);
					if (d.getValue() == 0) {
						m.setMark(dp._x, dp._y, Marks.F.SAFE);
						newOpened.add(dp);
					} else {
						m.setMark(dp._x, dp._y, Marks.F.MINE);
					}
				}
			}
			
			o.setChanged();
			o.notifyObservers(m);
			GamePrinter.print(game, m);
			System.out.println("");
			
			// オープン
			for (Point op : newOpened) {
				game.open(op._x, op._y);
			}
		}

		_accessCount += m.getAccessCount();
		_accessCount += _accessCount2;
		return m;
	}

	
	public int getAccessCount() {
		return _accessCount;
	}
	
	public int getAccessCount2() {
		return _accessCount2;
	}
	
	@Override
	public void printSummary() {
		System.out.println("Access" + " marks=" + getAccessCount() + " marks2=" + getAccessCount2());
		//System.out.println("Marked by subjunks=" + getMarkedSubjuncCount());
	}
	
	@Override
	public Observable obs() {
		return o;
	}
}
