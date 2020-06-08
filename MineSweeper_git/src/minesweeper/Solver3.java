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

public class Solver3 implements ISolver {
	
	private Observable o = new Observable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	private int _markedBysubJunc = 0;
	
	//private int _boundaryAccess = 0;
	
	/* (non-Javadoc)
	 * @see minesweeper.ISolver#solve(minesweeper.IGame)
	 */
	@Override
	public Marks solve(IGame game, Marks m) {
		initSummary();
		
		List<Point> affected = m.getAllMarked(Marks.F.OPEN);;
		Set<Point> boundary = new HashSet<>();
		for (Point p : affected) {
			boundary.addAll(m.getAroundMarks(p._x, p._y, Marks.F.UNMARKED));
		}
		
		do {
			while (affected.size() > 0) {
				if (! mark(game, m, affected)) {
					System.out.println("Illegal failed");
					_accessCount += m.getAccessCount();
					return m;
				}
				
				GamePrinter pr = new GamePrinter();
				//pr.print(game, m);
				//waitInput(); 
				
				//int ｘ = Integer.parseInt（buf）;
				
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
						List<Point> aroundOpened = m.getAroundMarks(x, y, Marks.F.OPEN);
						for (Point p : aroundOpened) {
							boundary.addAll(m.getAroundMarks(p._x, p._y, Marks.F.UNMARKED));
						}
						System.out.println("open (" + x + "," + y + ")");
					}
				}
				System.out.println("open cnt=" + opened.size());
				affected = opened;
			};
			
			int accessCount0 = m.getAccessCount();
			for (Iterator<Point> i = boundary.iterator(); i.hasNext();) {
				Point p = i.next();
				i.remove();
				if (m.get(p._x, p._y) != Marks.F.UNMARKED) {
					//i.remove();
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
					if (! isMarkable) {
						System.out.println("try success set(" + x + "," + y + ")" + "SAFE");
						m.setMark(x, y, Marks.F.SAFE);
						affected = m.getAroundMarks(x, y, Marks.F.OPEN);
						_markedBysubJunc++;
						break;
					}
					Marks mm2 = m.clone();
					System.out.println("try setsafe(" + x + "," + y + ")");
					mm2.setMark(x, y, Marks.F.SAFE);
					isMarkable = mark(game, mm2, mm2.getAroundMarks(x, y, Marks.F.OPEN));
					if (! isMarkable) {
						System.out.println("try success set(" + x + "," + y + ")" + "MINE");
						m.setMark(x, y, Marks.F.MINE);
						affected = m.getAroundMarks(x, y, Marks.F.OPEN);
						_markedBysubJunc++;
						break;
					}
					System.out.println("try failed(" + x + "," + y + ")");
					//}
				}
			}
			_accessCount2 += m.getAccessCount() - accessCount0;
		} while(boundary.size() > 0 || affected.size() > 0);
		_accessCount += m.getAccessCount();
		
		printSummary();
		
		return m;
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
	
	private void initSummary() {
		_accessCount = 0;
		_accessCount2 = 0;
		_markedBysubJunc = 0;
	}
	
	
	@Override
	public Observable obs() {
		return o;
	}
}
