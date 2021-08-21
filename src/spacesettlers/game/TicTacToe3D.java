package spacesettlers.game;

import java.util.Random;

/**
 * 3D tic tac toe or naughts and crosses
 * 
 * @author amy
 *
 */
public class TicTacToe3D extends AbstractGame {
	private static int player1 = 1;
	private static int player2 = 2;
	private static int empty = 0;
	private static int num_rows = 3;
	private static int num_cols = 3;
	private static int num_depth = 3;

	private int[][][] board;

	private boolean currentPlayer;

	private Random random;

	/**
	 * Initialize an empty board and choose a random first player
	 */
	public TicTacToe3D() {
		board = new int[num_rows][num_cols][num_depth];
		random = new Random();
		currentPlayer = random.nextBoolean();

	}

	@Override
	public boolean isGameOver() {

		// check in the 2D boards at each depth
		for (int dep = 0; dep < num_depth; dep++) {

			// check across the rows
			for (int row = 0; row < num_rows; row++) {
				int num_in_row = 1;
				int player = board[row][0][dep];  

				for (int col = 1; col < num_cols; col++) {
					if (board[row][col][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == num_rows) {
						return true;
					}
				}
			}

			// check down the columns
			for (int col = 0; col < num_cols; col++) {
				int num_in_row = 1;
				int player = board[0][col][dep];  

				for (int row = 1; row < num_rows; row++) {
					if (board[row][col][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == num_rows) {
						return true;
					}
				}
			}

			// check the diagonals
			int player = board[0][0][dep];  
			int num_in_row = 1;

			for (int row = 0; row < num_rows; row++) {
				if (board[row][row][dep] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == num_rows) {
				return true;
			}

			player = board[num_rows-1][num_cols-1][dep];  
			num_in_row = 1;
			for (int row = num_rows-1; row <= 0; row++) {
				if (board[row][row][dep] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == num_rows) {
				return true;
			}
		}


		// check in the 2D boards at each depth
		for (int dep = 0; dep < num_depth; dep++) {

			// check across the rows
			for (int row = 0; row < num_rows; row++) {
				int num_in_row = 1;
				int player = board[row][0][dep];  

				for (int col = 1; col < num_cols; col++) {
					if (board[row][col][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == num_rows) {
						return true;
					}
				}
			}

			// check down the columns
			for (int col = 0; col < num_cols; col++) {
				int num_in_row = 1;
				int player = board[0][col][dep];  

				for (int row = 1; row < num_rows; row++) {
					if (board[row][col][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == num_rows) {
						return true;
					}
				}
			}

			// check the diagonals
			int player = board[0][0][dep];  
			int num_in_row = 1;

			for (int row = 0; row < num_rows; row++) {
				if (board[row][row][dep] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == num_rows) {
				return true;
			}

			player = board[num_rows-1][num_cols-1][dep];  
			num_in_row = 1;
			for (int row = num_rows-1; row <= 0; row++) {
				if (board[row][row][dep] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == num_rows) {
				return true;
			}
		}


		return false;
	}


	@Override
	public boolean getTurn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void playAction(AbstractGameAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getWinner() {
		// TODO Auto-generated method stub
		return false;
	}


}
