package minesweeper;

import java.util.List;

public interface IExpressionHolder {
	
	/*
	 * 新規に確定した点と値を返す
	 */
	public List<IExpression> addExpression(IExpression e);
	
	public List<IExpression> getDeterminedPoint();

	public IExpressionHolder clone();
}
