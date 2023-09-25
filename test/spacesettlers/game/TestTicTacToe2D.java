package spacesettlers.game;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spacesettlers.actions.SpaceSettlersActionException;

import static org.junit.Assert.*;

/**
 * Ensure the Tic Tac Toe game 2D mechanics work
 * @author amy
 *
 */
public class TestTicTacToe2D {
	TicTacToe2D game;
	
	private static final class MockAbstractGameAgent extends AbstractGameAgent {
		@Override
		public AbstractGameAction getNextMove(AbstractGame game) {
			return null;
		}		
	}
	
	private static final MockAbstractGameAgent mockAgent = new MockAbstractGameAgent();
	
	@Before
	public void setUp() throws Exception {
		game = new TicTacToe2D(new MockAbstractGameAgent(), new MockAbstractGameAgent());
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
		int [][] board = new int[3][3];
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		
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
		int [][] board = new int[3][3];
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		
		// a board with a few scattered moves should not be over
		board[0][0] = 1;
		board[0][1] = 2;
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		// an empty board should not be over
		assertFalse(game.isGameOver());
	}

	/**
	 * test the tie situation (no one wins but board is empty)
	 *
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testTieBoard() {
		int [][] board = new int[3][3];
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);

		// a board with no winner
		// 1 2 1
		// 2 1 2
		// 2 1 2
		board[0][0] = 1;
		board[0][1] = 2;
		board[0][2] = 1;
		board[1][0] = 2;
		board[1][1] = 1;
		board[1][2] = 2;
		board[2][0] = 2;
		board[2][1] = 1;
		board[2][2] = 2;
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		// an empty board should not be over
		assertTrue(game.isGameOver());
	}


	/**
	 * test the game over class with a winner across rows
	 * 
	 * @throws SpaceSettlersActionException
	 */
	@Test
	public void testIsGameOverWinnerRow2D() {
		int [][] board = new int[3][3];

		for (int row = 0; row < 3; row++) {
			board[row][0] = 1;
			board[row][1] = 1;
			board[row][2] = 1;
			game = new TicTacToe2D(board, true, mockAgent, mockAgent);
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
		int [][] board = new int[3][3];

		for (int col = 0; col < 3; col++){
			board[0][col] = 1;
			board[1][col] = 1;
			board[2][col] = 1;
			game = new TicTacToe2D(board, true, mockAgent, mockAgent);
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
		int [][] board = new int[3][3];

		board[0][0] = 1;
		board[1][1] = 1;
		board[2][2] = 1;
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		assertTrue(game.isGameOver());

		board = new int[3][3];
		board[0][2] = 1;
		board[1][1] = 1;
		board[2][0] = 1;
		game = new TicTacToe2D(board, true, mockAgent, mockAgent);
		assertTrue(game.isGameOver());
	}
	

	
	
}