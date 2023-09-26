package spacesettlers.game;

/**
 * Standard mancala game
 *  Rules described here
 *  https://www.scholastic.com/content/dam/teachers/blogs/alycia-zimmerman/migrated-files/mancala_rules.pdf
 * @author amy
 */
public class Mancala extends AbstractGame {
    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public boolean getTurn() {
        return false;
    }

    @Override
    public void playAction(AbstractGameAction action) {

    }

    @Override
    public int getWinner() {
        return 0;
    }

    @Override
    public AbstractGameBoard getBoard() {
        return null;
    }

    @Override
    public AbstractGameAgent getPlayer1() {
        return null;
    }

    @Override
    public AbstractGameAgent getPlayer2() {
        return null;
    }

    @Override
    public AbstractGameAgent getCurrentPlayer() {
        return null;
    }
}
