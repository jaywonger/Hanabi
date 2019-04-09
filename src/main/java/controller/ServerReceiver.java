package controller;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

/**
 * Waits for messages to be sent from the server, then receives them and
 * passes them on to a controller to parse their content and react to them.
 * This class mainly exists to segregate the reading process into its own
 * thread to not block other parts of the Client.
 */
public class ServerReceiver implements Runnable {
    private ServerComm communicator;
    private MessageReceiver receiver;
    private boolean running = true;

    /**
     * Creates a new receiver that uses a given communicator to receive
     * messages and a given controller to react to them
     * @param sc The server communicator that does the work of receiving server
     *           messages
     * @param rc The controller that takes a received server message and
     *           reacts to it
     */
    public ServerReceiver(ServerComm sc, MessageReceiver rc) {
        communicator = sc;
        receiver = rc;
    }

    /**
     * Runs the ServerReceiver in an infinite loop of receiving messages from
     * the Server until the connection is closed
     */
    @Override
    public void run() {
        while (running) {
            try {
                //Block while waiting for a message to receive. When a
                // message is finally received, check that its an actual
                // message and pass it to the receiver for detailed handling.
                Map<String, String> message = communicator.receiveMessage();
                if (message != null) {
                    receiver.respondToMessage(message);
                } else {
                    //If the message is null, then the stream has ended and
                    // the communicator should disconnect from the server.
                    communicator.disconnect();
                    running = false;
                }
            } catch (SocketException e) {
                //This normally occurs when the communicator's socket is
                // closed in the middle of a read, which is a good enough way
                // of ending receiving
                running = false;
            } catch (IOException e) {
                System.err.println("Unexpected IOException in ServerReceiver");
            }
        }
    }
}
