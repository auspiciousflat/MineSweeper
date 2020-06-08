package minesweeper;

import java.util.Observable;

public class Solver implements ISolver {
	
	private MyObservable o = new MyObservable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	
	public Marks solve(IGame game, Marks m) {
		
		int cnt2 = 0;
		do {
			cnt2 = 0;
			int cnt;
			do {
				cnt = 0;
				if (! mark(game, m)) {
					System.out.println("Illegal failed");
					return m;
				}
				
				o.setChanged();
				o.notifyObservers(m);
				
				for (int y = 0; y < game.getHeight(); y++) {
					for (int x = 0; x < game.getWidth(); x++) {
						if (m.get(x, y) == Marks.F.SAFE) {
							if (! game.open(x, y)) {
								System.out.println("falied on (" + x + "," + y + ")");
							}
							m.setMark(x, y, Marks.F.OPEN);
							cnt++;
						}
					}
				}
			} while (cnt > 0);
			
			for (int y = 0; y < game.getHeight(); y++) {
				for (int x = 0; x < game.getWidth(); x++) {
					if (m.get(x, y) == Marks.F.UNMARKED) {
						boolean flg;
						Marks mm = m.clone();
						mm.setMark(x, y, Marks.F.MINE);
						flg = mark(game, mm);
						_accessCount2+= mm.getAccessCount();
						if (! flg) {
							m.setMark(x, y, Marks.F.SAFE);
							cnt2++;
							continue;
						}
						Marks mm2 = m.clone();
						mm2.setMark(x, y, Marks.F.SAFE);
						flg = mark(game, mm);
						_accessCount2+= mm2.getAccessCount();
						if (! flg) {
							m.setMark(x, y, Marks.F.MINE);
							cnt2++;
							continue;
						}
					}
				}
			}
		} while(cnt2 > 0);
		_accessCount += m.getAccessCount();
		_accessCount += _accessCount2;
		return m;
	}

	private boolean mark(IGame game, Marks m) {
		int cnt;
		do {
			cnt = 0;
			for (int y = 0; y < game.getHeight(); y++) {
				for (int x = 0; x < game.getWidth(); x++) {
					if (game.isOpened(x, y)) {
						int num = game.getPanelInfo(x, y);
						int aroundunmarked = m.getAroundMarksCount(x, y, Marks.F.UNMARKED);
						int aroundmines = m.getAroundMarksCount(x, y, Marks.F.MINE);
						//矛盾計算
						if (aroundunmarked + aroundmines < num) {
							return false;
						} else if (aroundmines > num) {
							return false;
						}
						//マーク
						if (aroundunmarked != 0) {
							if (num == aroundmines) {
								m.setAroundMarks(x, y, Marks.F.SAFE);
								cnt += aroundunmarked;
							} else if (aroundmines + aroundunmarked == num) {
								m.setAroundMarks(x, y, Marks.F.MINE);
								cnt += aroundunmarked;
							}
						}
					}
				}
			}
		} while (cnt > 0);
		return true;
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
	}

	@Override
	public Observable obs() {
		return o;
	}
}
