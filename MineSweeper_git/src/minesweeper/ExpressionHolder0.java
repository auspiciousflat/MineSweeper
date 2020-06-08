package minesweeper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ExpressionHolder0 implements IExpressionHolder {
	private static Logger logger = Logger.getGlobal();
	
	private Set<IExpression> _expressions = new HashSet<>();
	
	public ExpressionHolder0() {
		
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
				if (! _expressions.contains(ec)) {
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
					for (IExpression ex : _expressions) {
						if (ex.isSubPart(ec)) {
							divided.add(ec);
							newExp.add(ec.sub(ex));
						}
					 	if (ec.isSubPart(ex)) {
					 		divided.add(ex);
							newExp.add(ex.sub(ec));
						}
					 	// ñµèÇí≤ç∏
					 	if (ec.isSameMaterials(ex)) {
					 		if (ec.getValue() != ex.getValue()) {
					 			return null;
					 		}
					 	}
					 	if (ec.getMaterialNum() < ec.getValue() || ec.getValue() < 0) {
					 		return null;
					 	}
					}

					logger.fine("add:" + ec.toString());
					_expressions.add(ec);
					for (IExpression d : divided) {
						logger.fine("remove:" + d.toString());
					}
					_expressions.removeAll(divided);
				}
			}
		}
		
		return determined;
	}

	@Override
	public List<IExpression> getDeterminedPoint() {
		List<IExpression> determined = new ArrayList<>();
		for (IExpression e : _expressions) {
			if (e.getMaterialNum() == 1) determined.add(e);
		}
		return determined;
	}
	
	@Override
	public ExpressionHolder0 clone() {
		ExpressionHolder0 e = new ExpressionHolder0();
		e._expressions.addAll(_expressions);
		return e;
	}
}
