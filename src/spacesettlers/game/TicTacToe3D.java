package spacesettlers.game;

import java.util.Random;

/**
 * 3D tic tac toe or naughts and crosses
 * 
 * @author amy
 *
 */
public class TicTacToe3D extends AbstractGame {
	TicTacToe3DBoard myBoard;
	private boolean isPlayer1Turn;
	private Random random;
	private final AbstractGameAgent player1, player2;

	/**
	 * Initialize an empty board and choose a random first player
	 */
	public TicTacToe3D(final AbstractGameAgent lhs_player, final AbstractGameAgent rhs_player) {
		myBoard = new TicTacToe3DBoard();
		random = new Random();
		isPlayer1Turn = true;
		
		if (random.nextBoolean()) {
			(this.player1 = lhs_player).setPlayer(AbstractGame.player1);
			(this.player2 = rhs_player).setPlayer(AbstractGame.player2);
		} else {
			(this.player1 = rhs_player).setPlayer(AbstractGame.player1);
			(this.player2 = lhs_player).setPlayer(AbstractGame.player2);
		}
	}

	/**
	 * Used only for unit tests so the board is set to something specific
	 * @param board
	 */
	public TicTacToe3D(int [][][]board, boolean player, final AbstractGameAgent player1, final AbstractGameAgent player2) {
		this.myBoard = new TicTacToe3DBoard();
		this.myBoard.setBoard(board);
		isPlayer1Turn = player;
		random = new Random();
		(this.player1 = player1).setPlayer(AbstractGame.player1);
		(this.player2 = player2).setPlayer(AbstractGame.player2);
	}


	/**
	 * is the game over?  check all conditions.  
	 * 
	 * @return true if the game is over and false otherwise
	 */
	public boolean isGameOver() {
		if (myBoard.getWinningPlayer() != myBoard.empty) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AbstractGameAgent getPlayer1() { return player1; }
	
	@Override
	public AbstractGameAgent getPlayer2() { return player2; }
	
	@Override
	public AbstractGameAgent getCurrentPlayer() { return isPlayer1Turn ? player1 : player2; }

	/**
	 * Return true if is player 1's turn
	 */
	public boolean getTurn() {
		return isPlayer1Turn;
	}

	@Override
	public void playAction(AbstractGameAction action) {
		TicTacToe3DAction TTTAction = (TicTacToe3DAction) action;
		this.myBoard.makeMove(TTTAction, getCurrentPlayer().getPlayer());
		isPlayer1Turn = !isPlayer1Turn;
	}

	/**
	 * Returns true if player 1 is the winner.  
	 */
	public int getWinner() {
		return myBoard.getWinningPlayer();
	}

	@Override
	public AbstractGameBoard getBoard() {
		return myBoard.deepClone();
	}


}
