package minesweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Marks  implements Cloneable {
	private static Logger logger = Logger.getGlobal();
	
	public enum F {
		UNMARKED,
		SAFE,
		MINE,
		OPEN
	}
	
	private class AccessCount {
		public int n = 0;
	}
	
	/*
	 * 0 unmarked
	 * 1 safe
	 * 2 mine
	 */
	private F _fields[];
	private int _width;
	private int _height;
	
	private Set<Point> _safe = new HashSet<>();
	
	//cloneメソッドによって作られたインスタンスと_accessCountを共有する
	private AccessCount _accessCount = new AccessCount();
	
	public Marks(IGame game) {
		_width = game.getWidth();
		_height = game.getHeight();
		_fields = new F[_width * _height];
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				if (game.isOpened(x, y)) {
					_fields[y * _width + x] = F.OPEN;
				} else {
					_fields[y * _width + x] = F.UNMARKED;
				}
			}
		}
	}
	
	private Marks() {
	}
	
	public void setMark(int x, int y, F m) {
		logger.fine("set(" + x + "," + y+ ")"+  m.toString());
		_accessCount.n++;
		_fields[_width * y + x] = m;
		if (m == F.SAFE) {
			_safe.add(new Point(x, y));
		} else {
			_safe.remove(new Point(x, y));
		}
	}
	
	public F get(int x, int y) {
		_accessCount.n++;
		return _fields[_width * y + x];
	}

	public int getAroundMarksCount(int x, int y, F state) {
		int cnt = 0;
		for (int yy = Math.max(0, y - 1); yy <= Math.min(_height - 1, y + 1); yy++) {
			for (int xx = Math.max(0, x - 1); xx <= Math.min(_width - 1, x + 1); xx++) {
				_accessCount.n++;
				if (_fields[yy * _width + xx] == state) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	
	public List<Point> getAroundMarks(int x, int y, F state) {
		List<Point> p = new ArrayList<>();
		for (int yy = Math.max(0, y - 1); yy <= Math.min(_height - 1, y + 1); yy++) {
			for (int xx = Math.max(0, x - 1); xx <= Math.min(_width - 1, x + 1); xx++) {
				_accessCount.n++;
				if (_fields[yy * _width + xx] == state) {
					p.add(new Point(xx, yy));
				}
			}
		}
		return p;
	}

	public void setAroundMarks(int x, int y, F state) {
		for (int yy = Math.max(0, y - 1); yy <= Math.min(_height - 1, y + 1); yy++) {
			for (int xx = Math.max(0, x - 1); xx <= Math.min(_width - 1, x + 1); xx++) {
				if (_fields[yy * _width + xx] == F.UNMARKED) {
					setMark(xx, yy, state);
				}
			}
		}
	}
		
	@Override
	public Marks clone() {
		Marks m = new Marks();
		m._width = this._width;
		m._height = this._height;
		m._fields = this._fields.clone();
		m._accessCount = _accessCount;
		m._safe.addAll(this._safe);
		return m;
	}
	
	public int getAccessCount() {
		return _accessCount.n++;
	}

	public List<Point> getAllMarked(F state) {
		List<Point> p = new ArrayList<>();
		if (state == F.SAFE) {
			p.addAll(_safe);
		} else {
			for (int y = 0; y <_height; y++) {
				for (int x= 0; x <_width; x++) {
					_accessCount.n++;
					if (_fields[y * _width + x] == state) {
						p.add(new Point(x, y));
					}
				}
			}
		}
		return p;
	}
}
