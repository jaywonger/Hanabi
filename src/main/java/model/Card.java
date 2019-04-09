package model;

/**
 * Represents a single card in a game of Hanabi. Has its own rank and colour
 * and maintains flags about whether the Client's Player knows those
 * properties.
 */
public class Card {
    private int rank;
    private String colour;
    private boolean rankKnown;
    private boolean colourKnown;

    /**
     * Creates a new card with a given rank, colour, and information flags
     * @param rank The card's rank (from 1-5)
     * @param colour The card's colour (from 'r', 'b', 'g', 'w', 'y', and 'm')
     * @param rankKnown A flag for whether the rank is known
     * @param colourKnown A flag for whether the colour is known
     */
    public Card(int rank, String colour, boolean rankKnown,
                boolean colourKnown) {
        this.rank = rank;
        this.colour = colour;
        this.rankKnown = rankKnown;
        this.colourKnown = colourKnown;
    }

    /** Returns the numeric rank of the card. */
    public int getRank() {
        return rank;
    }

    /** Returns the colour of the card. */
    public String getColour() {
        return colour;
    }

    /**
     * Returns the info known to the player holding the card
     * @return A boolean array with the information flags. The first bool is a
     * flag for the rank and the second bool is a flag for the colour.
     */
    public boolean[] getInfo() {
        boolean[] array = new boolean[2];
        array[0] = rankKnown;
        array[1] = colourKnown;

        return array;
    }

    /**
     * Determines if another card is equal to this card in a game of Hanabi
     * (i.e. has the same colour and rank)
     * @param c The card to compare colours and ranks with
     * @return True if their colors and ranks are the same, False otherwise
     */
    @Override
    public boolean equals(Object c) {
        if (! (c instanceof Card)) {
            return false;
        }

        boolean sameColour = sameColour((Card) c);
        boolean sameRank = sameRank((Card) c);
        return sameColour && sameRank;
    }

    /**
     * Determines if another card has the same colour as this card
     * @param c The card to compare colours with
     * @return True if their colours are the same, False otherwise
     */
    public boolean sameColour(Card c) {
        return this.colour.equals(c.getColour());
    }

    /**
     * Determines if another card has the same rank as this card
     * @param c The card to compare ranks with
     * @return True if their ranks are the same, False otherwise
     */
    public boolean sameRank(Card c) {
        return this.rank == c.getRank();
    }
}
