package controller;

// THIS WHOLE THING MIGHT NOT BE NEEDED

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiveControllerTest {

    @Test
    void respondToCreateTest() {
        String json =
          "{ \"reply\":\"created\"," +
            "\"game-id\": 2341," +
            "\"token\" : \"somethingSecret\"}";
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        // Quickly check for the right contents
        assertEquals("2341", jsonMap.get("game-id"));
        assertEquals("somethingSecret", jsonMap.get("token"));

//     ReceiveController receiver = new ReceiveController();
//     receiver.respondToMessage(jsonMap);

        // TODO confirm right method is called
    }

    @Test
    void respondToPlayerJoinedTest() {
        String json =
          "{ \"reply\":\"joined\"," +
            "\"needed\": 3," +
            "\"timeout\": 60}";
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertEquals("3", jsonMap.get("needed"));
        assertEquals("60", jsonMap.get("timeout"));

        // ControllerData contData = new ControllerData();
//    ReceiveController receiver = new ReceiveController();
//    receiver.respondToMessage(jsonMap);
    }

    @Test
    void parseStartingHandTest() {
        String newHand = "[[b1,b2,b4,g1]" +
          ",[]" +
          ",[b1,b3,g1,g2]" +
          ",[b2,b4,g1,g3]" +
          "]";

        ControllerData contData = ControllerData.getInstance();
        MessageSender sender = null;
        ReceiveController receiver = new ReceiveController(contData, sender);

        String[][] startingHand = receiver.parseStartingHands(newHand);

        assertEquals("b1", startingHand[0][0]);
        assertEquals("", startingHand[1][2]);
        assertEquals("b3", startingHand[2][1]);
        assertEquals("g3", startingHand[3][3]);

        String[][] expected = { {"b1", "b2", "b4", "g1"}, {"", "", "", ""},
          {"b1", "b3", "g1", "g2"}, {"b2", "b4", "g1", "g3"} };

        assertArrayEquals(expected, startingHand);
    }
}
