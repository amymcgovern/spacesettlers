package spacesettlers.game;

/**
 * Abstract class for game agents
 * @author amy
 *
 */
public abstract class AbstractGameAgent {
	int player;

	/**
	 * All game agents must be able to return a next move
	 * @param game
	 * @return
	 */
	public abstract AbstractGameAction getNextMove(AbstractGame game);

	
	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}
	

}
