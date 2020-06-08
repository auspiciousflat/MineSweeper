package minesweeper;

public class GamePrinter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void print(IGame g, Marks s) {
		for (int y = 0; y < g.getHeight(); y++) {
			for (int x = 0; x < g.getWidth(); x++) {
				if (g.isOpened(x, y)) {
					if (g.getPanelInfo(x, y) == -1) {
						System.out.printf("@");
					} else {
						if (g.getPanelInfo(x, y) > 0) {
							System.out.printf("%01d", g.getPanelInfo(x, y));
						} else {
							System.out.printf(".");
						}
					}
				} else {
					if (s.get(x, y) == Marks.F.MINE) {
						System.out.print("*");
					} else if (s.get(x, y) == Marks.F.SAFE) {
						System.out.print("_");
					} else {
						System.out.print("#");
					}
				}
				System.out.print(" ");
			}
			System.out.println("");
		}
	}
	
	public static void printAnswer(Game g, Marks s) {
		for (int y = 0; y < g.getHeight(); y++) {
			for (int x = 0; x < g.getWidth(); x++) {
				if (g.isOpened(x, y)) {
					if (g.getPanelInfo(x, y) == -1) {
						System.out.printf("@");
					} else {
						if (g.getPanelInfo(x, y) > 0) {
							System.out.printf("%01d", g.getPanelInfo(x, y));
						} else {
							System.out.printf(".");
						}
					}
				} else {
					if (s.get(x, y) == Marks.F.MINE) {
						if (g.isMine(x, y)) {
							System.out.print("*");
						} else {
							System.out.print("X");
						}
					} else if (s.get(x, y) == Marks.F.SAFE) {
						if (g.isMine(x, y)) {
							System.out.print("=");
						} else {
							System.out.print("_");
						}
					} else {
						if (g.isMine(x, y)) {
							System.out.print("^");
						} else {
							System.out.print("#");
						}
					}
				}
				System.out.print(" ");
			}
			System.out.println("");
		}
	}

}
