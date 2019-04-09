package model;

/**
 * Stores a record of a move in a game of Hanabi. This includes what Player
 * made the move, what type of move it was, and the parameters of the
 * move. Parameters include either the hand index and values of a card for
 * plays and discards, or another Player index and the property that was
 * mentioned for info giving.
 */
public class Action {
    private int actingPlayer;
    private String moveType;
    private String[] moveParams;

    /**
     * Creates a new Action where a given Player made a move with given
     * parameters
     * @param actingPlayer The index of the player that took the action
     * @param moveType The type of move that was made
     * @param moveParams A String array of the parameters for the move
     */
    public Action (int actingPlayer, String moveType, String[] moveParams) {
        this.actingPlayer = actingPlayer;
        this.moveType = moveType;
        this.moveParams = moveParams;
    }

    /** Returns the index of the Player that performed the action. */
    public int getPlayer() {
        return this.actingPlayer;
    }

    /** Returns the type of move that was made. */
    public String getMoveType() {
        return this.moveType;
    }

    /** Returns the parameters of a move. */
    public String[] getMoveParams() {
        return this.moveParams;
    }
}
