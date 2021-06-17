import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MineSweepPart extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_HEIGHT = 700;
	private static final int WINDOW_WIDTH = 700;
	private static final int MINE_GRID_ROWS = 16;
	private static final int MINE_GRID_COLS = 16;
	private static final int TOTAL_MINES = 16;
	private static final int NO_MINES_IN_PERIMETER_GRID_VALUE = 0;
	private static final int ALL_MINES_IN_PERIMETER_GRID_VALUE = 8;
	private static final int IS_A_MINE_IN_GRID_VALUE = 9;

	private static int guessedMinesLeft = TOTAL_MINES;
	private static int actualMinesLeft = TOTAL_MINES;

	private static final String UNEXPOSED_FLAGGED_MINE_SYMBOL = "@";
	private static final String EXPOSED_MINE_SYMBOL = "M";

	// visual indication of an exposed MyJButton
	private static final Color CELL_EXPOSED_BACKGROUND_COLOR = Color.lightGray;
	
	// colors used when displaying the getStateStr() String
	private static final Color CELL_EXPOSED_FOREGROUND_COLOR_MAP[] = { Color.lightGray, Color.blue, Color.green,
			Color.cyan, Color.yellow, Color.orange, Color.pink, Color.magenta, Color.red, Color.red };

	private boolean running = true;
	
	// holds the "number of mines in perimeter" value for each MyJButton
	private int[][] mineGrid = new int[MINE_GRID_ROWS][MINE_GRID_COLS];

	public MineSweapPart() {
		this.setTitle("MineSweap                                                         "
				+ MineSweapPart.guessedMinesLeft + " Mines left");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setResizable(false);
		this.setLayout(new GridLayout(MINE_GRID_ROWS, MINE_GRID_COLS, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.createContents();
		
		// place MINES number of mines in mineGrid and adjust all of the "mines in
		// perimeter" values
		this.setMines();
		this.setVisible(true);
	}

	public void createContents() {
		for (int gr = 0; gr < MINE_GRID_ROWS; ++gr) {
			for (int gc = 0; gc < MINE_GRID_COLS; ++gc) {
				
				// set sGrid[gr][gc] entry to 0 - no mines in it's perimeter
				this.mineGrid[gr][gc] = 0;
				
				// create a MyJButton that will be at location (br, bc) in the GridLayout
				MyJButton but = new MyJButton("", gr, gc);
				
				// register the event handler with this MyJbutton
				but.addActionListener(new MyListener());
				
				// add the MyJButton to the GridLayout collection
				this.add(but);
			}
		}
	}

	// place TOTAL_MINES number of mines in mineGrid and adjust all of the "mines in
	// perimeter" values

	private void setMines() {
		Random rand = new Random();
		int rowMine = rand.nextInt(MINE_GRID_ROWS);
		int colMine = rand.nextInt(MINE_GRID_COLS);
		int count = 0;
		while (count < TOTAL_MINES) {
			do {
				rowMine = rand.nextInt(MINE_GRID_ROWS);
				colMine = rand.nextInt(MINE_GRID_COLS);
			} while (mineGrid[rowMine][colMine] != NO_MINES_IN_PERIMETER_GRID_VALUE);
			mineGrid[rowMine][colMine] = IS_A_MINE_IN_GRID_VALUE;
			count++;
		}
		for(int row = 0; row < MINE_GRID_ROWS; row++) {
			for(int col = 0; col < MINE_GRID_COLS; col++) {
				int findMine = 0;
				if(this.mineGrid[row][col] != IS_A_MINE_IN_GRID_VALUE) {
					if((row - 1) >= 0) {
						if((col - 1) >= 0) {
							if(mineGrid[row - 1][col - 1] == IS_A_MINE_IN_GRID_VALUE) {
								findMine++;
							}
						}
						if(mineGrid[row - 1][col] == IS_A_MINE_IN_GRID_VALUE) {
							findMine++;
						}
						if((col + 1) < MINE_GRID_COLS) {
							if(mineGrid[row - 1][col + 1] == IS_A_MINE_IN_GRID_VALUE) {
								findMine++;
							}
						}
					}
					if((col - 1) >= 0) {
						if (mineGrid[row][col - 1] == IS_A_MINE_IN_GRID_VALUE) {
							findMine++;
						}
					}
					if((col + 1) < MINE_GRID_COLS) {
						if (mineGrid[row][col + 1] == IS_A_MINE_IN_GRID_VALUE) {
							findMine++;
						}
					}
					if((row + 1) < MINE_GRID_ROWS) {
						if ((col - 1) >= 0) {
							if (mineGrid[row + 1][col - 1] == IS_A_MINE_IN_GRID_VALUE) {
								findMine++;
							}
						}
						if (mineGrid[row + 1][col] == IS_A_MINE_IN_GRID_VALUE) {
							findMine++;
						}
						if ((col + 1) < MINE_GRID_COLS) {
							if (mineGrid[row + 1][col + 1] == IS_A_MINE_IN_GRID_VALUE) {
								findMine++;
							}
						}
					}
					mineGrid[row][col] = findMine;
				}
			}
		}
	}

	private String getGridValueStr(int row, int col) {
		
		// no mines in this MyJbutton's perimeter
		if (this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_GRID_VALUE)
			return "";
		
		// 1 to 8 mines in this MyJButton's perimeter
		else if (this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_GRID_VALUE
				&& this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_GRID_VALUE)
			return "" + this.mineGrid[row][col];
		
		// this MyJButton in a mine
		else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
			return MineSweapPart.EXPOSED_MINE_SYMBOL;
	}

	// nested private class
	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (running) {
				
				// used to determine if ctrl or alt key was pressed at the time of mouse action
				int mod = event.getModifiers();
				MyJButton mjb = (MyJButton) event.getSource();
				
				// is the MyJbutton that the mouse action occurred in flagged
				boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
				
				// is the MyJbutton that the mouse action occurred in already exposed
				boolean exposed = mjb.getBackground().equals(CELL_EXPOSED_BACKGROUND_COLOR);
				
				// flag a cell : ctrl + left click
				if (!flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0) {
					mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
					--MineSweapPart.guessedMinesLeft;
					
					// if the MyJbutton that the mouse action occurred in is a mine
					// 10 pts
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
						--MineSweapPart.actualMinesLeft;
						if(MineSweapPart.actualMinesLeft == 0 && MineSweapPart.guessedMinesLeft >= 0) {
							JFrame gameOver = new JFrame();
							gameOver.setTitle("You Won");
							gameOver.setSize(500, 200);
							gameOver.setResizable(false);
							gameOver.setLayout(new FlowLayout());
							gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
							gameOver.setVisible(true);
							JLabel label = new JLabel("Game Over, you found all of the mines!!!");
							gameOver.add(label);
						}
					}
					setTitle("MineSweap                                                         "
							+ MineSweapPart.guessedMinesLeft + " Mines left");
				}
				
				// unflag a cell : alt + left click
				else if (flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0) {
					mjb.setText("");
					++MineSweapPart.guessedMinesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					// 10 pts
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
						++MineSweapPart.actualMinesLeft; 
					}
					setTitle("MineSweap                                                         "
							+ MineSweapPart.guessedMinesLeft + " Mines left");
				}
				
				// expose a cell : left click
				else if (!flagged && !exposed) {
					exposeCell(mjb);
				}
			}
		}

		public void exposeCell(MyJButton mjb) {
			if(!running)
				return;
			
			// expose this MyJButton 
			mjb.setBackground(CELL_EXPOSED_BACKGROUND_COLOR);
			mjb.setForeground(CELL_EXPOSED_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
			mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
			
			// if the MyJButton that was just exposed is a mine
			if(mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {  
        // what else do you need to adjust?
				// could the game be over?
				// if the game is over - what else needs to be exposed / highlighted
				for(int row = 0; row < MINE_GRID_ROWS; row++) {
					for(int col = 0; col < MINE_GRID_COLS; col++) {
						if(mineGrid[row][col] == IS_A_MINE_IN_GRID_VALUE) {
							MyJButton jbn = (MyJButton)mjb.getParent().getComponent((row * MINE_GRID_COLS) + col);
							if(jbn.getText() != UNEXPOSED_FLAGGED_MINE_SYMBOL) {
								jbn.setText(EXPOSED_MINE_SYMBOL);
							}
							jbn.setForeground(CELL_EXPOSED_FOREGROUND_COLOR_MAP[mineGrid[jbn.ROW][jbn.COL]]);
						}
					}
				}
				JFrame gameOver = new JFrame();
				gameOver.setTitle("You Lost");
				gameOver.setSize(500, 200);
				gameOver.setResizable(false);
				gameOver.setLayout(new FlowLayout());
				gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
				gameOver.setVisible(true);
				JLabel label = new JLabel("Game Over, you clicked on a mine!!!");
				gameOver.add(label);
				return; 
			}

			// if the MyJButton that was just exposed has no mines in its perimeter
			if(mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_GRID_VALUE) {
				
				// lots of work here - must expose all MyJButtons in its perimeter
				// and so on
				// and so on
				// .
				// .
				// .
				// Hint : MyJButton jbn = (MyJButton)mjb.getParent().getComponent(<linear index>);
				//
				
				int ct = ((mjb.ROW - 1) *  MINE_GRID_COLS) + mjb.COL;
				int crtt = ((mjb.ROW - 1) * MINE_GRID_COLS) + (mjb.COL + 1);
				int clfb = ((mjb.ROW + 1) * MINE_GRID_COLS) + (mjb.COL - 1);
				int crtb = ((mjb.ROW + 1) * MINE_GRID_COLS) + (mjb.COL + 1);
				int cb = ((mjb.ROW + 1) * MINE_GRID_COLS) + mjb.COL;
				int clf = (mjb.ROW * MINE_GRID_COLS) + (mjb.COL - 1);
				int clft = ((mjb.ROW - 1) * MINE_GRID_COLS) + (mjb.COL - 1);
				int crt = (mjb.ROW * MINE_GRID_COLS) + (mjb.COL + 1);
				int row = mjb.ROW; 
				int col = mjb.COL;
				int totalRow = MINE_GRID_ROWS;
				int totalCol = MINE_GRID_COLS; 
				int totalElem = (MINE_GRID_ROWS * MINE_GRID_COLS);
				
				if((ct >= 0) && (ct < totalElem) && ((row - 1) >= 0)) {
					checkCell(ct,mjb);
				}
				if((crtt >= 0) && (crtt < totalElem) && ((row - 1) >= 0) && ((col + 1) < totalCol)) {
					checkCell(crtt,mjb);
				}
				if((clfb >= 0) && (clfb < totalElem) && ((row + 1) < totalRow) && ((col - 1) >= 0)) {
					checkCell(clfb,mjb);
				}
				if((crtb >= 0) && (crtb < totalElem) && ((row + 1) < totalRow) && ((col + 1) < totalCol)) {
					checkCell(crtb,mjb);
				}
				if((cb >= 0) && (cb < totalElem) && ((row + 1) < totalRow)) {
					checkCell(cb,mjb);
				}	
				if((clf >= 0) && (clf < totalElem) && ((col - 1) >= 0)) {
					checkCell(clf,mjb);
				}	
				if((clft >= 0) && (clft < totalElem) && ((row - 1) >= 0) && ((col - 1) >=0)) {
					checkCell(clft,mjb);
				}	
				if((crt >= 0) && (crt < totalElem) && ((col + 1) < totalCol)) {
					checkCell(crt,mjb);
				}	
			
				// nested private class
			}
		}
		
		public void checkCell(int comp, MyJButton mjb) {
			MyJButton jbn = (MyJButton)mjb.getParent().getComponent(comp);
			if(jbn.getBackground() != CELL_EXPOSED_BACKGROUND_COLOR) {
				if(jbn.getText() != UNEXPOSED_FLAGGED_MINE_SYMBOL) {
					if(mineGrid[jbn.ROW][jbn.COL] != IS_A_MINE_IN_GRID_VALUE) {
						exposeCell(jbn);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new MineSweapPart();
	}
}
