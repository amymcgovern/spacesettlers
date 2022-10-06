package spacesettlers.game;

public class TicTacToe3DBoard extends AbstractGameBoard {
	int[][][] board;
	static int empty = 0;
	static int board_size = 3;

	public TicTacToe3DBoard() {
		board = new int[board_size][board_size][board_size];
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

	public TicTacToe3DBoard deepClone() {
		TicTacToe3DBoard newBoard = new TicTacToe3DBoard();

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				for (int k = 0; k < board_size; k++) {
					newBoard.board[i][j][k] = board[i][j][k];
				}
			}
		}
		return newBoard;
	}

	public void setBoard(int [][][]newBoard) {
		this.board = newBoard;
	}

	public int getWinningPlayer() {
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
							return player;
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
							return player;
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
					return player;
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
					return player;
				}
			}
		}


		// check the 3D diagonals
		int centerPoint;
		
		//Cross diagonals (4)
		centerPoint = board [1][1][1];
		if (centerPoint != empty) {
			
			/*
					x-- --- ---
					--- -x- ---
					--- --- --x
			*/
			if ((board[0][0][0] == centerPoint) && (board[2][2][2] == centerPoint)) {
				return centerPoint;
			}

			/*
					--- --- --x
					--- -x- ---
					x-- --- ---
			*/
			if ((board[2][0][0] == centerPoint) && (board[0][2][2] == centerPoint)) {
				return centerPoint;
			}

			/*
					--- --- x--
					--- -x- ---
					--x --- ---
			*/
			if ((board[0][0][2] == centerPoint) && (board[2][2][0] == centerPoint)) {
				return centerPoint;
			}

			/*
					--x --- ---
					--- -x- ---
					--- --- x--
			*/
			if ((board[0][2][0] == centerPoint) && (board[2][0][2] == centerPoint)) {
				return centerPoint;
			}
		}

		//Columnn 3D Diagonals (6)
		/*
					x-- --- ---
					--- x-- ---
					--- --- x--
		*/
		/*
					--- --- x--
					--- x-- ---
					x-- --- ---
		*/
		centerPoint = board[1][0][1];
		if (centerPoint != empty) {
			if ((board[0][0][0] == centerPoint)  && (board[2][0][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[2][0][0] == centerPoint)  && (board[0][0][2] == centerPoint)) {
				return centerPoint;
			}
		}

		/*
					-x- --- ---
					--- -x- ---
					--- --- -x-
		*/
		/*
					--- --- -x-
					--- -x- ---
					-x- --- ---
		*/
		centerPoint = board[1][1][1];
		if (centerPoint != empty) {
			if ((board[0][1][0] == centerPoint)  && (board[2][1][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[2][1][0] == centerPoint)  && (board[0][1][2] == centerPoint)) {
				return centerPoint;
			}
		}

		/*
					--- --- --x
					--- --x ---
					--x --- ---
		*/
		/*
					--x --- ---
					--- --x ---
					--- --- --x
		*/
		centerPoint = board[1][2][1];
		if (centerPoint != empty) {
			if ((board[2][2][0] == centerPoint)  && (board[0][2][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[2][1][0] == centerPoint)  && (board[0][1][2] == centerPoint)) {
				return centerPoint;
			}
		}

		//Row 3D diagonals (6)
		/*
					x-- -x- --x
					--- --- ---
					--- --- ---
		*/
		/*
					--x -x- x--
					--- --- ---
					--- --- ---
		*/
		centerPoint = board[0][1][1];
		if (centerPoint != empty) {
			if ((board[0][0][0] == centerPoint)  && (board[0][2][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[0][2][0] == centerPoint)  && (board[0][0][2] == centerPoint)) {
				return centerPoint;
			}
		}

		/*
					--- --- ---
					x-- -x- --x
					--- --- ---
		*/
		/*
					--- --- ---
					--x -x- x--
					--- --- ---
		*/
		centerPoint = board[1][1][1];
		if (centerPoint != empty) {
			if ((board[1][0][0] == centerPoint)  && (board[1][2][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[1][2][0] == centerPoint)  && (board[1][0][2] == centerPoint)) {
				return centerPoint;
			}
		}

		/*
					--- --- ---
					--- --- ---
					x-- -x- --x
		*/
		/*
					--- --- ---
					--- --- ---
					--x -x- x--
		*/
		centerPoint = board[2][1][1];
		if (centerPoint != empty) {
			if ((board[2][0][0] == centerPoint)  && (board[2][2][2] == centerPoint)) {
				return centerPoint;
			}

			if ((board[2][2][0] == centerPoint)  && (board[2][0][2] == centerPoint)) {
				return centerPoint;
			}
		}

		//Check the pillars
		/*
					x-- x-- x--
					--- --- ---
					--- --- ---
		*/
		for (int row = 0; row < board_size; row++) {
			for (int col = 0; col < board_size; col++) {
				int num_in_pillar = 1;
				int player = board[row][col][0];
				if (player != empty) {
					for (int dep = 1; dep < board_size; dep++) {
						if (board[row][col][dep] == player) {
							num_in_pillar++;
						} else {
							break;
						}
						if (num_in_pillar == board_size) {
							return player;
						}
					}
				}
			}
		}


		return empty;
	}
	
	
	/**
	 * Returns a clone of the board (so agents can't set anything)
	 * 
	 * @return
	 */
	public int[][][]getBoard() {
		int[][][] new_board = new int[board_size][board_size][board_size];

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				for (int k = 0; k < board_size; k++) {
					new_board[i][j][k] = board[i][j][k];
				}
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
	public void makeMove(TicTacToe3DAction TTTAction, int player) {
		if (board[TTTAction.row][TTTAction.col][TTTAction.depth] == empty) {
			board[TTTAction.row][TTTAction.col][TTTAction.depth] = player;
		}
	}
	
	/**
	* Unmakes a move previously made
	*
	* @param TTTAction
	*/ 
	public void unMakeMove(TicTacToe3DAction TTTAction) {
		board[TTTAction.row][TTTAction.col][TTTAction.depth] = empty;
	}
	

	/**
	 * print out the board to a string
	 */
	public String toString() {
		StringBuffer myStr = new StringBuffer();
		
		for (int k = 0; k < board_size; k++) {
			myStr.append("Board at depth " + k + "\n");
			for (int i = 0; i < board_size; i++) {
				for (int j = 0; j < board_size; j++) {
					myStr.append(board[i][j][k]);
				}
				myStr.append("\n");
			}
		}
		return myStr.toString();
	}
	
}
