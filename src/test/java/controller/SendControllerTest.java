package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/* A JUnit5 test class for SendController. Due to a reliance on the config
file 'hanabisecret.txt' in the user's home directory, this assumes that this
exists and has a secret for 'abc123' of '1728096db9ec656636928eec315d026a'. */
class SendControllerTest {
    private static MockSendController test;
    private static ControllerData cd = ControllerData.getInstance();
    private static MockServerComm mockComm;
    private static MockHanabiGame mockGame;

    /*
    A Mock version of SendController that pairs with a MockReceiver instead
    of the normal ReceiveController
     */
    private class MockSendController extends SendController {
        public MockSendController(ControllerData cd, MessageSender sc) {
            super(cd, sc);
        }

        @Override
        protected MessageReceiver getMessageReceiver() {
            return new MockReceiver();
        }
    }

    @BeforeEach
    void setup() {
        mockComm = new MockServerComm();
        mockGame = new MockHanabiGame();
        cd.setGame(mockGame);
        test = new MockSendController(cd, mockComm);
    }

    @Test
    void tellCreateGameTest() {
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("numPlayers", "5");
        expectedMap.put("timeout", "60");
        expectedMap.put("force", "false");
        expectedMap.put("rainbow", "wild");
        expectedMap.put("nsid", "abc123");
        expectedMap.put("secret", "1728096db9ec656636928eec315d026a");

        test.tellCreateGame(5, 60, "wild", false, "abc123");
        assertIterableEquals(
          expectedMap.entrySet(), mockComm.sentValues.entrySet());
        assertEquals("abc123", cd.getNsid());
        assertEquals("wild", cd.getRainbow());
        assertTrue(mockComm.connected);
        assertTrue(mockComm.receiver instanceof MockReceiver);
    }

    @Test
    void tellJoinGameTest() {
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("id", "1");
        expectedMap.put("token", "sample");
        expectedMap.put("nsid", "abc123");
        expectedMap.put("secret", "1728096db9ec656636928eec315d026a");

        test.tellJoinGame(1, "sample", "abc123");
        assertIterableEquals(
          expectedMap.entrySet(), mockComm.sentValues.entrySet());
        assertEquals("abc123", cd.getNsid());
        assertEquals(1, cd.getGameID());
        assertEquals("sample", cd.getToken());
        assertTrue(mockComm.connected);
        assertTrue(mockComm.receiver instanceof MockReceiver);
    }

    @Test
    void tellDiscardCardTest() {
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("handIndex", "3");

        test.tellDiscardCard(3);
        assertIterableEquals(
          expectedMap.entrySet(), mockComm.sentValues.entrySet());
        assertEquals(3, cd.getLastMoveIndex());
    }

    @Test
    void tellPlayCardTest() {
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("handIndex", "3");
        expectedMap.put("colour", "w");

        test.tellPlayCard(3, "w");
        assertIterableEquals(
          expectedMap.entrySet(), mockComm.sentValues.entrySet());
        assertEquals(3, cd.getLastMoveIndex());
        assertEquals("w", cd.getLastPileColour());
    }

    @Test
    void tellGiveInfoTest() {
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("playerIndex", "3");
        expectedMap.put("property", "5");

        test.tellGiveInfo(3, "5");
        assertIterableEquals(
          expectedMap.entrySet(), mockComm.sentValues.entrySet());
        assertEquals(3, cd.getLastMoveIndex());
        assertEquals("5", cd.getLastMoveProperty());
    }

    @Test
    void tellToggleDiscardTest() {
        test.tellToggleDiscard();
        assertTrue(mockGame.viewToggle);
    }

    @Test
    void tellToggleLogTest() {
        test.tellToggleLog();
        assertTrue(mockGame.logToggle);
    }

    @Test
    void tellLeaveGameTest() {
        test.tellLeaveGame();
        assertTrue(mockGame.endOccurred);
    }
}