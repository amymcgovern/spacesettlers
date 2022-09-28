package spacesettlers.game;

/**
 * Factory class used to generate new games.  Eventually will randomly pick from 
 * the set of available games.
 * 
 * @author amy
 *
 */

public class GameFactory {
	public static TicTacToe3D generateNewGame(final TicTacToe3DGameAgent player_lhs, final TicTacToe3DGameAgent player_rhs) {
		return new TicTacToe3D(player_lhs, player_rhs);
	}
}
