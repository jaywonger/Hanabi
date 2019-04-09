package controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerCommTest {
    private static ServerComm client;
    private String receivedMessage;

    @BeforeAll
    static void setUpAll() {
        // Make a new serverComm and point it to localhost
        client = new ServerComm();
        try {
            client.setServerName(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            fail("Could not get localhost");
        }

        //Start up a mock echo Server at localhost in a new thread
        Thread t = new Thread(new MockEchoServer());
        t.start();
    }

    @BeforeEach
    void setUp() {
        //Nullify any previous received messages
        receivedMessage = null;
    }

    @Test
    void connectionTest() {
        //Try to connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }
        assertTrue(client.socket.isConnected());

        //Test the connection to the Server by sending a receiving a message
        String testString = "Hello World!";
        client.sendTest(testString);
        try {
            receivedMessage = client.receiveTest();
            assertEquals(testString, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Test that the connection can be destroyed
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
        assertTrue(client.socket.isClosed());
    }

    @Test
    void sendCreateTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send a create message and receive the echoed version that the
        // server saw
        String secret = "1728096db9ec656636928eec315d026a";
        client.sendCreate(4, 60, false, "none", "abc123", secret);
        try {
            receivedMessage = client.receiveTest();
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Grab the MD5 hash in the echoed message
        int hashStart = receivedMessage.lastIndexOf(":\"") + 2;
        int hashEnd = receivedMessage.lastIndexOf("\"}");
        String computedHash = receivedMessage.substring(hashStart, hashEnd);

        //Replace the MD5 hash in the echoed message, recalculate the
        // hash and check that the hashes match
        String rehashMessage = receivedMessage.replaceFirst(
          "\"md5hash\":\"[0-9a-f]*\"", "\"md5hash\":\""+secret+"\"");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(rehashMessage.getBytes());
            String recomputedHash = new BigInteger(1,md.digest()).toString(16);
            assertEquals(computedHash, recomputedHash);
        } catch (NoSuchAlgorithmException e) {
            fail("Couldn't get MD5 hash algorithm to check hashing");
        }

        //Check that the echoed message (less the timestamp and md5hash
        // fields) is correct
        int signIndex = receivedMessage.indexOf(",\"timestamp\":");
        String nonStampedMessage = receivedMessage.substring(0,signIndex);
        String expectedMessage = "{\"cmd\":\"create\",\"nsid\":\"abc123\"" +
          ",\"players\":4,\"timeout\":60,\"force\":false" +
          ",\"rainbow\":\"none\"";
        assertEquals(expectedMessage, nonStampedMessage);

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void sendJoinTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send a join message and receive the echoed version that the
        // server saw
        String secret = "1728096db9ec656636928eec315d026a";
        client.sendJoin(2341, "somethingSecret", "def456", secret);
        try {
            receivedMessage = client.receiveTest();
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Grab the MD5 hash in the echoed message
        int hashStart = receivedMessage.lastIndexOf(":\"") + 2;
        int hashEnd = receivedMessage.lastIndexOf("\"}");
        String computedHash = receivedMessage.substring(hashStart, hashEnd);

        //Replace the MD5 hash in the echoed message, recalculate the
        // hash and check that the hashes match
        String rehashMessage = receivedMessage.replaceFirst(
          "\"md5hash\":\"[0-9a-f]*\"", "\"md5hash\":\""+secret+"\"");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(rehashMessage.getBytes());
            String recomputedHash = new BigInteger(1,md.digest()).toString(16);
            assertEquals(computedHash, recomputedHash);
        } catch (NoSuchAlgorithmException e) {
            fail("Couldn't get MD5 hash algorithm to check hashing");
        }

        //Check that the echoed message (less the timestamp and md5hash
        // fields) is correct
        int signIndex = receivedMessage.indexOf(",\"timestamp\":");
        String nonStampedMessage = receivedMessage.substring(0,signIndex);
        String expectedMessage = "{\"cmd\":\"join\",\"nsid\":\"def456\"" +
          ",\"game-id\":2341,\"token\":\"somethingSecret\"";
        assertEquals(expectedMessage, nonStampedMessage);

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void sendDiscardTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send a discard message and check that the correct message was sent
        String expectedMessage = "{\"action\":\"discard\",\"position\":3}";
        client.sendDiscard(3);
        try {
            receivedMessage = client.receiveTest();
            assertEquals(expectedMessage, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void sendPlayTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send a play message with no firework specified and check that the
        // correct message was sent
        String expectedMessage = "{\"action\":\"play\",\"position\":3}";
        client.sendPlay(3, "");
        try {
            receivedMessage = client.receiveTest();
            assertEquals(expectedMessage, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Send a play message with a firework specified and check that the
        // correct message was sent
        expectedMessage = "{\"action\":\"play\",\"position\":3," +
          "\"firework\":\"r\"}";
        client.sendPlay(3, "r");
        try {
            receivedMessage = client.receiveTest();
            assertEquals(expectedMessage, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void sendInfoTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send an inform message with a rank specified and check that the
        // correct message was sent
        String expectedMessage = "{\"action\":\"inform\",\"player\":3," +
          "\"rank\":2}";
        client.sendInfo(3, "2");
        try {
            receivedMessage = client.receiveTest();
            assertEquals(expectedMessage, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Send an inform message with a suit specified and check that the
        // correct message was sent
        expectedMessage = "{\"action\":\"inform\",\"player\":3," +
          "\"suit\":\"r\"}";
        client.sendInfo(3, "r");
        try {
            receivedMessage = client.receiveTest();
            assertEquals(expectedMessage, receivedMessage);
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void receiveTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Send a discard message and check that the echoed message can be
        // received properly
        Map<String, String> receivedMap;
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("action", "discard");
        expectedMap.put("position", "3");
        client.sendDiscard(3);
        try {
            receivedMap = client.receiveMessage();
            assertIterableEquals(expectedMap.entrySet(),
              receivedMap.entrySet());
        } catch (IOException e) {
            fail("Failed to get server response.");
        }

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    @Test
    void autonomousReceiveTest() {
        //Connect to the mock Server
        try {
            client.connect();
        } catch (IOException e) {
            fail("Failed to make connection");
        }

        //Create a mock receiver and have it receive from the Client's
        // connection to the mock server
        MockReceiver receiver = new MockReceiver();
        client.startReceiving(receiver);

        //Send a message and check that it is autonomously received by the
        // mock receiver
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("action", "discard");
        expectedMap.put("position", "3");
        client.sendDiscard(3);
        waitForEchoMessage(receiver);
        Map<String, String> actualMap = receiver.getReceivedMessage();
        assertIterableEquals(expectedMap.entrySet(), actualMap.entrySet());

        //Disconnect from the mock Server
        try {
            client.disconnect();
        } catch (IOException e) {
            fail("Failed to destroy connection");
        }
    }

    private void waitForEchoMessage(MockReceiver receiver) {
        while (! receiver.getNewMessageFlag()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("ServerReceiverTest wait interrupted");
            }
        }
    }
}