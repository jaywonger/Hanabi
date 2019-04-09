package controller;

import java.util.Map;

/**
 * A mock receiver that simply stores whatever message it is told to respond
 * to and makes it accessible to test classes
 */
public class MockReceiver implements MessageReceiver {
    private Map<String, String> receivedMessage;
    private boolean newMessageFlag = false;

    /**
     * Stores a JSON message that would normally be responded to
     * @param message The JSON message to store
     */
    @Override
    public synchronized void respondToMessage(Map<String, String> message) {
        receivedMessage = message;
        newMessageFlag = true;
    }

    /** Returns the last received message */
    public synchronized Map<String, String> getReceivedMessage() {
        newMessageFlag = false;
        return receivedMessage;
    }

    /** Returns the flag for whether or not the received message is new */
    public boolean getNewMessageFlag() {
        return  newMessageFlag;
    }
}
