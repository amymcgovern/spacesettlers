package spacesettlers.game;

/**
 * Abstract class for game agents
 * @author amy
 *
 */
public abstract class AbstractGameAgent<T extends AbstractGameBoard, U extends AbstractGameAction> {
	int player;

	/**
	 * All game agents must be able to return a next move
	 * @param board
	 * @return
	 */
	public abstract U getNextMove(T board);

	
	public int getPlayerID() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}
	

}
