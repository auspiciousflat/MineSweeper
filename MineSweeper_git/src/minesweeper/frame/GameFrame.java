package minesweeper.frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameFrame(JPanel p) {
		this.getContentPane().add(p);
		this.addWindowListener(new WindowClosingHandler());
		this.pack();
	}
	
	public class WindowClosingHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}
}
