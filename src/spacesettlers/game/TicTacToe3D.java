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
	private boolean currentPlayer;
	private Random random;

	/**
	 * Initialize an empty board and choose a random first player
	 */
	public TicTacToe3D() {
		myBoard = new TicTacToe3DBoard();
		random = new Random();
		currentPlayer = random.nextBoolean();
		super.heuristicPlayer = player1;
	}

	/**
	 * Used only for unit tests so the board is set to something specific
	 * @param board
	 */
	public TicTacToe3D(int [][][]board, boolean player) {
		this.myBoard = new TicTacToe3DBoard();
		this.myBoard.setBoard(board);
		currentPlayer = player;
		random = new Random();
		super.heuristicPlayer = player1;
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

	/**
	 * Return true if is player 1's turn
	 */
	public boolean getTurn() {
		return currentPlayer;
	}

	@Override
	public void playAction(AbstractGameAction action) {
		TicTacToe3DAction TTTAction = (TicTacToe3DAction) action;
		int player = player1;
		
		if (!currentPlayer) {
			player = player2;
		}
		
		this.myBoard.makeMove(TTTAction, player);
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
