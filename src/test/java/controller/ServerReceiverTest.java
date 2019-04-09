package controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerReceiverTest {
    private MockReceiver receiver;

    @BeforeEach
    void setup() {
        receiver = null;
    }

    @Test
    void receiveTest() {
        // Make a new serverComm and point it to localhost
        ServerComm client = new ServerComm();
        try {
            client.setServerName(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            fail("Could not get localhost");
        }

        //Start up a mock echo Server at localhost in a new thread
        MockEchoServer server = new MockEchoServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        //Connect to the mock server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Could not connect to mock server");
        }

        //Create a mock receiver and pass it and the client to a new
        // ServerReceiver in a new thread
        receiver = new MockReceiver();
        client.startReceiving(receiver);

        //Send a test discard message, get what the receiver got back, and
        // check that it is correct
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("action", "discard");
        expectedMap.put("position", "3");
        client.sendDiscard(3);
        waitForEchoMessage();
        Map<String, String> actualMap = receiver.getReceivedMessage();
        assertIterableEquals(expectedMap.entrySet(), actualMap.entrySet());

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }

        //Stop running the mock echo server
        try {
            server.stopRunning();
        } catch (IOException e) {
            fail("Couldn't stop mock echo server");
        }
    }

    @Test
    void detectDisconnectTest() {
        // Make a new serverComm and point it to localhost
        ServerComm client = new ServerComm();
        try {
            client.setServerName(InetAddress.getLocalHost().getHostAddress());
            client.setServerPort(10220);
        } catch (UnknownHostException e) {
            fail("Could not get localhost");
        }

        //Start up a mock message Server at localhost with 1 loaded message
        // in a new thread
        MockMessageServer server = new MockMessageServer();
        server.loadMessages(Arrays.asList("{\"last\":\"yes\"}"));
        Thread serverThread = new Thread(server);
        serverThread.start();

        //Connect to the mock server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Could not connect to mock server");
        }

        //Create a mock receiver and pass it and the client to a new
        // ServerReceiver in a new thread
        receiver = new MockReceiver();
        client.startReceiving(receiver);

        //Send a test discard message and get what the receiver got back
        client.sendDiscard(3);
        waitForEchoMessage();
        Map<String, String> actualMap = receiver.getReceivedMessage();

        //The mock server's socket should have closed after sending the one
        // message, causing ServerReceive to disconnect on the client end
        assertTrue(client.socket.isClosed());

        //Stop running the mock message server
        try {
            server.stopRunning();
        } catch (IOException e) {
            fail("Couldn't stop mock message server");
        }
    }

    private void waitForEchoMessage() {
        while (! receiver.getNewMessageFlag()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("ServerReceiverTest wait interrupted");
            }
        }
    }
}