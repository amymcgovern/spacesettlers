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
