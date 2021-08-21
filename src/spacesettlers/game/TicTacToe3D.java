package spacesettlers.game;

import static org.junit.Assert.assertTrue;

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
	private static int board_size = 3;

	private int[][][] board;

	private boolean currentPlayer;

	private Random random;

	/**
	 * Initialize an empty board and choose a random first player
	 */
	public TicTacToe3D() {
		board = new int[board_size][board_size][board_size];
		random = new Random();
		currentPlayer = random.nextBoolean();
	}

	/**
	 * Used only for unit tests so the board is set to something specific
	 * @param board
	 */
	public TicTacToe3D(int [][][]board, boolean player) {
		this.board = board;
		currentPlayer = player;
		random = new Random();
	}


	@Override
	public boolean isGameOver() {

		// check in the 2D boards at each depth
		for (int dep = 0; dep < board_size; dep++) {

			// check across the rows
			for (int row = 0; row < board_size; row++) {
				int num_in_row = 1;
				int player = board[row][0][dep]; 
				if (player != empty) {
					for (int col = 1; col < board_size; col++) {
						if (board[row][col][dep] == player) {
							num_in_row++;
						} else {
							break;
						}
						if (num_in_row == board_size) {
							return true;
						}
					}
				}
			}

			// check down the columns
			for (int col = 0; col < board_size; col++) {
				int num_in_row = 1;
				int player = board[0][col][dep];  

				if (player != empty) {
					for (int row = 1; row < board_size; row++) {
						if (board[row][col][dep] == player) {
							num_in_row++;
						} else {
							break;
						}
						if (num_in_row == board_size) {
							return true;
						}
					}
				}
			}

			// check the diagonals
			int player = board[0][0][dep];
			int num_in_row = 1;
			if (player != empty) {
				for (int row = 1; row < board_size; row++) {
					if (board[row][row][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
				}
				if (num_in_row == board_size) {
					return true;
				}
			}

			player = board[0][board_size-1][dep];  
			num_in_row = 1;
			if (player != empty) {
				for (int row = 1; row < board_size; row++) {
					if (board[row][board_size-row-1][dep] == player) {
						num_in_row++;
					} else {
						break;
					}
				}
				if (num_in_row == board_size) {
					return true;
				}
			}
		}


		// check rows across depth
		for (int row = 0; row < board_size; row++) {

			// check across the rows
			for (int dep = 0; dep < board_size; dep++) {
				int num_in_row = 1;
				int player = board[row][0][dep];  
				if (player != empty) {
					for (int col = 1; col < board_size; col++) {
						if (board[row][col][dep] == player) {
							num_in_row++;
						} else {
							break;
						}
						if (num_in_row == board_size) {
							return true;
						}
					}
				}
			}

			// check down the columns
			for (int dep = 0; dep < board_size; dep++) {
				int num_in_row = 1;
				int player = board[row][0][dep];  

				for (int col = 1; col < board_size; col++) {
					if (player != empty) {
						if (board[row][col][dep] == player) {
							num_in_row++;
						} else {
							break;
						}
						if (num_in_row == board_size) {
							return true;
						}
					}
				}
			}
		}

		// check the 3D diagonals
		if ((board[0][0][0] == board[1][1][1]) && (board[2][2][2] == board[1][1][1]) && (board[0][0][0] != empty)) {
			return true;
		}

		if ((board[2][0][0] == board[1][1][1]) && (board[0][2][2] == board[1][1][1]) && (board[2][0][0] != empty)) {
			return true;
		}

		if ((board[0][0][2] == board[1][1][1]) && (board[2][2][0] == board[1][1][1]) && (board[0][0][2] != empty)) {
			return true;
		}

		if ((board[0][2][0] == board[1][1][1]) && (board[2][0][2] == board[1][1][1]) && (board[0][2][0] != empty)) {
			return true;
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
