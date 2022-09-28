package spacesettlers.game;

/**
 * Abstract 2 player Game class so we can implement more than one type of game eventually
 * 
 * @author amy
 *
 */
public abstract class AbstractGame<T extends AbstractGameBoard, U extends AbstractGameAgent<T, ? extends AbstractGameAction>> {
	 public static int PLAYER1_ID = 1;
	 public static int PLAYER2_ID = 2;
	
	/** 
	 * Is the game over?
	 *  
	 * @return true if the game is over and false otherwise
	 */
	public abstract boolean isGameOver();

	/**
	 * Return true if it is the true player's turn and false otherwise
	 * @return
	 */
	public abstract boolean getTurn();
	
	/**
	 * Play the action for the player.
	 */
	public abstract void playCurrentTurn();
	
	/**
	 * Return the winning player (remember players are true and false)
	 * 
	 * @return
	 */
	public abstract int getWinner();
	
	/**
	 * Get the game board
	 * @return
	 */
	public abstract T getBoard();
	
}
