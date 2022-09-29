package spacesettlers.game;

import java.util.Random;

/**
 * 3D tic tac toe or naughts and crosses
 * 
 * @author amy
 *
 */
public class TicTacToe3D extends AbstractGame<TicTacToe3DBoard, TicTacToe3DGameAgent> implements Runnable {
	TicTacToe3DBoard myBoard;
	private boolean isPlayer1Turn;
	
	final TicTacToe3DGameAgent player1, player2;

	/**
	 * Initialize an empty board and randomly assign selected players as player1 and player2.
	 * @param player_lhs 
	 * @param player_rhs
	 */
	public TicTacToe3D(final TicTacToe3DGameAgent player_lhs, final TicTacToe3DGameAgent player_rhs) {
		myBoard = new TicTacToe3DBoard();
		
		final Random random = new Random();
		isPlayer1Turn = new Random().nextBoolean();
		
		if (random.nextBoolean()) {
			(player1 = player_lhs).setPlayer(AbstractGame.PLAYER1_ID);
			(player2 = player_rhs).setPlayer(AbstractGame.PLAYER2_ID);
		} else {
			(player1 = player_rhs).setPlayer(AbstractGame.PLAYER1_ID);
			(player2 = player_lhs).setPlayer(AbstractGame.PLAYER2_ID);
		}
	}

	/**
	 * Used only for unit tests so the board is set to something specific
	 * @param board
	 */
	public TicTacToe3D(int [][][]board, boolean isPlayer1Turn, TicTacToe3DGameAgent player1, TicTacToe3DGameAgent player2) {
		this.myBoard = new TicTacToe3DBoard();
		this.myBoard.setBoard(board);
		this.isPlayer1Turn = isPlayer1Turn;
		(this.player1 = player1).setPlayer(1);
		(this.player2 = player2).setPlayer(2);
	}


	/**
	 * is the game over?  check all conditions.  
	 * 
	 * @return true if the game is over and false otherwise
	 */
	public boolean isGameOver() {
		return myBoard.getWinningPlayer() != TicTacToe3DBoard.EMPTY;
		
	}

	/**
	 * Return true if is player 1's turn
	 */
	public boolean getTurn() {
		return isPlayer1Turn;
	}

	@Override
	public void playCurrentTurn() {
		final TicTacToe3DGameAgent player = getCurrentPlayer();
		myBoard.makeMove(player.getNextMove(myBoard), player.getPlayerID());
		isPlayer1Turn = !isPlayer1Turn;
	}

	/**
	 * Returns the winning player id if any. Else, the empty value (0).
	 */
	public int getWinner() {
		return myBoard.getWinningPlayer();
	}

	@Override
	public TicTacToe3DBoard getBoard() {
		return myBoard.deepClone();
	}

	/**
	 * @return The player whose turn it is currently.
	 */
	public TicTacToe3DGameAgent getCurrentPlayer() {
		return isPlayer1Turn ? player1 : player2;
	}

	@Override
	public void run() {
		while (!isGameOver()) {
			playCurrentTurn();
		}
	}

}
