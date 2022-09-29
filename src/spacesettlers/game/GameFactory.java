package spacesettlers.game;

/**
 * Factory class used to generate new games.  Eventually will randomly pick from 
 * the set of available games.
 * 
 * @author amy
 *
 */

public class GameFactory {
	public static TicTacToe3D generateNewGame(final AbstractGameAgent lhs_player, final AbstractGameAgent rhs_player) {
		return new TicTacToe3D(lhs_player, rhs_player);
	}
}
