package spacesettlers.game;

public class TicTacToe2DAction extends AbstractGameAction {
	int row, col;

	public TicTacToe2DAction(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
