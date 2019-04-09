package model;

/**
 * A representation of a Player and their hand of cards
 */
public class Player {
    private Hand hand;

    /**
     * Creates a new Player with a given size hand
     * @param handSize The size of the Player's hand
     */
    public Player(int handSize){
        hand = new Hand(handSize);
    }

    /** Returns the player's hand of cards */
    public Hand getHand() {
        return this.hand;
    }
}
