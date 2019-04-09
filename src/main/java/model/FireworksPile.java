package model;

import java.util.Collections;
import java.util.List;

/**
 * A FireworksPile stores a firework in Hanabi, which is a mono-colour pile in 
 * increasing numeric order from 1-5. Cards are added to it as they are 
 * successfully played while the pile is cleared at the end of the game.
 */
public class FireworksPile extends AbstractContainer {
    /**
     * The color of the pile
     */
    private String colour;

    /**
     * Creates a new empty fireworks pile of a given colour
     * @param colour The colour of cards that go in this firework pile
     */
    public FireworksPile(String colour) {
        super(5); //All fireworks piles have 5 cards
        this.colour = colour;
    }

    /** Return the colour of the cards in a firework pile */
    public String getColour() {
        return this.colour;
    }

    /**
     * Returns the list of cards in the firework pile. There should be no null
     * elements for empty top elements in the pile.
     * @return The list of cards currently in the firework pile
     */
    @Override
    public List<Card> getCards() {
        List<Card> lc = super.getCards();
        lc.removeAll(Collections.singletonList(null));
        return lc;
    }

    /**
     * Adds a card to the pile and updates its getSize
     * @param c The card to add to the pile
     * @throws IllegalStateException If the pile is full
     * @throws IllegalArgumentException If the card is not the next numerical
     * card in the pile
     */
    @Override
    public void addCard(Card c) {
        if (c.getRank() != this.getSize() + 1
          || !this.getColour().equals(c.getColour()) && !c.getColour().equals("m")) {
            throw new IllegalArgumentException("Wrong colour or rank to be " +
              "the next card in the pile");
        }
        super.addCard(c);
    }
}
