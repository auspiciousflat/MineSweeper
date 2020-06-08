package minesweeper.frame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import minesweeper.Game;
import minesweeper.Marks;
import minesweeper.Point;

public class GamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int SCREEN_W = 800;
			// PropParser.getInt("shooting.SCREEN_W");
	private static final int SCREEN_H = 600;
			//PropParser.getInt("shooting.SCREEN_H");;

	private FieldPanel _fieldPanel = new FieldPanel();
	
	private GridBagLayout _laoyout = new GridBagLayout();
	private JPanel _buttonPanel = new JPanel();
	private JComboBox<String> _solver;
	private JComboBox<String> _isEnable;

	private CustomGameDialog _customGameDialog;
	
	private GameModel _model;
	private List<Thread> _threads = new ArrayList<>();
	
	private IGameDrawer drawer = new GameDrawer2();
	
	// FrameRateCalculator c = new FrameRateCalculator();

	public GamePanel(GameModel m) {
		_model = m;
		this.setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
		this.setSize(new Dimension(SCREEN_W, SCREEN_H));
		this.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(_fieldPanel);
		scrollPane.setSize(SCREEN_W, SCREEN_H * 3 / 4);
		scrollPane.getViewport().setLayout(null);
		this.add(scrollPane);
		
		_fieldPanel.addMouseListener(new MouseInputAdapter(){
			public void mousePressed(MouseEvent e) {
				e.getPoint().getX();
				Point p = drawer.getCoordinate(e.getPoint().getX(), e.getPoint().getY());
				int i = _solver.getSelectedIndex();
				Game g = _model.getGame(i);
				Marks mark = _model.getMark(i);
				if (0 <= p._x && p._x < g.getWidth() && 0 <= p._y && p._y < g.getHeight()) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						_model.open(i, p._x, p._y);
					} else {
						Marks.F f = mark.get(p._x, p._y);
						if (f == Marks.F.MINE) {
							mark.setMark(p._x, p._y, Marks.F.UNMARKED);
						} else if (f == Marks.F.UNMARKED) {
							mark.setMark(p._x, p._y, Marks.F.MINE);
						}
					}
					_fieldPanel.update();
					repaint();
				}
			}
		});
		
		_buttonPanel.setLocation(0, SCREEN_H * 3 / 4);
		_buttonPanel.setSize(SCREEN_W, SCREEN_H / 4);
		_buttonPanel.setLayout(_laoyout);
		this.add(_buttonPanel);
		
		_solver = new JComboBox<>();
		addComponentToPanel(_solver, 0, 0, 3, 1);
		for (int i = 0; i < _model.getCount(); i++) {
			_solver.addItem(_model.getSolver(i).getClass().toString());
			_threads.add(new MyThread(i));
		}
		_solver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_isEnable.setSelectedIndex(_model.isEnable(_solver.getSelectedIndex()) ? 0 : 1);
				repaint();
			}});
		
		_isEnable = new JComboBox<>();
		addComponentToPanel(_isEnable, 3, 0);
		_isEnable.addItem("Enable");
		_isEnable.addItem("Disable");
		_isEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_model.setIsEnable(_solver.getSelectedIndex(), (_isEnable.getSelectedIndex() == 0));
				_fieldPanel.update();
				repaint();
			}});
		
		_customGameDialog = new CustomGameDialog(_model.getCustomGameInfo());
		
		JButton resetAll = new JButton("ResetAll");
		addComponentToPanel(resetAll, 0, 1);
		resetAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Thread t : _threads) t.interrupt();
				_model.resetAll();
				_fieldPanel.update();
				repaint();
			}});
		
		JButton solveAll = new JButton("solveAll");
		addComponentToPanel(solveAll, 1, 1);
		solveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Thread t : _threads) t.interrupt();
				_model.resetAll();
				_model.solveAll();
				_fieldPanel.update();
				repaint();
			}});
		
		JButton reset = new JButton("reset");
		addComponentToPanel(reset, 2, 1);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyThread t = (MyThread)_threads.get(_solver.getSelectedIndex());
				t.interrupt();
				_model.reset(_solver.getSelectedIndex());
				_fieldPanel.update();
				repaint();
			}});
		
		JButton solve = new JButton("solve");
		addComponentToPanel(solve, 3, 1);
		solve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = _solver.getSelectedIndex();
				MyThread t = (MyThread)_threads.get(i);
				synchronized (t) {
					if (t.isAlive()) {
						t.notifyAll();
					} else {
						t = new MyThread(i);
						t.setWaiting(false);
						_threads.set(i, t);
						System.out.println("thread start");
						_threads.get(i).start();
					}
				}
			}});
		
		JButton step = new JButton("step");
		addComponentToPanel(step, 4, 1);
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = _solver.getSelectedIndex();
				MyThread t = (MyThread)_threads.get(i);
				synchronized (t) {
					if (t.isAlive()) {
						t.notifyAll();
						System.out.println("notify " + t.toString());
					} else {
						t = new MyThread(i);
						t.setWaiting(true);
						_threads.set(i, t);
						System.out.println("thread start "  + t.toString());
						_threads.get(i).start();
					}
				}
			}});
		
		JButton createGame = new JButton("Create Game");
		addComponentToPanel(createGame, 0, 2);
		createGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Thread t : _threads) t.interrupt();
				_model.createGame();
				_fieldPanel.resize();
				_fieldPanel.update();
				repaint();
			}});
		
		JButton customGame = new JButton("Custom Game");
		addComponentToPanel(customGame, 1, 2);
		customGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_customGameDialog.setVisible(true);
				repaint();
			}});
		
		JButton loadGame = new JButton("Load Game");
		addComponentToPanel(loadGame, 2, 2);
		loadGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser();
				filechooser.setCurrentDirectory(new File("./Resource"));
				int selected = filechooser.showOpenDialog(GamePanel.this);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File f = filechooser.getSelectedFile();
					_model.load(f);
					_fieldPanel.resize();
					_fieldPanel.update();
				}
				repaint();
			}});
		
		JButton saveGame = new JButton("Save Game");
		addComponentToPanel(saveGame, 3, 2);
		saveGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yy'-'MM'-'dd'_'hh'-'mm'-'ss'.txt'");
				Date date = new Date();
				JFileChooser filechooser = new JFileChooser(new File("./Resource"));
				filechooser.setSelectedFile(new File(sdf1.format(date)));
				int selected = filechooser.showSaveDialog(GamePanel.this);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File f = filechooser.getSelectedFile();
					_model.save(f);
				}
				repaint();
			}});
		
		final JCheckBox dispMine = new JCheckBox("DisplayMine");
		addComponentToPanel(dispMine, 5, 1);
		dispMine.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				drawer.setDispMine(dispMine.isSelected());
				_fieldPanel.update();
				repaint();
			}
		});
		drawer.setDispMine(dispMine.isSelected());
		
		_fieldPanel.resize();
		_fieldPanel.update();
	}
	
	private void addComponentToPanel(JComponent p, int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		_laoyout.setConstraints(p, gbc);
		_buttonPanel.add(p);
	}
	
	private void addComponentToPanel(JComponent p, int x, int y) {
		addComponentToPanel(p, x, y, 1, 1);
	}

