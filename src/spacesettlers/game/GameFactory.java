package spacesettlers.game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory class used to generate new games.  Randomly picks from the set of available games
 *
 * @author amy
 *
 */

public class GameFactory {
	/**
	 * @param lhs_player Player 1
	 * @param rhs_player Player 2
	 * @param random Random number generator used to choose a game randomly
	 * @return a game object for the randomly chosen game
	 */
	public static AbstractGame generateNewGame(final AbstractGameAgent lhs_player,
											   final AbstractGameAgent rhs_player, ThreadLocalRandom random) {
		int game_id = random.nextInt(2);
		if (game_id == 0) {
			System.out.println("Generating a 3D Tic Tac Toe game");
			return new TicTacToe3D(lhs_player, rhs_player);
		} else if (game_id == 1) {
			System.out.println("Generating a 2D Tic Tac Toe game");
			return new TicTacToe2D(lhs_player, rhs_player);
		} else {
			return new TicTacToe3D(lhs_player, rhs_player);
			// TODO: implement the next new game
			//return new Mancala(lhs_player, rhs_player);
		}
	}
}
