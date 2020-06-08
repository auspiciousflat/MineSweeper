package minesweeper;

import java.util.Observable;
import java.util.Observer;

public interface ISolver {
	//
	public Observable obs();

	public abstract Marks solve(IGame game, Marks m);
	
	public void printSummary();
}