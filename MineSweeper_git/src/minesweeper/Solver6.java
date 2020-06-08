package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

public class Solver6 implements ISolver {
	private static Logger logger = Logger.getGlobal();
	
	private MyObservable o = new MyObservable();
	
	private int _accessCount = 0;
	private int _accessCount2 = 0;
	private int _subJunks = 0;
	
	public Marks solve(IGame game, Marks m) {
		initSummary();
		
		IExpressionHolder holder = new ExpressionHolder(game.getWidth(), game.getHeight());
		List<Point> newOpened = m.getAllMarked(Marks.F.OPEN);
		while(! newOpened.isEmpty()) {
			List<Point> opened = new ArrayList<>(newOpened);
			newOpened.clear();
			for (Point p : opened) {
				logger.fine("opened={" + p._x + "," + p._y + "}");
				
				// ���̐���
				List<Point> unmarked = m.getAroundMarks(p._x, p._y, Marks.F.UNMARKED);
				int mines = m.getAroundMarksCount(p._x, p._y, Marks.F.MINE);
				IExpression e = new Expression(unmarked, game.getPanelInfo(p._x, p._y) - mines);
				
				// ���̒ǉ��ƌ���p�l���擾
				List<IExpression> determined = holder.addExpression(e);
				
				// ���s�͂��Ȃ��͂��H
				if (determined == null) {
					logger.fine("Illegal failed");
					return m;
				}
				
				// ���S�p�l���}�[�N
				for (IExpression d : determined) {
					Point dp = d.getPointList().get(0);
					if (d.getValue() == 0) {
						m.setMark(dp._x, dp._y, Marks.F.SAFE);
						newOpened.add(dp);
					} else {
						m.setMark(dp._x, dp._y, Marks.F.MINE);
					}
				}
			}
			//GamePrinter.print(game, m);
			logger.fine("subjunc");
			// ����@
			if (newOpened.isEmpty()) {
				// �J����p�l�����Ȃ��Ƃ��������s
				LOOP:
				for (int y = 0; y < game.getHeight(); y++) {
					for (int x = 0; x < game.getWidth(); x++) {
						IExpression ee = null;
						if (m.get(x, y) == Marks.F.UNMARKED) {
							if (m.getAroundMarksCount(x, y, Marks.F.OPEN) != 0) {
								// ���͂ɃI�[�v���p�l����������̂ɂ��Ă̂ݎ��s
								logger.fine("Test(" + x + "," + y + ")");
								List<Point> lp = new ArrayList<>();
								lp.add(new Point(x, y));
								
								IExpressionHolder holder21 = holder.clone();
								IExpression e1  = new Expression(lp, 0);
								if (null == holder21.addExpression(e1)) {
									m.setMark(x, y, Marks.F.MINE);
									ee = new Expression(lp, 1);
									_subJunks++;
								} else {							
									IExpressionHolder holder22 = holder.clone();
									IExpression e2 = new Expression(lp, 1);
									if (null == holder22.addExpression(e2)) {
										m.setMark(x, y, Marks.F.SAFE);
										ee = new Expression(lp, 0);
										_subJunks++;
										newOpened.add(new Point(x, y));
									}
								}
								
								// ���S�p�l���}�[�N
								if (ee != null) {
									o.setChanged();
									o.notifyObservers(m);
									
									// ���̒ǉ��ƌ���p�l���擾
									List<IExpression> determined = holder.addExpression(ee);
									
									// ���s�͂��Ȃ��͂��H
									if (determined == null) {
										logger.fine("Illegal failed");
										return m;
									}
									
									// ���S�p�l���}�[�N
									for (IExpression d : determined) {
										Point dp = d.getPointList().get(0);
										if (d.getValue() == 0) {
											m.setMark(dp._x, dp._y, Marks.F.SAFE);
											newOpened.add(dp);
										} else {
											m.setMark(dp._x, dp._y, Marks.F.MINE);
										}
									}
								}
								if (! newOpened.isEmpty()) {
									break LOOP;
								}
							} //if (m.getAroundMarksCount(x, y, Marks.F.OPEN) != 0) {
						} //if (m.get(x, y) == Marks.F.UNMARKED)
					} // for (int x = 0; x < game.getWidth(); x++)
				} // for (int y = 0; y < game.getHeight(); y++) {
			} // if (newOpened.size() == 0)
			
			o.setChanged();
			o.notifyObservers(m);
			
			// �I�[�v��
			for (Point op : newOpened) {
				game.open(op._x, op._y);
				m.setMark(op._x, op._y, Marks.F.OPEN);
			}
		}

		_accessCount += m.getAccessCount();
		_accessCount += _accessCount2;
		
		o.setChanged();
		o.notifyObservers(m);
		
		printSummary();
		
		return m;
	}

	
	public int getAccessCount() {
		return _accessCount;
	}
	
	public int getAccessCount2() {
		return _accessCount2;
	}
	
	@Override
	public void printSummary() {
		logger.info("Access" + " marks=" + getAccessCount() + " marks2=" + getAccessCount2());
		logger.info("Marked by subjunks=" + _subJunks);
	}
	
	private void initSummary() {
		_accessCount = 0;
		_accessCount2 = 0;
		_subJunks = 0;
	}
	
	@Override
	public Observable obs() {
		return o;
	}
}
