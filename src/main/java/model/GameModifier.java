package model;

import java.util.Map;

/**
 * The interface that the controller sees of the HanabiGame model
 */
public interface GameModifier {
    /**
     * Enters a new game with a given identity
     * @param id      The Game ID of the new game to enter
     * @param token   The string token of the new game to enter
     * @param rainbow The String identifier for how rainbow cards are handled
     *                in the new game
     */
    void enterGame(int id, String token, String rainbow);

    /**
     * Updates the number of remaining Player slots in the game
     * @param numLeft The number of Player slots left in the game
     */
    void updatePlayersLeft(int numLeft);

    /**
     * Starts a game of Hanabi using the cards dealt out to each Player. Each
     * of 2-5 Players gets a hand with 5 cards (with 2-3 Players) or 4 cards
     * (with 4-5 Players). The Player running the Client will be given an
     * empty hand since they don’t know what cards they have at the start of
     * the game.
     * @param hands A String array with the hands of cards that the Players
     *              start with. Each card is 2 characters: one for the colour
     *              (r,b,g,y,w,m) and one for the rank (1,2,3,4,5).
     */
    void startGame(String[][] hands);

    /**
     * Makes the current Player play a card from their hand and replace it
     * with a drawn card.
     * @param handIndex The index of the card to play in
     *                    the current Player’s hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param pileColour The colour of the firework to play the card on, or
     *                   an empty string if a pile wasn't provided
     */
    void playCard(int handIndex, String drawColour, int drawRank,
                  String pileColour);

    /**
     * Informs the current Player of the rank and colour of a card they are
     * playing, then plays that card from their hand and replaces it with a
     * drawn card
     * @param handIndex The index of the card to play in the current Player’s
     *                 hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param pileColour The index of the pile in which to
     *                    play the card.
     * @param ownColour The colour of the Player's played card
     * @param ownRank The rank of the Player's played card
     */
    void playCard(int handIndex, String drawColour, int drawRank,
                     String pileColour, String ownColour, int ownRank);

    /**
     * Makes the current Player discard a card from their hand and replace it
     * with a drawn card
     * @param handIndex The index of the discarded card in the current
     *                  Player’s hand
     * @param drawColour: The colour of the Player's drawn card
     * @param drawRank: The rank of the Player's drawn card
     */
    void discardCard(int handIndex, String drawColour, int drawRank);

    /**
     * Informs the current Player of the rank and colour of a card they are
     * discarding, then discards that card from their hand and replaces it
     * with a drawn card
     * @param handIndex The index of the card to discard in the current
     *                  Player’s hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param ownColour The colour of the Player's discarded card
     * @param ownRank The rank of the Player's discarded card
     */
    void discardCard(int handIndex, String drawColour, int drawRank,
                        String ownColour, int ownRank);

    /**
     * Tells the Player running the client information about their hand that
     * matches the given property. This version handles the case of the
     * Client Player being given info.
     * @param property The property of the other Player’s hand to tell them
     *                 about.
     * @param info An array of which cards in the Client Player's hand have
     *             the given property
     */
    void giveInfo(String property, boolean[] info);

    /**
     * Tells the given Player information about their hand that matches the
     * given property. This version handles the case of a Player other than
     * the Client being given info.
     * @param property The property of the other Player’s hand to tell them
     *                 about
     * @param playerIndex The other Player's index
     */
    void giveInfo(int playerIndex, String property);

    /**
     * Gets a move for an AI Player based on the state of the game
     * @return A map of move properties and values: the move type, the hand
     * index (for plays and discards), the firework pile to play on (for
     * plays), a player index (for info), and a kind of property and property
     * value (for info)
     */
    Map<String, String> getAIMove();

    /**
     * Toggles the flag for making the log of game moves visible to the Player
     * running the Client.
     */
    void toggleLogView();

    /**
     * Toggles the flag for making the discarded cards visible to the Player
     * running the Client
     */
    void toggleDiscardView();

    /**
     * Ends the current game of Hanabi nad clears all of its state
     */
    void endGame();
}
