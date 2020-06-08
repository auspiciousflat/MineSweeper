package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Expression implements IExpression {

	private List<Point> _p = new ArrayList<>();
	private int _value;
	
	public Expression(List<Point> p, int value) {
		_value = value;
		_p = p;
		
//		System.out.print("{");
//		for (Point ps : p) {
//			System.out.print("{" + ps._x + "," + ps._y + "},");
//		}
//		System.out.print("}");
//		System.out.println("="+ value);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		for (Point ps : _p) {
			b.append("{" + ps._x + "," + ps._y + "},");
		}
		b.append("}");
		b.append("="+ _value);
		return b.toString();
	}
	
	@Override
	public List<Point> getPointList() {
		return _p;
	}

	@Override
	public int getValue() {
		return _value;
	}

	@Override
	public int getMaterialNum() {
		return _p.size();
	}

	@Override
	public IExpression sub(IExpression e) {
		List<Point> p = e.getPointList();
		if (_p.containsAll(p)) {
			List<Point> p2 = new ArrayList<>(_p);
			p2.removeAll(p);
			return new Expression(p2, _value - e.getValue());
		} else {
			return null;
		}
	}

	@Override
	public boolean isSubPart(IExpression e) {
		return e.getPointList().containsAll(_p) && _p.size() < e.getPointList().size();
	}
	
	@Override
	public boolean equals(Object e) {
		IExpression ex = (IExpression) e; 
		return isSameMaterials(ex) && _value == ex.getValue();
	}
	
	@Override
	public int hashCode() {
		return _p.get(0).hashCode();
	}

	@Override
	public boolean isSameMaterials(IExpression ex) {
		List<Point> p = ex.getPointList();
		return p.containsAll(_p) && _p.containsAll(p);
	}
}
