package spacesettlers.actions;

import spacesettlers.game.AbstractGame;
import spacesettlers.game.AbstractGameAction;

/**
 * TODO: to be implemented!  To be used by the minimax search project
 * 
 * @author amy
 *
 */
public abstract class AbstractGameSearchAction {

	/**
	 * Return the next move in the current game
	 * 
	 * @param game
	 * @return
	 */
	public abstract AbstractGameAction getNextMove(AbstractGame game);
	
}
