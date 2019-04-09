package controller;

import model.GameModifier;

/**
 * A singleton data class that coordinates information between SendController
 * and ReceiveController. It holds common things like a reference to the
 * model, the current game's ID and token, the Client Player's NSID and
 * whether they are an AI or not, and move information for Client Player
 * moves that won't be echoed back by the Server.
 */
public class ControllerData {
    /** A reference to a game of Hanabi that can be modified */
    private GameModifier game;
    /** The gameID of the current game, needed for passing to AI Players */
    private int gameID;
    /** The token of the current game, needed for passing to AI Players */
    private String token;
    /** The NSID of the Client's Player, needed for passing to AI Players for
     * authenticating with the server */
    private String nsid;
    /** The tag for how to handle rainbows in a created game, needed because
     * the server doesn't echo this back */
    private String rainbow;
    /** A flag for whether or not the Client Player is an AI Player */
    private boolean isAI;
    /** An index (for either a Hand's card or a Player) for the Client
     * Player's most recent move, needed because the server doesn't echo this
     * back */
    private int lastMoveIndex;
    /** A string for the played pile colour of the Client Player's most
     * recent play move, needed because the server doesn't echo this back */
    private String lastPileColour;
    /** A string with the Card property of the Client Player's most recent
     * info move, needed because the server doesn't echo this back */
    private String lastMoveProperty;
    /** The single instance of the Controller data block */
    private static final ControllerData contData = new ControllerData();

    /** Private constructor method for the singleton instance of
     * ControllerData */
    private ControllerData () {}

    /**
     * Gets the singleton instance of the controller data block
     * @return The singleton instance of ControllerData
     */
    public static ControllerData getInstance() {
        return contData;
    }

    /** Gets the instance of the GameModifier */
    public GameModifier getGame() {
        return game;
    }

    /** Sets the instance of the GameModifier */
    public void setGame(GameModifier game) {
        this.game = game;
    }

    /** Gets the current gameID of this Client's connected game */
    public int getGameID() {
        return gameID;
    }

    /** Sets the current gameID of this Client's connected game */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /** Gets the current token of this Client's connected game */
    public String getToken() {
        return token;
    }

    /** Sets the current token of this Client's connected game */
    public void setToken(String token) {
        this.token = token;
    }

    /** Gets the NSID of this Client's Player */
    public String getNsid() {
        return nsid;
    }

    /** Sets the NSID of this Client's Player */
    public void setNsid(String nsid) {
        this.nsid = nsid;
    }

    /** Gets the rainbow tag value for created games */
    public String getRainbow() {
        return rainbow;
    }

    /** Sets the rainbow tag value for created games */
    public void setRainbow(String rainbow) {
        this.rainbow = rainbow;
    }

    /** Gets the flag for this Client representing an AI Player */
    public boolean isAI() {
        return isAI;
    }

    /** Sets the flag for this Client representing an AI Player */
    public void setAI(boolean AI) {
        isAI = AI;
    }

    /** Gets the Hand or Player index of the Client Player's most recent
     * play or discard move */
    public int getLastMoveIndex() {
        return lastMoveIndex;
    }

    /** Sets the Hand or Player index of the Client Player's most recent
     * play or discard move */
    public void setLastMoveIndex(int lastMoveIndex) {
        this.lastMoveIndex = lastMoveIndex;
    }

    /** Gets the played pile colour of the Client Player's most recent play
     * move */
    public String getLastPileColour() {
        return lastPileColour;
    }

    /** Sets the played pile colour of the Client Player's most recent play
     * move */
    public void setLastPileColour(String lastPileColour) {
        this.lastPileColour = lastPileColour;
    }

    /** Gets the string property of the Client Player's most recent info move */
    public String getLastMoveProperty() {
        return lastMoveProperty;
    }

    /** Sets the string property of the Client Player's most recent info move */
    public void setLastMoveProperty(String lastMoveProperty) {
        this.lastMoveProperty = lastMoveProperty;
    }
}
