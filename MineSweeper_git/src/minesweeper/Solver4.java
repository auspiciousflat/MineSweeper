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

public class Solver4 implements ISolver {
	
	private MyObservable o = new MyObservable();
	
	private static final int MAX_DEPTH = 3;
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	private int _markedBysubJunc = 0;
	private int _depth = 0;
	
	//private int _boundaryAccess = 0;
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#solve(minesweeper.IGame)
	 */
	@Override
	public Marks solve(IGame game, Marks m) {
		List<Point> affected = m.getAllMarked(Marks.F.OPEN);
		
		do {
			// affected = m.getAllMarked(Marks.F.OPEN);
			mark(game, m, affected);
			
			if (! mark(game, m, affected)) {
				System.out.println("Illegal failed");
				_accessCount += m.getAccessCount();
				return m;
			}
			
			o.setChanged();
			o.notifyObservers(m);
			GamePrinter.print(game, m);
			//waitInput(); 
			
			affected = open(game, m);
		} while(affected.size() > 0);
		_accessCount += m.getAccessCount();
		
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
	private boolean mark(IGame game, Marks m, Collection<Point> targetOpenPanelList) {
		_depth++;
		System.out.println("depth=" + _depth);
		System.out.println("targetOpenPanelList count=" + targetOpenPanelList.size());
		Set<Point> unmarkedPointList = new HashSet<>();
		while (targetOpenPanelList.size() > 0) {
			while (targetOpenPanelList.size() > 0) {
				Set<Point> newlyAffected = new HashSet<>();
				for (Point p : targetOpenPanelList) {
					int x = p._x;
					int y = p._y;
					//if (game.isOpened(x, y)) {
					int num = game.getPanelInfo(x, y);
					List<Point> aroundUnmarkedPoint = m.getAroundMarks(x, y, Marks.F.UNMARKED);
					int aroundunmarked = aroundUnmarkedPoint.size();
					int aroundmines = m.getAroundMarksCount(x, y, Marks.F.MINE);
					//矛盾計算
					if (aroundunmarked + aroundmines < num) {
						_depth--;
						return false;
					} else if (aroundmines > num) {
						_depth--;
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
							for (Point u : aroundUnmarkedPoint) {
								m.setMark(u._x, u._y, mark);
								unmarkedPointList.remove(new Point(u._x, u._y));
								newlyAffected.addAll(m.getAroundMarks(u._x, u._y, Marks.F.OPEN));
							}
						} else {
							for (Point u : aroundUnmarkedPoint) {
								unmarkedPointList.add(new Point(u._x, u._y));
							}
						}
					}
				}
				targetOpenPanelList = newlyAffected;
				System.out.println("newlyAffected size=" + targetOpenPanelList.size());
			}
			//未決
			
			// 再帰呼び出し
			Marks.F mark = null;
			int x = -1;
			int y = -1;
			System.out.println("unmarkedPointList size=" + unmarkedPointList.size());
			if (_depth < MAX_DEPTH) {
				for (Iterator<Point> i = unmarkedPointList.iterator(); i.hasNext();) {
					Point p = i.next();
					i.remove();
					if (m.get(p._x, p._y) != Marks.F.UNMARKED) {
						//i.remove();
					} else {
						x = p._x;
						y = p._y;
						boolean isMarkable;
						mark = null;
						System.out.println("try(" + x + "," + y + ")");
						Marks mm = m.clone();
						System.out.println("try setmine(" + x + "," + y + ")");
						mm.setMark(x, y, Marks.F.MINE);
						isMarkable = mark(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN));
						_accessCount2+= mm.getAccessCount();
						if (! isMarkable) {
							mark = Marks.F.SAFE;
							break;
						}
						Marks mm2 = m.clone();
						System.out.println("try setsafe(" + x + "," + y + ")");
						mm2.setMark(x, y, Marks.F.SAFE);
						isMarkable = mark(game, mm2, mm2.getAroundMarks(x, y, Marks.F.OPEN));
						_accessCount2+= mm2.getAccessCount();
						if (! isMarkable) {
							mark = Marks.F.MINE;
							break;
						}
						System.out.println("try failed(" + x + "," + y + ")");
					}
				}
				if (mark != null) {
					System.out.println("try success set(" + x + "," + y + ")" + mark.toString());
					m.setMark(x, y, mark);
					targetOpenPanelList = m.getAroundMarks(x, y, Marks.F.OPEN);
					_markedBysubJunc++;
				}
			}
		}
		
		_depth--;
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
		System.out.println("Marked by subjunks=" + getMarkedSubjuncCount());
	}
	
	@Override
	public Observable obs() {
		return o;
	}
}
