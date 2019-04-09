package controller;

import java.util.Map;

/**
 * An interface for controller classes that can respond to messages received
 * from the server.
 */
public interface MessageReceiver {
    /**
     * Interprets and responds to a JSON message from the server
     * @param message The JSON message to respond to, as a string map of
     *                name-value pairs
     */
    void respondToMessage(Map<String, String> message);
}
