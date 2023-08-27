package spacesettlers.game;

public class TicTacToe2DBoard extends AbstractGameBoard {
	int[][] board;
	static int empty = 0;
	static int board_size = 3;

	public TicTacToe2DBoard() {
		board = new int[board_size][board_size];
	}
	
	/**
	 * Returns the empty value for the board
	 * @return
	 */
	public static int getEmpty() {
		return empty;
	}

	
	/**
	 * Returns the size along each dimension for the board
	 * @return
	 */
	public static int getBoardSize() {
		return board_size;
	}

	public TicTacToe2DBoard deepClone() {
		TicTacToe2DBoard newBoard = new TicTacToe2DBoard();

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				newBoard.board[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	public void setBoard(int [][]newBoard) {
		this.board = newBoard;
	}

	/**
	 * finds the winning player for 2D TTT
	 * @return 1 if there is a winner and 0 otherwise (this includes ties)
	 */
	public int getWinningPlayer() {
		// check across the rows
		for (int row = 0; row < board_size; row++) {
			int num_in_row = 1;
			int player = board[row][0];
			if (player != empty) {
				for (int col = 1; col < board_size; col++) {
					if (board[row][col] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == board_size) {
						return player;
					}
				}
			}
		}

		// check down the columns
		for (int col = 0; col < board_size; col++) {
			int num_in_row = 1;
			int player = board[0][col];

			if (player != empty) {
				for (int row = 1; row < board_size; row++) {
					if (board[row][col] == player) {
						num_in_row++;
					} else {
						break;
					}
					if (num_in_row == board_size) {
						return player;
					}
				}
			}
		}

		// check the diagonals
		int player = board[0][0];
		int num_in_row = 1;
		if (player != empty) {
			for (int row = 1; row < board_size; row++) {
				if (board[row][row] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == board_size) {
				return player;
			}
		}

		player = board[0][board_size-1];
		num_in_row = 1;
		if (player != empty) {
			for (int row = 1; row < board_size; row++) {
				if (board[row][board_size-row-1] == player) {
					num_in_row++;
				} else {
					break;
				}
			}
			if (num_in_row == board_size) {
				return player;
			}
		}

		// make sure there are places left to play
		for (int col = 0; col < board_size; col++) {
			for (int row = 1; row < board_size; row++) {
				if (board[row][col] == empty) {
					return empty;
				}
			}
		}

		return -1;
	}
	
	
	/**
	 * Returns a clone of the board (so agents can't set anything)
	 * 
	 * @return
	 */
	public int[][]getBoard() {
		int[][] new_board = new int[board_size][board_size];

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				new_board[i][j] = board[i][j];
			}
		}
		return new_board;
	}

	/**
	 * Makes the move - if the spot is not empty, the move is ignored (turn lost)
	 * 
	 * @param TTTAction
	 * @param player
	 */
	public void makeMove(TicTacToe2DAction TTTAction, int player) {
		if (board[TTTAction.row][TTTAction.col] == empty) {
			board[TTTAction.row][TTTAction.col] = player;
		}
	}
	
	/**
	* Unmakes a move previously made
	*
	* @param TTTAction
	*/ 
	public void unMakeMove(TicTacToe2DAction TTTAction) {
		board[TTTAction.row][TTTAction.col] = empty;
	}
	

	/**
	 * print out the board to a string
	 */
	public String toString() {
		StringBuffer myStr = new StringBuffer();

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				myStr.append(board[i][j]);
			}
			myStr.append("\n");
		}
		return myStr.toString();
	}
	
}
