package model;

import java.util.Collections;
import java.util.List;

/**
 * A representation of a pile of discarded cards in a game of Hanabi, which
 * grows in size as cards are discarded throughout the game.
 */
public class DiscardPile extends AbstractContainer {

    /**
     * Creates a new discard pile for a game with a given total number of cards
     * @param numCards The number of cards that can go in the discard pile
     */
    public DiscardPile(int numCards) {
        super(numCards);
    }

    /**
     * Returns the list of cards in the discard pile. There should be no null
     * elements for empty top elements in the pile.
     * @return The list of cards currently in the discard pile
     */
    @Override
    public List<Card> getCards() {
        List<Card> lc = super.getCards();
        lc.removeAll(Collections.singletonList(null));
        return lc;
    }
}
