package controller;

import java.io.IOException;

/**
 * An interface that specifies what functionality in a socket-based message
 * sender (like ServerComm) SendController should see
 */
public interface MessageSender {
    /**
     * Creates a connection to the Hanabi Server
     * @throws IOException If ServerComm fails to create a connection
     */
    void connect() throws IOException;

    /**
     * Destroys a connection to the Hanabi Server
     * @throws IOException If ServerComm fails to destroy the connection
     */
    void disconnect() throws IOException;

    /**
     * Starts autonomously receiving messages from the server and responding
     * to them with the given MessageReceiver. This continues until the server
     * connection is severed.
     * @param mr A MessageReceiver that will respond to any messages received
     *          by the ServerReceiver
     */
    void startReceiving(MessageReceiver mr);

    /**
     * Sends a JSON message to the server to create a game with the given
     * settings and verify the Player's identity
     * @param numPlayers The number of Player slots in the game
     * @param timeout The timeout length in seconds for the game
     * @param force A flag for whether to forcefully create the game if
     *              another one exists for the same NSID
     * @param rainbow A setting for whether to include rainbow/multicolour
     *                cards and how to handle them. "none" excludes them,
     *                "firework" makes them a 6th independent colour, and
     *                "wild" makes them wildcards that can be placed in any
     *                other pile. Giving an empty string will exclude this
     *                field in the message, implying the "none" option.
     * @param nsid A string of the Player's NSID
     * @param secret A string with the NSID's server secret for signing the
     *               message
     */
    void sendCreate(int numPlayers, int timeout, boolean force,
                    String rainbow, String nsid, String secret);

    /**
     * Sends a JSON message to the server to join a game specified by an id and
     * token and verify the Player's identity
     * @param id The numeric game-id of the game
     * @param token A string with the game's token
     * @param nsid A string of the Player's NSID
     * @param secret A string with the NSID's server secret for signing the
     *               message
     */
    void sendJoin(int id, String token, String nsid, String secret);

    /**
     * Sends a JSON message to the server to discard a given card from the
     * current Player's hand
     * @param handIndex The index of the discarded card in the current Player's
     *                  hand
     */
    void sendDiscard(int handIndex);

    /**
     * Sends a JSON message to the server to play a given card from the current
     * Player's hand. A optional specifier states which firework pile to play
     * it on, which is necessary to disambiguate rainbow/multicolour plays
     * @param handIndex The index of the played card in the current Player's
     *                  hand
     * @param colour The colour of the firework to play on as a character
     *               string (from "r", "b", "g", "y", "w", and "m"). The
     *               firework pile is not include in the message if this is
     *               an empty string.
     */
    void sendPlay(int handIndex, String colour);

    /**
     * Sends a JSON message to the server to inform another player of all their
     * cards with a given property
     * @param playerIndex The index of the other Player to give info to
     * @param property A character string with the property to give (from
     *                 "r", "b", "g", "y", "w", and "m" for colours or from
     *                 "1", "2", "3", "4", and "5" for ranks)
     */
    void sendInfo(int playerIndex, String property);
}
