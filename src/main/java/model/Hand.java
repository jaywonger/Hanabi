package model;

/**
 * Represents a Player's hand of cards. Cards are inserted in and removed from
 * specific positions in a hand as a game progresses.
 */
public class Hand extends AbstractContainer {

    /**
     * Creates a new Hand with a given getSize
     * @param numCards The size of the hand
     */
    public Hand(int numCards) {
        super(numCards);
    }

    /**
     * Returns a card from the hand at a given index
     * @param i The index in the hand to get the cards from. Ranges from 1-5
     * @return The card at that position, which may be null
     * @throws ArrayIndexOutOfBoundsException If the index doesn't specify a
     * valid location (between 1 and getSize)
     */
    public Card getCard(int i) {
        if (i > this.getSize() || i <= 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid index");
        }
        return cards[i - 1];
    }

    /**
     * Removes a card from the hand from a given index
     * @param i The index in the hand to remove the card from
     * @throws IllegalStateException If the position already doesn't have a card
     */
    public void removeCard(int i) {
        if (this.cards[i - 1] == null) {
            throw new IllegalStateException("Position " + i + " is already" +
                    " empty");
        }
        this.cards[i - 1] = null;
        this.size--;

    }

    /**
     * Adds a new card to the hand at a given index
     * @param c The new card to add to the hand
     * @param i The index in the hand to place the new card in
     * @throws IllegalStateException If there is already a card in that position
     */
    public void addCard(Card c, int i) {
        if (this.cards[i-1] != null) {
            throw new IllegalStateException("Position " + i + " already has a" +
                    " card");
        }
        this.cards[i - 1] = c;
        this.size++;
    }
}
