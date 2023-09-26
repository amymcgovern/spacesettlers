package spacesettlers.game;

import java.util.ArrayList;

/**
 * Mancala board is implemented as a linked list since it is easiest to send stones that way
 * @author amy
 */
public class MancalaBoard extends AbstractGameBoard {
    ArrayList<MancalaPit> board;

    public MancalaBoard() {
        board = new ArrayList<MancalaPit>();

        // add the first player's seed pits
        for (int i = 0; i < 6; i++){
            MancalaPit pit = new MancalaPit(AbstractGame.player1, false);
            board.add(pit);
        }
        // and their store
        MancalaPit pit = new MancalaPit(AbstractGame.player1, true);
        board.add(pit);

        // add the second player's seed pits
        for (int i = 0; i < 6; i++){
            pit = new MancalaPit(AbstractGame.player2, false);
            board.add(pit);
        }
        // and their store
        pit = new MancalaPit(AbstractGame.player2, true);
        board.add(pit);
    }
}
