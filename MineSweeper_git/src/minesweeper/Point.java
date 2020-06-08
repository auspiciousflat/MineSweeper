package minesweeper;

public class Point {
	public int _x;
	public int _y;
	
	public Point(int x, int y) {
		_x = x;
		_y = y;
	}
	
	@Override
	public boolean equals(Object p) {
		Point pp = (Point) p;
		return (pp._x == _x && pp._y == _y);
	}
	
	@Override
    public int hashCode() {
        return _y * 65536 + _x;
    }
}
