package spacesettlers.game;

/**
 * Plays TicTacToe3D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class HeuristicTicTacToe3DGameAgent extends TicTacToe3DGameAgent {
	/**
	 * First see if we can win in one and then otherwise take the first available 
	 * (this will be improved over time)
	 * 
	 * @param game
	 * @return
	 */
	@Override
	public TicTacToe3DAction getNextMove(final TicTacToe3DBoard board) {	
		// check to see if the center is free
		if (board.board[1][1][1] == TicTacToe3DBoard.EMPTY) {
			return new TicTacToe3DAction(1, 1, 1);
		}
		
		// check to see if the other two centers are free
		if (board.board[1][1][0] == TicTacToe3DBoard.EMPTY) {
			return new TicTacToe3DAction(1, 1, 0);
		}

		if (board.board[1][1][2] == TicTacToe3DBoard.EMPTY) {
			return new TicTacToe3DAction(1, 1, 2);
		}

		// check to see if we can win in one
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (board.board[i][j][k] == TicTacToe3DBoard.EMPTY) {
						TicTacToe3DAction action = new TicTacToe3DAction(i, j, k);
						board.makeMove(action, this.player);
						if (board.getWinningPlayer() == this.player) {
							return action;
						}
						// unmake the move
						board.board[i][j][k] = TicTacToe3DBoard.EMPTY;
					}
				}
			}
		}
		
		
		// otherwise play the first available move
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (board.board[i][j][k] == TicTacToe3DBoard.EMPTY) {
						return new TicTacToe3DAction(i, j, k);
					}
				}
			}
			
		}
		return null;
	}

}