//	@Override
//	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		int i = _solver.getSelectedIndex();
//		GameDrawer.printAnswer((Graphics2D) g, _model.getGame(i), _model.getMark(i));
//	}
	
	private class MyThread extends Thread implements Observer {
		private int _i;
		private boolean _waiting = false;
		public MyThread(int i) {
			_i = i;
		}
		
		public void setWaiting(boolean flg) {
			_waiting = flg;
		}
		
		@Override
		public void run() {
			try {
				_model.solve(_i, this);
			} catch (ThreadFinishException e) {
				System.out.println("catch thread finish exception");
			}
			repaint();
			System.out.println("thread finished");
		}
		
		@Override
		public synchronized void update(Observable o, Object arg) {
			try {
				//SwingUtilities.invokeLater(new Runnable() {
				//	public void run() {
						_fieldPanel.update();
				//	}
				//});
				repaint();
				System.out.println("wait start" + this.toString());
				if (_waiting) {
					wait();
				}
				System.out.println("wait finish" + this.toString());
			} catch (InterruptedException e) {
				//e.printStackTrace();
				throw new ThreadFinishException();
			}
		}
	}
	
	private class ThreadFinishException extends RuntimeException {
		private static final long serialVersionUID = -4635852839388276673L;
	}
	
	@SuppressWarnings("serial")
	private class FieldPanel extends JLabel {
		BufferedImage img[] = new BufferedImage[2];
		int currentBuf = 0;
		ImageIcon icon = new ImageIcon();
		
		public FieldPanel() {
			this.setSize(SCREEN_W, SCREEN_H * 3 / 4);
			this.setPreferredSize(this.getSize());
			this.setIcon(icon);
		}
		
		public void resize() {
			int i = _solver.getSelectedIndex();
			this.setSize(GameDrawer.getDrawingSize(_model.getGame(i), _model.getMark(i)));
			this.setPreferredSize(this.getSize());
			img[0] = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
			img[1] = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
			icon.setImage(img[currentBuf]);
		}
		
		public void update() {
			Graphics2D g = img[1 - currentBuf].createGraphics();
			g.clearRect(0, 0, this.getSize().width, this.getSize().height);
			int i = _solver.getSelectedIndex();
			drawer.printAnswer(g, _model.getGame(i), _model.getMark(i));
			currentBuf = 1 - currentBuf;
			if (! SwingUtilities.isEventDispatchThread()) {
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run() {
							icon.setImage(img[currentBuf]);
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				icon.setImage(img[currentBuf]);
			}
		}
		
//		@Override
//		protected void paintComponent(Graphics g) {
//			super.paintComponent(g);
//			int i = _solver.getSelectedIndex();
//			GameDrawer.printAnswer((Graphics2D) g, _model.getGame(i), _model.getMark(i));
//		}
	}
}
