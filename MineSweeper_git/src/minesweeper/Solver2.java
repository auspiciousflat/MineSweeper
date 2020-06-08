package minesweeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

public class Solver2 implements ISolver {
	
	private MyObservable o = new MyObservable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	private int _markedBysubJunc = 0;
	
	//private int _boundaryAccess = 0;
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#solve(minesweeper.IGame)
	 */
	@Override
	public Marks solve(IGame game, Marks m) {
		List<Point> affected;
		Set<Point> boundary = new HashSet<>();
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if(m.get(x, y) == Marks.F.OPEN) {
					boundary.addAll(m.getAroundMarks(x, y, Marks.F.UNMARKED));
				}
			}
		}
		affected = m.getAllMarked(Marks.F.OPEN);
		int cnt2 = 0;
		do {
			// affected = m.getAllMarked(Marks.F.OPEN);
			int cnt;
			do {
				cnt = 0;
				if (! mark(game, m, affected)) {
					System.out.println("Illegal failed");
					return m;
				}
				
				o.setChanged();
				o.notifyObservers(m);
				
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
						boundary.addAll(m.getAroundMarks(x, y, Marks.F.UNMARKED));
						System.out.println("open (" + x + "," + y + ")");
						cnt++;
						//_boundaryAccess += 8;
					}
				}
				affected = opened;
				System.out.println("open cnt=" + cnt);
			} while (cnt > 0);
			
			o.setChanged();
			o.notifyObservers(m);
			
			cnt2 = 0;
			for (Iterator<Point> i = boundary.iterator(); i.hasNext();) {
				Point p = i.next();
				if (m.get(p._x, p._y) != Marks.F.UNMARKED) {
					i.remove();
				} else {
					int x = p._x;
					int y = p._y;
					// if (m.getAroundMarksCount(x, y, Marks.F.OPEN) > 0) {
					boolean isMarkable;
					System.out.println("try(" + x + "," + y + ")");
					Marks mm = m.clone();
					System.out.println("try setmine(" + x + "," + y + ")");
					mm.setMark(x, y, Marks.F.MINE);
					isMarkable = mark(game, mm, mm.getAroundMarks(x, y, Marks.F.OPEN));
					_accessCount2+= mm.getAccessCount();
					if (! isMarkable) {
						System.out.println("try success set(" + x + "," + y + ")" + "SAFE");
						m.setMark(x, y, Marks.F.SAFE);
						affected = m.getAroundMarks(x, y, Marks.F.OPEN);
						_markedBysubJunc++;
						cnt2++;
						break;
					}
					Marks mm2 = m.clone();
					System.out.println("try setsafe(" + x + "," + y + ")");
					mm2.setMark(x, y, Marks.F.SAFE);
					isMarkable = mark(game, mm2, mm2.getAroundMarks(x, y, Marks.F.OPEN));
					_accessCount2+= mm2.getAccessCount();
					if (! isMarkable) {
						System.out.println("try success set(" + x + "," + y + ")" + "MINE");
						m.setMark(x, y, Marks.F.MINE);
						affected = m.getAroundMarks(x, y, Marks.F.OPEN);
						_markedBysubJunc++;
						cnt2++;
						break;
					}
					System.out.println("try failed(" + x + "," + y + ")");
					//}
				}
				o.setChanged();
				o.notifyObservers(m);
			}
		} while(cnt2 > 0);
		_accessCount += m.getAccessCount();
		//_accessCount += _accessCount2;
		
		//System.out.println("b=" + _boundaryAccess);
		return m;
	}

	/*
	 * affected: 調査対象のオープン済みタイル
	 */
	private boolean mark(IGame game, Marks m, Collection<Point> affected) {
		System.out.println("affected count=" + affected.size());
		while (affected.size() > 0) {
			Set<Point> newlyAffected = new HashSet<>();
			for (Point p : affected) {
				int x = p._x;
				int y = p._y;
				//if (game.isOpened(x, y)) {
				int num = game.getPanelInfo(x, y);
				List<Point> aroundUnmarkedPoint = m.getAroundMarks(x, y, Marks.F.UNMARKED);
				int aroundunmarked = aroundUnmarkedPoint.size();
				int aroundmines = m.getAroundMarksCount(x, y, Marks.F.MINE);
				//矛盾計算
				if (aroundunmarked + aroundmines < num) {
					return false;
				} else if (aroundmines > num) {
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
							newlyAffected.addAll(m.getAroundMarks(u._x, u._y, Marks.F.OPEN));
						}
					}
				}
				//}
			}
			affected = newlyAffected;
			System.out.println("newlyAffected size=" + affected.size());
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
		System.out.println("Marked by subjunks=" + getMarkedSubjuncCount());
	}
	
	@Override
	public Observable obs() {
		return o;
	}
}
