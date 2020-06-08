package minesweeper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Game implements IGame {

	private int _width;
	private int _height;
	private int _num;
	private int[] _field;
	private boolean[] _isOpened;
	private int _openNum;

	private int _accessCount = 0;

	public Game(int width, int height, int num) {
		_width = width;
		_height = height;
		_num = num;
		_field = new int[_width * _height];
		_isOpened = new boolean[_width * _height];
		_openNum = 0;

		while (num > 0) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			if (_field[y * _width + x] == 0) {
				_field[y * _width + x] = -1;
				num--;
			}
		}

		calcPanelInfo();
	}
	
	public Game(int width, int height, int num, int px, int py) {
		_width = width;
		_height = height;
		_num = num;
		_field = new int[_width * _height];
		_isOpened = new boolean[_width * _height];
		_openNum = 0;

		while (num > 0) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			if (_field[y * _width + x] == 0) {
				if ((x > px + 1 || x < px - 1) || (y > py + 1 || y < py - 1)) {
					_field[y * _width + x] = -1;
					num--;
				}
			}
		}

		calcPanelInfo();
		open(px, py);
	}

	public Game(int width, int height, int[] array) {
		_width = width;
		_height = height;
		_field = new int[_width * _height];
		_isOpened = new boolean[_width * _height];

		_num = 0;
		for (int i = 0; i < _field.length; i++) {
			if ((array[i] & 1) != 0) {
				_field[i] = -1;
				_num++;
			} else {
				_field[i] = 0;
			}
			if ((array[i] & 2) != 0) {
				_isOpened[i] = true;
			}
		}

		calcPanelInfo();
	}

	private Game() {
	}

	private void calcPanelInfo() {
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				if (_field[y * _width + x] == 0) {
					int cnt = 0;
					for (int yy = Math.max(0, y - 1); yy <= Math.min(
							_height - 1, y + 1); yy++) {
						for (int xx = Math.max(0, x - 1); xx <= Math.min(
								_width - 1, x + 1); xx++) {
							if (_field[yy * _width + xx] == -1) {
								cnt++;
							}
						}
					}
					_field[y * _width + x] = cnt;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see minesweeper.IGame#getWidth()
	 */
	@Override
	public int getWidth() {
		return _width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see minesweeper.IGame#getHeight()
	 */
	@Override
	public int getHeight() {
		return _height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see minesweeper.IGame#getNum()
	 */
	@Override
	public int getNum() {
		return _num;
	}

	/*
	 * mine false safe true
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see minesweeper.IGame#open(int, int)
	 */
	@Override
	public boolean open(int x, int y) {
		if (!isOpened(x, y)) {
			_isOpened[y * _width + x] = true;
			_openNum++;
		}
		return (_field[y * _width + x] != -1);
	}

	/*
	 * 0~8 OPENED -1 CLOSED
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see minesweeper.IGame#getPanelInfo(int, int)
	 */
	@Override
	public int getPanelInfo(int x, int y) {
		_accessCount++;
		if (_isOpened[y * _width + x]) {
			return _field[y * _width + x];
		} else {
			return -1;
		}
	}

	@Override
	public boolean isOpened(int x, int y) {
		_accessCount++;
		return _isOpened[y * _width + x];
	}

	public int getAccessCount() {
		return _accessCount;
	}

	public int getOpenCount() {
		return _openNum;
	}

	public boolean isMine(int x, int y) {
		return _field[y * _width + x] == -1;
	}

	@Override
	public Game clone() {
		Game g = new Game();
		g._width = _width;
		g._height = _height;
		g._num = _num;
		g._field = _field.clone();
		g._isOpened = _isOpened.clone();
		g._accessCount = _accessCount;
		g._openNum = _openNum;
		return g;
	}

	public void save(File f) {
		try (FileWriter fw = new FileWriter(f);
				BufferedWriter out = new BufferedWriter(fw);) {
			out.write("" + _width + " " + _height + "\n");
			for (int y = 0; y < _height; y++) {
				for (int x = 0; x < _width; x++) {
					if (x > 0)
						out.write(" ");
					int c = 0;
					if (_field[y * _width + x] == -1)
						c |= 1;
					if (this._isOpened[y * _width + x])
						c |= 2;
					out.write("" + c);
				}
				out.write("\n");
			}
			out.write("\n");
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Game load(File f) {
		Game g;
		try (FileReader fr = new FileReader(f);
				BufferedReader in = new BufferedReader(fr);) {
			// FileReader fr = new FileReader(f);

			String str = in.readLine();
			String[] strs = str.split(" ");

			int width = Integer.parseInt(strs[0]);
			int height = Integer.parseInt(strs[1]);
			int[] field = new int[width * height];
			for (int y = 0; y < height; y++) {
				String line = in.readLine();
				strs = line.split(" ");
				for (int x = 0; x < width; x++) {
					field[y * width + x] = Integer.parseInt(strs[x]);
				}
			}
			in.close();
			g = new Game(width, height, field);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return g;
	}
}
