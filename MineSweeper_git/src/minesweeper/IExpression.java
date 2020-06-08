package minesweeper;

import java.util.List;

public interface IExpression {
	public List<Point> getPointList();
	public int getValue();
	public int getMaterialNum();
	
	public IExpression sub(IExpression e);
	public boolean isSubPart(IExpression e);
	public boolean isSameMaterials(IExpression ex);
}
