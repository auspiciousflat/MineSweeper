package minesweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpressionHolder3 implements IExpressionHolder {
	private static Logger logger = Logger.getGlobal();

	private List<Set<IExpression>> _index = new ArrayList<>();
	private int xsize;
	private int ysize;
	//private Set<IExpression> _expressions = new HashSet<>();
	
	public ExpressionHolder3(int xsize, int ysize) {
		this.xsize = xsize;
		this.ysize = ysize;
		for (int i = 0; i < xsize * ysize; i++) {
			_index.add(new HashSet<IExpression>(4));
		}
	}
	
	private boolean contains(IExpression e) {
		List<Point> list = e.getPointList();
		assert(list.isEmpty());// assert
		Point p = list.get(0);
		Set<IExpression> exps = _index.get(p._y * xsize + p._x);
		return exps.contains(e);
	}
	
	private Set<IExpression> getDuplicatedExpressions(IExpression e) {
		Set<IExpression> exps = new HashSet<>();
		List<Point> list = e.getPointList();
		for (Point p : list) {
			exps.addAll(_index.get(p._y * xsize + p._x));
		}
		return exps;
	}
	
	private void add(IExpression e) {
		List<Point> list = e.getPointList();
		for (Point p : list) {
			_index.get(p._y * xsize + p._x).add(e);
		}
	}
	
	private void remove(IExpression e) {
		List<Point> list = e.getPointList();
		for (Point p : list) {
			_index.get(p._y * xsize + p._x).remove(e);
		}
	}
	
	private IExpression mul(IExpression a, IExpression b) {
		List<Point> c = new ArrayList<>();
		for (Point ap : a.getPointList()) {
			if (b.getPointList().contains(ap)) {
				c.add(ap);
			}
		}
		if (c.size() > 0 && c.size() < a.getMaterialNum() && c.size() < b.getMaterialNum()) {
			int m1 = a.getValue() - (a.getMaterialNum() - c.size());
			int m2 = b.getValue() - (b.getMaterialNum() - c.size());
			int min = Math.max(0, Math.max(m1, m2));
			int max = Math.min(c.size(), Math.min(a.getValue(), b.getValue()));
			if (min == max) {
				return new Expression(c, min);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public List<IExpression> addExpression(IExpression e) {
		List<IExpression> determined = new ArrayList<>();
		if (e.getPointList().size() == 0) return determined; // return empty list 
				
		Set<IExpression> newExp = new HashSet<>();
		newExp.add(e);
		while (! newExp.isEmpty()) {
			Set<IExpression> current = new HashSet<>(newExp);
			newExp.clear();
			for (IExpression ec : current) {
				if (! contains(ec)) {
					Set<IExpression> divided = new HashSet<>();
					
					//ínóãorà¿ëSåàíË
					if (ec.getMaterialNum() == 1) {
						determined.add(ec);
					} else if (ec.getMaterialNum() == ec.getValue() || ec.getValue() == 0) {
						int v = (ec.getValue() == 0) ? 0 : 1;
						for (Point p : ec.getPointList()) {
							List<Point> pl = new ArrayList<>();
							pl.add(p);
							newExp.add(new Expression(pl, v));
						}
						divided.add(ec);
					}
					
					//ï™äÑâ¬î\Ç»éÆÇåüçı
					Set<IExpression> exs = getDuplicatedExpressions(ec);
					for (IExpression ex : exs) {
						if (ex.isSubPart(ec)) {
							divided.add(ec);
							newExp.add(ec.sub(ex));
						}
					 	if (ec.isSubPart(ex)) {
					 		divided.add(ex);
							newExp.add(ex.sub(ec));
						}
					 	IExpression d;
					 	if ((d = mul(ec, ex)) != null) {
					 		newExp.add(d);
					 	}
					 	
					 	// ñµèÇí≤ç∏
					 	if (ec.isSameMaterials(ex)) {
					 		if (ec.getValue() != ex.getValue()) {
					 			return null;
					 		}
					 	}

					}
					// ñµèÇí≤ç∏
				 	if (ec.getMaterialNum() < ec.getValue() || ec.getValue() < 0) {
				 		return null;
				 	}
				 	
				 	if (logger.isLoggable(Level.FINE)) {
				 		logger.fine("add:" + ec.toString());
				 	}
					add(ec);
					for (IExpression d : divided) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("remove:" + d.toString());
						}
						remove(d);
					}
				}
			}
		}
		
		return determined;
	}

	@Override
	public List<IExpression> getDeterminedPoint() {
		List<IExpression> determined = new ArrayList<>();
		for (Set<IExpression> set : _index) {
			for (IExpression e : set) {
				if (e.getMaterialNum() == 1) determined.add(e);
			}
		}
		return determined;
	}
	
	@Override
	public ExpressionHolder3 clone() {
		ExpressionHolder3 e = new ExpressionHolder3(xsize, ysize);
		e._index = new ArrayList<>(xsize * ysize);
		for (Set<IExpression> s : _index) {
			e._index.add(new HashSet<>(s));
		}
		return e;
	}
}
