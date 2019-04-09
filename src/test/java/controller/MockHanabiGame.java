package controller;

import model.GameModifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MockHanabiGame implements GameModifier {
    boolean viewToggle = false;
    boolean logToggle = false;
    boolean endOccurred = false;
    boolean moveGotten = false;
    Map<String, String> inputs = new LinkedHashMap<>();

    @Override
    public void enterGame(int id, String token, String rainbow) {
        inputs.put("id", Integer.toString(id));
        inputs.put("token", token);
        inputs.put("rainbow", rainbow);
    }

    @Override
    public void updatePlayersLeft(int numLeft) {
        inputs.put("numLeft", Integer.toString(numLeft));
    }

    @Override
    public void startGame(String[][] hands) {
        inputs.put("hands", Arrays.toString(hands));
    }

    @Override
    public void playCard(int handIndex, String drawColour, int drawRank,
                         String pileColour) {
        inputs.put("handIndex", Integer.toString(handIndex));
        inputs.put("drawColour", drawColour);
        inputs.put("drawRank", Integer.toString(drawRank));
        inputs.put("pileColour", pileColour);
    }

    @Override
    public void playCard(int handIndex, String drawColour, int drawRank,
                         String pileColour, String ownColour, int ownRank) {
        playCard(handIndex, drawColour, drawRank, pileColour);
        inputs.put("ownColour", ownColour);
        inputs.put("ownRank", Integer.toString(ownRank));
    }

    @Override
    public void discardCard(int handIndex, String drawColour, int drawRank) {
        inputs.put("handIndex", Integer.toString(handIndex));
        inputs.put("drawColour", drawColour);
        inputs.put("drawRank", Integer.toString(drawRank));
    }

    @Override
    public void discardCard(int handIndex, String drawColour, int drawRank,
                            String ownColour, int ownRank) {
        discardCard(handIndex, drawColour, drawRank);
        inputs.put("ownColour", ownColour);
        inputs.put("ownRank", Integer.toString(ownRank));
    }

    @Override
    public void giveInfo(String property, boolean[] info) {
        inputs.put("property", property);
        inputs.put("info", Arrays.toString(info));
    }

    @Override
    public void giveInfo(int playerIndex, String property) {
        inputs.put("playerIndex", Integer.toString(playerIndex));
        inputs.put("property", property);
    }

    @Override
    public void toggleLogView() {
        logToggle = true;
    }

    @Override
    public void toggleDiscardView() {
        viewToggle = true;
    }

    @Override
    public void endGame() {
        endOccurred = true;
    }

    @Override
    public Map<String, String> getAIMove() {
        moveGotten = true;
        return null;
    }
}
