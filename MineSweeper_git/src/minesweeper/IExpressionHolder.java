package minesweeper;

import java.util.List;

public interface IExpressionHolder {
	
	/*
	 * �V�K�Ɋm�肵���_�ƒl��Ԃ�
	 */
	public List<IExpression> addExpression(IExpression e);
	
	public List<IExpression> getDeterminedPoint();

	public IExpressionHolder clone();
}
