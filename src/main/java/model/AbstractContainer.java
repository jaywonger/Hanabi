package model;

import java.util.*;

/**
 * An representation of card piles in a game of Hanabi, including fireworks,
 * discards, and Player hands. Stores the cards as an array to make insertion
 * and deletion at arbitrary places simple while returning the pile of cards
 * as a List for easy iteration.
 */
public abstract class AbstractContainer {
    /**
     * The array of cards
     */
    protected Card[] cards;
    /**
     * The max amount of cards the pile can hold
     */
    private int capacity;
    /**
     * The current number of cards in the pile
     */
    protected int size;

    /**
     * Creates a new pile of cards with a given getSize
     * @param numCards The maximum getSize of the pile
     */
    public AbstractContainer(int numCards) {
        this.cards = new Card[numCards];
        this.capacity = numCards;
        this.size = 0;
    }

    /**
     * Returns the pile of cards as a list. This list will contain null
     * elements if the pile isn't full
     * @return The list of cards in the pile, with nulls for empty slots
     */
    public List<Card> getCards() {
        return new ArrayList<>(Arrays.asList(cards));
    }

    /**
     * Return the capacity of this pile
     * @return The pile's capacity
     */
    public int getCapacity(){
        return this.capacity;
    }

    /**
     * Return the size of the pile (i.e. the number of cards in the pile)
     * @return The pile's size
     */
    public int getSize(){
        return this.size;
    }

    /**
     * Adds a card to the pile
     * @param c The card to add to the pile
     * @throws IllegalStateException If the pile is full
     */
    public void addCard(Card c) {
        if (this.getSize() >= this.getCapacity()) {
            throw new IllegalStateException("Card pile is full");
        }

        //Finds the current top of the pile and adds the card there
        this.cards[size] = c;
        this.size++;
    }

    /** Removes all of the cards in the pile */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            cards[i] = null;
        }
        this.size = 0;
    }
}


