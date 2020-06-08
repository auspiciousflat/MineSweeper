package minesweeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

public class Solver41 implements ISolver {
	
	private MyObservable o = new MyObservable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	private int _markedBysubJunc = 0;
	private int _maxDepth;
	private int _depth = 0;
	
	//private int _boundaryAccess = 0;
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#solve(minesweeper.IGame)
	 */
	@Override
	public Marks solve(IGame game, Marks m) {
		initSummary();
		
		List<Point> affected = m.getAllMarked(Marks.F.OPEN);
		Set<Point> unmarkedPoints = new HashSet<>();
		
		while(affected.size() > 0) {
			while(affected.size() > 0) {
				if (! markUnique(game, m, affected, unmarkedPoints)) {
					System.out.println("Illegal failed");
					_accessCount += m.getAccessCount();
					printSummary();
					return m;
				}
				
				o.setChanged();
				o.notifyObservers(m);
				GamePrinter.print(game, m);
				//waitInput(); 
				
				affected = open(game, m);
			};
			
			int accessCount0 = m.getAccessCount();
			//仮定法再帰
			for (Iterator<Point> i = unmarkedPoints.iterator(); i.hasNext();) {
				Point p = i.next();
				int x = p._x;
				int y = p._y;
				if (m.get(x, y) != Marks.F.UNMARKED) {
					i.remove();
				} else {
					System.out.println("try(" + x + "," + y + ")");
					Marks mm; 
					mm = m.clone();
					mm.setMark(x, y, Marks.F.MINE);
					if (! isAllocatable(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN))) {
						m.setMark(x, y, Marks.F.SAFE);
						affected.addAll(mm.getAroundMarks(x, y, Marks.F.OPEN));
						System.out.println("try success set(" + x + "," + y + ")" + Marks.F.SAFE);
						_markedBysubJunc++;
						break;
					} else {
						mm = m.clone();
						mm.setMark(x, y, Marks.F.SAFE);
						if(! isAllocatable(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN))) {
							m.setMark(x, y, Marks.F.MINE);
							affected.addAll(mm.getAroundMarks(x, y, Marks.F.OPEN));
							System.out.println("try success set(" + x + "," + y + ")" + Marks.F.MINE);
							_markedBysubJunc++;
							break;
						}
					}
				}
			}
			_accessCount2 += m.getAccessCount() - accessCount0;
			
			o.setChanged();
			o.notifyObservers(m);
		}

		_accessCount += m.getAccessCount();
		printSummary();
		return m;
	}

	private List<Point> open(IGame game, Marks m) {
		List<Point> opened = new ArrayList<>();
		List<Point> safe = m.getAllMarked(Marks.F.SAFE);
		for (Point s : safe) {
			int x = s._x;
			int y = s._y;
			if (m.get(x, y) == Marks.F.SAFE) {
				if (! game.open(x, y)) {
					System.out.println("falied on (" + x + "," + y + ")");
				}
				m.setMark(x, y, Marks.F.OPEN);
				opened.add(new Point(x, y));
				System.out.println("open (" + x + "," + y + ")");
			}
		}
		System.out.println("open cnt=" + opened.size());
		return opened;
	}

	private void waitInput() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			String buf = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * affected: 調査対象のオープン済みタイル
	 */
	private boolean isAllocatable(IGame game, Marks m, Collection<Point> affectedPoints) {
		_depth++;
		if (_depth > _maxDepth) _maxDepth = _depth;
		System.out.println("depth=" + _depth);
		System.out.println("targetOpenPanelList count=" + affectedPoints.size());
		
		Set<Point> unmarkedPoints = new HashSet<>();
		
		boolean flg = markUnique(game, m, affectedPoints, unmarkedPoints);
		if (! flg) {
			//矛盾あり
			_depth--;
			return false;
		}
		
		// 再帰呼び出し
		int x = -1;
		int y = -1;
		Marks ma = m;
		System.out.println("unmarkedPointList size=" + unmarkedPoints.size());
		for (Point p : unmarkedPoints) {
			if (ma.get(p._x, p._y) == Marks.F.UNMARKED) {
				x = p._x;
				y = p._y;
				System.out.println("try(" + x + "," + y + ")");
				Marks mm; 
				mm = ma.clone();
				mm.setMark(x, y, Marks.F.MINE);
				if (isAllocatable(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN))) {
					ma = mm;
					System.out.println("try success set(" + x + "," + y + ")" + Marks.F.MINE);
				} else {
					mm = ma.clone();
					mm.setMark(x, y, Marks.F.SAFE);
					if(isAllocatable(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN))) {
						ma = mm;
						System.out.println("try success set(" + x + "," + y + ")" + Marks.F.SAFE);
					} else {
						System.out.println("try failed(" + x + "," + y + ")");
						//矛盾あり
						_depth--;
						return false;
					}
				}
			}
		}
		
		_depth--;
		return true;
	}

	private boolean markUnique(IGame game, Marks m,
			Collection<Point> affectedPoints, Set<Point> unmarkedPoints) {
		while (affectedPoints.size() > 0) {
			Set<Point> newlyAffected = new HashSet<>();
			for (Point p : affectedPoints) {
				int x = p._x;
				int y = p._y;
				int num = game.getPanelInfo(x, y);
				List<Point> aroundUnmarkedPoints = m.getAroundMarks(x, y, Marks.F.UNMARKED);
				int aroundunmarked = aroundUnmarkedPoints.size();
				int aroundmines = m.getAroundMarksCount(x, y, Marks.F.MINE);
				
				//矛盾計算
				if (aroundunmarked + aroundmines < num || aroundmines > num) {
					return false;
				}
				
				//マーク
				if (aroundunmarked != 0) {
					Marks.F mark = null;
					if (num == aroundmines) {
						mark = Marks.F.SAFE;
					} else if (aroundmines + aroundunmarked == num) {
						mark = Marks.F.MINE;
					}
					if (mark != null) {
						for (Point u : aroundUnmarkedPoints) {
							m.setMark(u._x, u._y, mark);
							unmarkedPoints.remove(new Point(u._x, u._y));
							newlyAffected.addAll(m.getAroundMarks(u._x, u._y, Marks.F.OPEN));
						}
					} else {
						for (Point u : aroundUnmarkedPoints) {
							unmarkedPoints.add(new Point(u._x, u._y));
						}
					}
				}
			}
			affectedPoints = newlyAffected;
			System.out.println("newlyAffected size=" + affectedPoints.size());
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#getAccessCount()
	 */
	public int getAccessCount() {
		return _accessCount;
	}
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#getAccessCount2()
	 */
	public int getAccessCount2() {
		return _accessCount2;
	}

	public int getMarkedSubjuncCount() {
		return _markedBysubJunc;
	}
	
	@Override
	public void printSummary() {
		System.out.println("Access" + " marks=" + getAccessCount() + " marks2=" + getAccessCount2());
		System.out.println("MaxDepth=" + _maxDepth);
		System.out.println("Marked by subjunks=" + getMarkedSubjuncCount());
	}
	
	private void initSummary() {
		_accessCount = 0;
		_accessCount2 = 0;
		_markedBysubJunc = 0;
		_maxDepth = 0;
	}
	
	@Override
	public Observable obs() {
		return o;
	}
}
