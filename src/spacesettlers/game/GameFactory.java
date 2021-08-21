package spacesettlers.game;

/**
 * Factory class used to generate new games.  Eventually will randomly pick from 
 * the set of available games.
 * 
 * @author amy
 *
 */

public class GameFactory {
	public static AbstractGame generateNewGame() {
		return new TicTacToe3D();
	}
}
