package spacesettlers.game;

/**
 * Plays TicTacToe2D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class HeuristicTicTacToe2DGameAgent extends AbstractGameAgent {

	public HeuristicTicTacToe2DGameAgent() {
	}

	/**
	 * First see if we can win in one and then otherwise take the first available 
	 * (this will be improved over time)
	 * 
	 * @param game
	 * @return
	 */
	public AbstractGameAction getNextMove(AbstractGame game) {
		TicTacToe2DBoard board = (TicTacToe2DBoard) game.getBoard();
		//System.out.println("Heuristic agent current state of the board is \n" + board);
		//System.out.println("Player is " + this.getPlayer());

		// check to see if the center is free
		if (board.board[1][1] == board.empty) {
			return new TicTacToe2DAction(1, 1);
		}

		// check to see if we can win in one
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board.board[i][j] == board.empty) {
					TicTacToe2DAction action = new TicTacToe2DAction(i, j);
					board.makeMove(action, this.player);
					if (board.getWinningPlayer() == this.player) {
						return action;
					}
					// unmake the move
					board.board[i][j] = board.empty;
				}
			}
		}
		
		
		// otherwise play the first available move
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board.board[i][j] == board.empty) {
					return new TicTacToe2DAction(i, j);
				}
			}
			
		}

		System.out.println("Returning a null action for the following board\n");
		System.out.println(board);
		return null;
	}

}
