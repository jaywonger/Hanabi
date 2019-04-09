package controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A mock version of ServerComm that just records whether functions were
 * called and what the inputs were
 */
public class MockServerComm implements MessageSender {
    boolean connected = false;
    MessageReceiver receiver = null;
    Map<String, String> sentValues = new LinkedHashMap<>();

    @Override
    public void connect() throws IOException {
        connected = true;
    }

    @Override
    public void disconnect() throws IOException {
        connected = false;
    }

    @Override
    public void startReceiving(MessageReceiver mr) {
        receiver = mr;
    }

    @Override
    public void sendCreate(int numPlayers, int timeout, boolean force,
                           String rainbow, String nsid, String secret) {
        sentValues.put("numPlayers", Integer.toString(numPlayers));
        sentValues.put("timeout", Integer.toString(timeout));
        sentValues.put("force", Boolean.toString(force));
        sentValues.put("rainbow", rainbow);
        sentValues.put("nsid", nsid);
        sentValues.put("secret", secret);
    }

    @Override
    public void sendJoin(int id, String token, String nsid, String secret) {
        sentValues.put("id", Integer.toString(id));
        sentValues.put("token", token);
        sentValues.put("nsid", nsid);
        sentValues.put("secret", secret);
    }

    @Override
    public void sendDiscard(int handIndex) {
        sentValues.put("handIndex", Integer.toString(handIndex));
    }

    @Override
    public void sendPlay(int handIndex, String colour) {
        sentValues.put("handIndex", Integer.toString(handIndex));
        sentValues.put("colour", colour);
    }

    @Override
    public void sendInfo(int playerIndex, String property) {
        sentValues.put("playerIndex", Integer.toString(playerIndex));
        sentValues.put("property", property);
    }
}
