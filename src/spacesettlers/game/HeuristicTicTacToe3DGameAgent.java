package spacesettlers.game;

/**
 * Plays TicTacToe3D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class HeuristicTicTacToe3DGameAgent extends AbstractGameAgent {
	public HeuristicTicTacToe3DGameAgent(int player) {
		this.player = player;
	}

	/**
	 * For right now (just to ensure the code is working all the way through), the heuristic plays in the first
	 * available move
	 * 
	 * @param game
	 * @return
	 */
	public AbstractGameAction getNextMove(AbstractGame game) {
		TicTacToe3DBoard board = (TicTacToe3DBoard) game.getBoard();
		
		// check to see if the center is free
		if (board.board[1][1][1] == board.empty) {
			return new TicTacToe3DAction(1, 1, 1);
		}
		
		// check to see if the other two centers are free
		if (board.board[1][1][0] == board.empty) {
			return new TicTacToe3DAction(1, 1, 0);
		}

		if (board.board[1][1][2] == board.empty) {
			return new TicTacToe3DAction(1, 1, 2);
		}

		// check to see if we can win in one
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (board.board[i][j][k] == board.empty) {
						TicTacToe3DAction action = new TicTacToe3DAction(i, j, k);
						board.makeMove(action, this.player);
						if (board.getWinningPlayer() == this.player) {
							return action;
						}
						// unmake the move
						board.board[i][j][k] = board.empty;
					}
				}
			}
		}
		
		
		// otherwise play the first available move
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (board.board[i][j][k] == board.empty) {
						return new TicTacToe3DAction(i, j, k);
					}
				}
			}
			
		}
		return null;
	}

}
