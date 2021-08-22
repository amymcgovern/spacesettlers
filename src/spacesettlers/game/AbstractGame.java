package spacesettlers.game;

/**
 * Abstract 2 player Game class so we can implement more than one type of game eventually
 * 
 * @author amy
 *
 */
public abstract class AbstractGame {
	 public static int player1 = 1;
	 public static int player2 = 2;
	 
	 int heuristicPlayer;
	
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
	 * Play the action for the current player
	 */
	public abstract void playAction(AbstractGameAction action);
	
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
	public abstract AbstractGameBoard getBoard();

	/**
	 * Return the heuristic player (set inside the actual game initialization)
	 * @return
	 */
	public int getHeuristicPlayer() {
		return heuristicPlayer;
	}
	
}
