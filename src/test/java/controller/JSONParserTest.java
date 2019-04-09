package controller;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JSONParserTest {
    @Test
    void makeSingleNumberTest(){
        String[] name = {"number"};
        String[] value = {"59"};
        String correctJson = "{\"number\":59}";
        String json = JSONParser.makeJSON(name, value);
        assertEquals(correctJson, json);
    }

    @Test
    void makeCreationMessageTest(){
        String[] names = {"cmd", "nsid", "players", "timeout", "force",
          "timestamp", "md5hash"};
        String[] values = {"create", "abc123", "4", "60", "false",
          "1549579464" , "1728096db9ec656636928eec315d026a"};
        String correctJson =
          "{\"cmd\":\"create\"," +
          "\"nsid\":\"abc123\"," +
          "\"players\":4," +
          "\"timeout\":60," +
          "\"force\":false," +
          "\"timestamp\":1549579464," +
          "\"md5hash\":\"1728096db9ec656636928eec315d026a\"}";
        String json = JSONParser.makeJSON(names, values);
        assertEquals(correctJson, json);
    }

    @Test
    void makeNumericHashTest(){
        String[] names = {"md5hash"};
        String[] values = {"1234567890"};
        String correctJson = "{\"md5hash\":\"1234567890\"}";
        String json = JSONParser.makeJSON(names, values);
        assertEquals(correctJson, json);
    }

    @Test
    void makeEmptyTest(){
        String[] names = {};
        String[] values = {};
        String correctJson = "{}";
        String json = JSONParser.makeJSON(names, values);
        assertEquals(correctJson, json);
    }

    @Test
    void makeNullFailTest(){
        String[] names = null;
        String[] values = {"7"};
        assertThrows(NullPointerException.class,
          () -> JSONParser.makeJSON(names, values));
    }

    @Test
    void makeMismatchFailTest(){
        String[] names = {"number"};
        String[] values = {"7", "false"};
        assertThrows(IllegalArgumentException.class,
          () -> JSONParser.makeJSON(names, values));
    }

    @Test
    void parseSingleNumberTest(){
        String json = "{\"integer\":34}";
        Map<String,String> correctMap = new LinkedHashMap<>();
        correctMap.put("integer", "34");
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertIterableEquals(correctMap.entrySet(), jsonMap.entrySet());
    }

    @Test
    void parseJoinMessageTest(){
        String json =
          "{ \"reply\":\"joined\"," +
          "\"needed\":3," +
          "\"timeout\" : 60}";
        Map<String,String> correctMap = new LinkedHashMap<>();
        correctMap.put("reply", "joined");
        correctMap.put("needed", "3");
        correctMap.put("timeout", "60");
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertIterableEquals(correctMap.entrySet(), jsonMap.entrySet());
    }

    @Test
    void parseStartGameMessageTest(){
        String json =
          "{\"notice\":\"game starts\"," +
            "\"hands\":" +
            "[[]" +
            ",[\"b1\",\"b3\",\"b5\",\"g2\"]" +
            ",[\"b1\",\"b3\",\"g1\",\"g2\"]" +
            ",[\"b2\",\"b4\",\"g1\",\"g3\"]]}";
        LinkedHashMap<String,String> correctMap = new LinkedHashMap<>();
        correctMap.put("notice", "game starts");
        correctMap.put("hands",
          "[[],[b1,b3,b5,g2],[b1,b3,g1,g2],[b2,b4,g1,g3]]");
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertIterableEquals(correctMap.entrySet(), jsonMap.entrySet());
    }

    @Test
    void parseGetInfoMessageTest(){
        String json = "{\"notice\":\"inform\"" +
          ",\"rank\":1" +
          ",\"cards\":[true,false,false,true]}";
        LinkedHashMap<String,String> correctMap = new LinkedHashMap<>();
        correctMap.put("notice", "inform");
        correctMap.put("rank", "1");
        correctMap.put("cards", "[true,false,false,true]");
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertIterableEquals(correctMap.entrySet(), jsonMap.entrySet());
    }

    @Test
    void parseEmptyTest(){
        String json = "{}";
        LinkedHashMap<String,String> correctMap = new LinkedHashMap<>();
        Map<String,String> jsonMap = JSONParser.parseJSON(json);
        assertIterableEquals(correctMap.entrySet(), jsonMap.entrySet());
    }

    @Test
    void parseNullFailTest(){
        String json = null;
        assertThrows(NullPointerException.class,
          () -> JSONParser.parseJSON(json));
    }
}