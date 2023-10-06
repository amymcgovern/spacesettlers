package spacesettlers.game;

/**
 * A pit in the mancala board (did a separate class since this is stored as a linked list in the board)
 * @author amy
 */
public class MancalaPit {
    int player;
    boolean isStore;
    int numStones;

    public MancalaPit(int player, boolean isStore) {
        this.player = player;
        this.isStore = isStore;
        if (isStore) {
            numStones = 0;
        } else {
            numStones = 4;
        }
    }

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean store) {
        isStore = store;
    }

    public int getNumStones() {
        return numStones;
    }

    public void setNumStones(int numStones) {
        this.numStones = numStones;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }
}
