package minesweeper.frame;

public class CustomGameInfo {

	enum OpenPoint {
		LEFTUP,
		CENTER
	}
	
	private int _x;
	private int _y;
	private int _mine;
	private OpenPoint _openPoint;
	private boolean _isSafeOpen;
	
	public CustomGameInfo(int x, int y, int mine, OpenPoint openPoint, boolean isSafeOpen) {
		setParameters(x, y, mine, openPoint, isSafeOpen);
	}
	
	public void setParameters(int x, int y, int mine, OpenPoint openPoint, boolean isSafeOpen) {
		_x = x;
		_y = y;
		_mine = mine;
		_openPoint = openPoint;
		_isSafeOpen = isSafeOpen;
	}
	
	public int getX() {
		return _x;
	}
	
	public int getY() {
		return _y;
	}
	
	public int getMine() {
		return _mine;
	}
	
	public OpenPoint getOpenPoint() {
		return _openPoint;
	}
	
	public boolean isSafeOpen() {
		return _isSafeOpen;
	}
}
