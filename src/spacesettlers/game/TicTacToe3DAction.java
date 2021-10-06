package spacesettlers.game;

public class TicTacToe3DAction extends AbstractGameAction {
	int row, col, depth;

	public TicTacToe3DAction(int row, int col, int depth) {
		super();
		this.row = row;
		this.col = col;
		this.depth = depth;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getDepth() {
		return depth;
	}
	
}
