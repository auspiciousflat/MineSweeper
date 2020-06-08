package minesweeper;

public interface IGame {

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract int getNum();
	
	/*
	 * mine false
	 * safe true
	 */
	public abstract boolean open(int x, int y);

	/*
	 * 0~8 OPENED
	 * -1 CLOSED
	 */
	public abstract int getPanelInfo(int x, int y);

	public abstract boolean isOpened(int x, int y);
}