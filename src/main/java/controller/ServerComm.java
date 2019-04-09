package controller;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;

/**
 * Handles communication with the Server for the Client. This involves
 * creating and maintaining a connection to the server, sending messages to
 * it, and noticing and receiving messages from it.
 */
public class ServerComm implements MessageSender {
    private String serverName = "gpu2.usask.ca";
    private int serverPort = 10219;
    Socket socket;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private ServerReceiver receiver;

    /**
     * Creates a connection to the Hanabi Server
     * @throws IOException If ServerComm fails to create a connection
     */
    public void connect() throws IOException {
        socket = new Socket(serverName, serverPort);
        serverInput =
          new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverOutput = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Destroys a connection to the Hanabi Server
     * @throws IOException If ServerComm fails to destroy the connection
     */
    public void disconnect() throws IOException {
        //Close the output stream first in case another thread is blocking on
        // reading from the input stream. This should cause the read to end
        // from either an EOS signal or a caught SocketException. Any running
        // ServerReceiver should also end on its own so that we can safely
        // remove the reference to it.
        serverOutput.close();
        if (receiver != null) {
            receiver = null;
        }
    }

    /**
     * Starts autonomously receiving messages from the server and responding
     * to them with the given MessageReceiver. This continues until the server
     * connection is severed.
     * @param mr A MessageReceiver that will respond to any messages received
     *          by the ServerReceiver
     * @throws IllegalStateException If a ServerReceiver is already running
     */
    public void startReceiving(MessageReceiver mr) {
        if (receiver != null) {
            throw new IllegalStateException("A receiver is already running; " +
              "close the connection first");
        }
        receiver = new ServerReceiver(this, mr);
        Thread t = new Thread(receiver);
        t.start();
    }

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
    public void sendCreate(int numPlayers, int timeout, boolean force,
                           String rainbow, String nsid, String secret) {
        //Create the initial unhashed message, using the current Unix Epoch
        // time for a timestamp
        int currentTime = (int) Instant.now().getEpochSecond();
        String[] names = {"cmd", "nsid", "players", "timeout", "force",
          "rainbow", "timestamp", "md5hash"};
        String[] values = {"create", nsid, Integer.toString(numPlayers),
          Integer.toString(timeout), Boolean.toString(force), rainbow,
          Integer.toString(currentTime), secret};
        String initialMessage = JSONParser.makeJSON(names, values);

        //Take the MD5 hash of the initial message and reconstruct the
        // message using the hash
        String hash = computeHash(initialMessage);
        values[7] = hash;
        String message = JSONParser.makeJSON(names, values);
        serverOutput.println(message);
    }

    /**
     * Sends a JSON message to the server to join a game specified by an id and
     * token and verify the Player's identity
     * @param id The numeric game-id of the game
     * @param token A string with the game's token
     * @param nsid A string of the Player's NSID
     * @param secret A string with the NSID's server secret for signing the
     *               message
     */
    public void sendJoin(int id, String token, String nsid, String secret) {
        //Create the initial unhashed message, using the current Unix Epoch
        // time for a timestamp
        int currentTime = (int) Instant.now().getEpochSecond();
        String[] names = {"cmd", "nsid", "game-id", "token", "timestamp",
          "md5hash"};
        String[] values = {"join", nsid, Integer.toString(id), token,
          Integer.toString(currentTime), secret};
        String initialMessage = JSONParser.makeJSON(names, values);

        //Take the MD5 hash of the initial message and reconstruct the
        // message using the hash
        String hash = computeHash(initialMessage);
        values[5] = hash;
        String message = JSONParser.makeJSON(names, values);
        serverOutput.println(message);
    }

    /**
     * Sends a JSON message to the server to discard a given card from the
     * current Player's hand
     * @param handIndex The index of the discarded card in the current Player's
     *                  hand
     */
    public void sendDiscard(int handIndex) {
        String[] names = {"action", "position"};
        String[] values = {"discard", Integer.toString(handIndex)};
        String message = JSONParser.makeJSON(names, values);
        serverOutput.println(message);
    }

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
    public void sendPlay(int handIndex, String colour) {
        String message;
        if (colour.equals("")) {
            String[] names = {"action", "position"};
            String[] values = {"play", Integer.toString(handIndex)};
            message = JSONParser.makeJSON(names, values);
        } else {
            String[] names = {"action", "position", "firework"};
            String[] values = {"play", Integer.toString(handIndex), colour};
            message = JSONParser.makeJSON(names, values);
        }
        serverOutput.println(message);
    }

    /**
     * Sends a JSON message to the server to inform another player of all their
     * cards with a given property
     * @param playerIndex The index of the other Player to give info to
     * @param property A character string with the property to give (from
     *                 "r", "b", "g", "y", "w", and "m" for colours or from
     *                 "1", "2", "3", "4", and "5" for ranks)
     */
    public void sendInfo(int playerIndex, String property) {
        String message;
        if (property.matches("^[1-5]$")) {
            String[] names = {"action", "player", "rank"};
            String[] values = {"inform", Integer.toString(playerIndex),
              property};
            message = JSONParser.makeJSON(names, values);
        } else {
            String[] names = {"action", "player", "suit"};
            String[] values = {"inform", Integer.toString(playerIndex),
              property};
            message = JSONParser.makeJSON(names, values);
        }
        serverOutput.println(message);
    }

    /**
     * Receives a JSON message from the server. Blocks until a message is
     * available to read.
     * @return A String to String map with the name-value pairs of the received
     * message, in the same order as in the message. This is instead null if
     * the input stream ends.
     * @throws IOException If ServerComm fails to receive a message
     */
    public Map<String, String> receiveMessage() throws IOException {
        String message = serverInput.readLine();
        if (message != null) {
            return JSONParser.parseJSON(message);
        } else {
            return null;
        }
    }

    /**
     * Sets the name of the server to connect to. The default server is
     * "gpu2.usask.ca"
     * @param server The name of the server to connect to
     */
    public void setServerName(String server) {
        serverName = server;
    }

    /**
     * Sets the port of the server to connect to. The default port is 10219
     * @param port The port of the server to connect to
     */
    public void setServerPort(int port) {
        serverPort = port;
    }

    /**
     * A test method to send a raw string message to a server
     * @param message The string message to send
     */
    void sendTest(String message) {
        serverOutput.println(message);
    }

    /**
     * A test method to receive a raw string message from a server. Blocks
     * until a message is available to read.
     * @return The string message received from the server
     * @throws IOException If ServerComm fails to read a received message
     */
    String receiveTest() throws IOException {
        return serverInput.readLine();
    }

    /**
     * Computes the MD5 hash of a create or join message
     * @param message The message to compute the MD5 hash for
     * @return The message's MD5 hash as a string, or an empty string if the
     * MD5 hashing algorithm can't be found
     */
    private String computeHash(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
