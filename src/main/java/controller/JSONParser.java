package controller;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Client's JSON Parser. Creates JSON messages from paired lists of
 * strings and parses JSON messages received from the Server into maps.
 */
public class JSONParser {

    public JSONParser() {}

    /**
     * Converts a list pair of name-value pairs to a JSON message string.
     * Values are assumed to each be a single value since all the messages to
     * send the the HanabiServer are simple pairs.
     * @param names A string array of the names in each pair
     * @param values A string array of the corresponding singular values in
     *               each pair
     * @return A string with the JSON message
     * @throws NullPointerException If either names or values is not given
     * @throws IllegalArgumentException If the number of names and values
     * don't match
     */
    public static String makeJSON(String[] names, String[] values) {
        if (names == null || values == null) {
            throw new NullPointerException("Names and values must be given");
        }
        if (names.length != values.length) {
            throw new IllegalArgumentException("Names and values should be " +
              "paired");
        }

        //Construct the top-level JSON object for the message, iterate over
        // the name-value pairs, and add them to the JSON message
        JsonObject messageObject = new JsonObject();
        for (int i = 0; i < names.length; i++) {
            //The sent values can be numbers, booleans, or strings so we need
            // to check for each case (using regular expressions). As well,
            // the md5hash value must always be a string.
            String name = names[i];
            String value = values[i];
            if (value.matches("^\\d+$") && ! name.equals("md5hash")) {
                //Add an integer name-value
                messageObject.addProperty(name, Integer.parseInt(value));
            } else if (value.matches("^(true)|(false)$")) {
                //Add a boolean name-value
                messageObject.addProperty(name, Boolean.parseBoolean(value));
            } else {
                //Add a string name-value
                messageObject.addProperty(name, value);
            }
        }

        //Turn the JsonObject into proper JSON and return it
        Gson gson = new Gson();
        return gson.toJson(messageObject);
    }

    /**
     * Converts a JSON message string to a map of string name-value pairs in
     * the message.
     * @param json A JSON message (as a string) to parse
     * @return A map of name-value pairs as strings, in the same order as the
     * message
     * @throws NullPointerException If no JSON message is given
     */
    public static Map<String, String> parseJSON(String json) {
        if (json == null) {
            throw new NullPointerException("JSON message must be given");
        }

        //Turn the JSON message string into a top-level object in a tree of
        // JsonElements
        JsonParser parser = new JsonParser();
        JsonObject jsonTree = parser.parse(json).getAsJsonObject();

        //Iterate over the name-value pairs in the JSON message and add them
        // to a map. Since JsonObject.entrySet() is used for the iterator,
        // the pairs should be parsed in order.
        Map<String,String> jsonMap = new LinkedHashMap<>();
        for (Map.Entry<String,JsonElement> pair : jsonTree.entrySet()) {
            String name = pair.getKey();
            JsonElement elem = pair.getValue();

            //We need to turn the value into a string while dealing with any
            // arrays that can show up
            String elemStr;
            if (! elem.isJsonArray()) {
                elemStr = elem.getAsString();
            } else {
                elemStr = JSONParser
                  .getJsonArrayAsString(elem.getAsJsonArray());
            }

            jsonMap.put(name, elemStr);
        }

        return jsonMap;
    }

    /**
     * Converts a JsonArray in GSON to a suitable string representation
     * @param arr A GSON JsonArray to convert to a string
     * @return The JsonArray's string representation
     */
    private static String getJsonArrayAsString(JsonArray arr) {
        ArrayList<String> arrStrings = new ArrayList<>();

        //Iterator over the elements of the array and add their string
        // representations to the ArrayList
        Iterator<JsonElement> arrIter = arr.iterator();
        while (arrIter.hasNext()) {
            JsonElement jElem = arrIter.next();
            if (jElem.isJsonArray()) {
                arrStrings.add(getJsonArrayAsString(jElem.getAsJsonArray()));
            } else {
                arrStrings.add(jElem.getAsString());
            }
        }

        //Use the ArrayList to form a single string representation of the
        // array and return it
        return
          "[" + String.join(",",arrStrings.toArray(new String[0])) + "]";
    }
}
