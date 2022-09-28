package spacesettlers.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spacesettlers.actions.SpaceSettlersActionException;

/**
 * Ensure the Tic Tac Toe game mechanics work
 * @author amy
 *
 */
public class TestTicTacToe3D {
	TicTacToe3D game;
	
	@Before
	public void setUp() throws Exception {
		game = new TicTacToe3D(null, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test the game over class 
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverEmptyBoard() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);
		
		// an empty board should not be over
		assertFalse(game.isGameOver());
	}
	
	/**
	 * test the game over class with some scattered moves
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverScatteredBoard() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);
		
		// a board with a few scattered moves should not be over
		board[0][0][0] = 1;
		board[0][1][0] = 2;
		board[1][0][2] = 1;
		game = new TicTacToe3D(board, true, null, null);
		// an empty board should not be over
		assertFalse(game.isGameOver());
	}

	/**
	 * test the game over class with a winner across rows
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerRow2D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][0][dep] = 1;
			board[0][1][dep] = 1;
			board[0][2][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}
	}
	
	/**
	 * test the game over class with a winner across cols
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerCol2D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][0][dep] = 1;
			board[1][0][dep] = 1;
			board[2][0][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}
	}

	/**
	 * test the game over class with a winner across diagonals
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerDiagonal2D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][0][dep] = 1;
			board[1][1][dep] = 1;
			board[2][2][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][2][dep] = 1;
			board[1][1][dep] = 1;
			board[2][0][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}

	}
	
	/**
	 * test the game over class with a winner across rows in 3D
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerRow3D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][0][dep] = 1;
			board[1][0][dep] = 1;
			board[2][0][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}
	}
	
	/**
	 * test the game over class with a winner across cols in 3D
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerCol3D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		for (int dep = 0; dep < 3; dep++) {
			board = new int[3][3][3];
			board[0][0][dep] = 1;
			board[0][1][dep] = 1;
			board[0][2][dep] = 1;
			game = new TicTacToe3D(board, true, null, null);
			assertTrue(game.isGameOver());
		}
	}

	/**
	 * test the game over class with a winner across diagonals
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerDiagonal3D() {
		int [][][] board = new int[3][3][3];
		game = new TicTacToe3D(board, true, null, null);

		board[0][0][0] = 1;
		board[1][1][1] = 1;
		board[2][2][2] = 1;
		game = new TicTacToe3D(board, true, null, null);
		assertTrue(game.isGameOver());

		board = new int[3][3][3];
		board[2][0][0] = 1;
		board[1][1][1] = 1;
		board[0][2][2] = 1;
		game = new TicTacToe3D(board, true, null, null);
		assertTrue(game.isGameOver());

		board = new int[3][3][3];
		board[0][0][2] = 1;
		board[1][1][1] = 1;
		board[2][2][0] = 1;
		game = new TicTacToe3D(board, true, null, null);
		assertTrue(game.isGameOver());

		board = new int[3][3][3];
		board[0][2][0] = 1;
		board[1][1][1] = 1;
		board[2][0][2] = 1;
		game = new TicTacToe3D(board, true, null, null);
		assertTrue(game.isGameOver());

		
	}
	
	
}