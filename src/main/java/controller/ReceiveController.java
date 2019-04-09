package controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * A controller class that handles responds to and parses the components of
 * messages sent from the server.
 */
public class ReceiveController implements MessageReceiver {
    private final ControllerData contData;
    private final MessageSender sender;

    /**
     * Creates a new ReceiveController with access to a common block of
     * controller data
     * @param cd The block of controller data
     */
    public ReceiveController(ControllerData cd, MessageSender ms) {
        contData = cd;
        sender = ms;
    }

    /**
     * Identifies a server message from its parsed JSON map and delegates
     * its response to another controller method
     * @param message The string map of the JSON message's name-value pairs
     * @throws IllegalArgumentException If the kind of message isn't recognized
     */
    @Override
    public void respondToMessage(Map<String, String> message) {
        //Look at the first entry of the JSON name-pair mapping to determine
        // what to do with it. This would ideally use Message objects with a
        // visitor or strategy pattern, but we only have string maps for now.
        if (message.containsKey("reply")) {
            switch (message.get("reply")) {
                case "created":
                    respondToCreate(message);
                    break;
                case "joined":
                    respondToJoined(message);
                    break;
                case "extant":
                    respondToExtant(message);
                    break;
                case "no such game":
                    respondToJoinFailure();
                    break;
                case "game full":
                    respondToJoinFailure();
                    break;
                case "invalid":
                    respondToInvalid();
                    break;
                case "built":
                    respondToClientPlay(message);
                    break;
                case "burned":
                    respondToClientPlay(message);
                    break;
                case "accepted":
                    //If there are any more fields, then this is for a
                    // discard move from the Client. Otherwise, it is from an
                    // inform move
                    if (message.size() > 1) {
                        respondToClientDiscard(message);
                    } else {
                        respondToClientInform();
                    }
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown reply message received from server");
            }
        } else if (message.containsKey("notice")) {
            switch (message.get("notice")) {
                case "player joined":
                    respondToNewPlayerCount(message);
                    break;
                case "player left":
                    respondToNewPlayerCount(message);
                    break;
                case "game starts":
                    respondToGameStarts(message);
                    break;
                case "game cancelled":
                    respondToGameCancelled();
                    break;
                case "discarded":
                    respondToDiscarded(message);
                    break;
                case "played":
                    respondToPlayed(message);
                    break;
                case "your move":
                    respondToYourMove();
                    break;
                case "game ends":
                    respondToGameEnds();
                    break;
                case "inform":
                    respondToNoticeInform(message);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown notice message received from server");
            }
        }
    }

    /**
     * Responds to a 'created' message from the server and stores the
     * returned gameID and token in the controller data block. This allows
     * the Game Creator to join their own game once a subsequent 'joined'
     * message is received
     * @param message A string map of the message's name-value pairs
     */
    private void respondToCreate(Map<String, String> message) {
        int gameID = Integer.parseInt(message.get("game-id"));
        String token = message.get("token");
        synchronized (contData) {
            contData.setGameID(gameID);
            contData.setToken(token);
        }
    }

    /**
     * Responds to an 'extant' message from the server and closes the
     * connection to the server
     * @param message A string map of the Extant message's name-value pairs
     */
    private void respondToExtant(Map<String, String> message) {
        int gameID = Integer.parseInt(message.get("game-id"));
        String token = message.get("token");
        try {
            sender.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to disconnect from server in 'extant' " +
                    "message response");
        }
        //TODO Should we send an error message to the model and view?
    }

    /**
     * Responds to an 'joined' message from the server and enters the game.
     * This can be received either after creating a game or joining an
     * existing game
     * @param message A string map of the message's name-value pairs
     */
    private void respondToJoined(Map<String, String> message) {
        //In getting the values from the message, we have to get 'rainbow'
        // form the controller block if we are the Game Creator or from the
        // message if we are just joining an existing game
        int needed = Integer.parseInt(message.get("needed"));
        synchronized (contData) {
            String rainbow = (message.containsKey("rainbow"))
              ? message.get("rainbow")
              : contData.getRainbow();
            int id = contData.getGameID();
            String token = contData.getToken();
            contData.getGame().enterGame(id, token, rainbow);
            contData.getGame().updatePlayersLeft(needed);
        }
    }

    /**
     * Responds to 'no such game' and 'game full' messages from the server
     * and closes the connection to the server
     */
    private void respondToJoinFailure() {
        try {
            sender.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to disconnect from server in response " +
                    "to join failure");
        }
        //TODO Should we send an error message to the model and view?
    }

    /**
     * Responds to an 'invalid' message from the server and sends an error
     * message to the user. Maybe...
     */
    private void respondToInvalid() {
        //TODO Should we send an error message to the model and view?
    }

    /**
     * Responds to 'built' and 'burned' messages from the server and tells
     * the model to play the Client Player's card. The model handles the
     * distinction between 'built' and 'burned' variations of a play.
     * @param message A string map of the message's name-value pairs
     */
    private void respondToClientPlay(Map<String, String> message) {
        String cardPlayedInfo = message.get("card");
        String playedColour = Character.toString(cardPlayedInfo.charAt(0));
        int playedRank = Integer.parseInt(
                Character.toString(cardPlayedInfo.charAt(1)));

        //Check whether a card was drawn
        boolean replaced = Boolean.parseBoolean(message.get("replaced"));
        String drawColour = replaced ? "" : null;
        int drawRank = replaced ? 0 : -1;

        synchronized (contData) {
            int handIndex = contData.getLastMoveIndex();
            String pileColour = contData.getLastPileColour();
            contData.getGame().playCard(handIndex, drawColour, drawRank,
              pileColour, playedColour, playedRank);
        }
    }

    /**
     * Responds to an 'accepted' message from the server accepting the Client
     * Player's discard move and tells the model to apply it
     * @param message A string map of the Extant message's name-value pairs
     */
    private void respondToClientDiscard(Map<String, String> message) {
        String cardPlayedInfo = message.get("card");
        String playedColour = Character.toString(cardPlayedInfo.charAt(0));
        int playedRank =
                Integer.parseInt(Character.toString(cardPlayedInfo.charAt(1)));

        //Check whether a card was drawn
        boolean replaced = Boolean.parseBoolean(message.get("replaced"));
        String drawColour = replaced ? "" : null;
        int drawRank = replaced ? 0 : -1;

        synchronized (contData) {
            int handIndex = contData.getLastMoveIndex();
            contData.getGame().discardCard(handIndex, drawColour, drawRank,
              playedColour, playedRank);
        }
    }

    /**
     * Responds to an 'accepted' message from the server accepting the Client
     * Player's inform move and tells the model to apply it
     */
    private void respondToClientInform() {
        synchronized (contData) {
            int playerIndex = contData.getLastMoveIndex();
            String property = contData.getLastMoveProperty();
            contData.getGame().giveInfo(playerIndex, property);
        }
    }

    /**
     * Responds to 'player joined' and 'player left' notices from the server
     * and updates the number of needed players
     * @param message A string map of the message's name-value pairs
     */
    private void respondToNewPlayerCount(Map<String, String> message) {
        int numLeft = Integer.parseInt(message.get("needed"));
        synchronized (contData) {
            contData.getGame().updatePlayersLeft(numLeft);
        }
    }

    /**
     * Responds to a 'game starts' notice from the server and tells the model
     * to start the game with the given set of starting hands
     * @param message A string map of the message's name-value pairs
     */
    private void respondToGameStarts(Map<String, String> message) {
        String hands = message.get("hands");
        String[][] parsedHands = parseStartingHands(hands);
        synchronized (contData) {
            contData.getGame().startGame(parsedHands);
        }
    }

    /**
     * Responds to a 'game cancelled' notice from the server and closes the
     * connection to the server while ending the game
     */
    private void respondToGameCancelled() {
        synchronized (contData) {
            contData.getGame().endGame();
        }
        try {
            sender.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to disconnect from server during " +
                    "unnatural end of game");
        }
    }

    /**
     * Responds to a 'game end' notice from the server and closes the
     * connection to the server while ending the game
     */
    private void respondToGameEnds() {
        synchronized (contData) {
            contData.getGame().endGame();
        }
        try {
            sender.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to disconnect from server during " +
                    "natural end of game");
        }
    }

    /**
     * Responds to a 'your move' notice from the server and either waits for
     * a move or gets an AI Player's move
     */
    private void respondToYourMove() {
        synchronized (contData) {
            if (contData.isAI()) {
                Map<String, String> move = contData.getGame().getAIMove();
                int handIndex;
                switch (move.get("move")) {
                    case "play":
                        handIndex = Integer.parseInt(move.get("handIndex"));
                        String pileColour = move.get("pileColour");
                        contData.setLastMoveIndex(handIndex);
                        contData.setLastPileColour(pileColour);
                        sender.sendPlay(handIndex, pileColour);
                        break;
                    case "discard":
                        handIndex = Integer.parseInt(move.get("handIndex"));
                        contData.setLastMoveIndex(handIndex);
                        sender.sendDiscard(handIndex);
                        break;
                    case "info":
                        int playerIndex =
                          Integer.parseInt(move.get("playerIndex"));
                        String property = (move.get("property").equals("rank"))
                          ? move.get("rank")
                          : move.get("suit");
                        contData.setLastMoveIndex(playerIndex);
                        contData.setLastMoveProperty(property);
                        sender.sendInfo(playerIndex, property);
                        break;
                    default:
                        System.err.println("AI Player sent unknown move type");
                }
            }
        }
    }

    /**
     * Responds to a 'discarded' notice from the server and tells the model
     * to discard one of the current (non-Client) Player's cards
     * @param message A string map of the message's name-value pairs
     */
    private void respondToDiscarded(Map<String, String> message) {
        int handIndex = Integer.parseInt(message.get("position"));

        //Get the drawn card and its rank and colour if a card was drawn.
        // Otherwise, pass the colour and rank of a null card (null and -1)
        String drawColour;
        int drawRank;
        String cardPlayedInfo = message.get("drew");
        if (!cardPlayedInfo.equals("NONE")) {
            drawColour = Character.toString(cardPlayedInfo.charAt(0));
            drawRank = Integer.parseInt(
                    Character.toString(cardPlayedInfo.charAt(1)));
        } else {
            drawColour = null;
            drawRank = -1;
        }

        synchronized (contData) {
            contData.getGame().discardCard(handIndex, drawColour, drawRank);
        }
    }

    /**
     * Responds to a 'played' notice from the server and tells the model to
     * play one of the current (non-Client) Player's cards
     * @param message A string map of the message's name-value pairs
     */
    private void respondToPlayed(Map<String, String> message) {
        int handIndex = Integer.parseInt(message.get("position"));

        //We need to get the pile colour from a 'firework' field if possible.
        // If one isn't provided, we can't get the right pile colour from
        // within the controller, so pass an empty string as a signal to
        // delegate this determination to playCard
        String pileColour = message.getOrDefault("firework", "");

        //Get the drawn card and its rank and colour if a card was drawn.
        // Otherwise, pass the colour and rank of a null card (null and -1)
        String drawColour;
        int drawRank;
        if (message.containsKey("drew")) {
            String cardPlayedInfo = message.get("drew");
            drawColour = Character.toString(cardPlayedInfo.charAt(0));
            drawRank = Integer.parseInt(
                    Character.toString(cardPlayedInfo.charAt(1)));
        } else {
            drawColour = null;
            drawRank = -1;
        }

        synchronized (contData) {
            contData.getGame().playCard(handIndex, drawColour, drawRank,
              pileColour);
        }
    }

    /**
     * Responds to an 'inform' notice from the server and tells the model to
     * inform one of the Players about a given property in their hand
     * @param message A string map of the message's name-value pairs
     */
    private void respondToNoticeInform(Map<String, String> message) {
        //Grab the kind of info being given by the other Player
        boolean rankInfo = message.containsKey("rank");
        String property = rankInfo
                ? message.get("rank")
                : message.get("suit");

        //Determine whether the Client Player or another Player is being
        // given info. If its another Player, just grab the player index and
        // have the model tell them about the info
        boolean isOtherPlayer = message.containsKey("player");
        synchronized (contData) {
            if (isOtherPlayer) {
                int playerIndex = Integer.parseInt(message.get("player"));
                contData.getGame().giveInfo(playerIndex, property);
            } else {
                //If its the Client Player getting info, then we need to get and
                // parse the list of booleans before we can tell the model to
                // inform us
                String cards = message.get("cards");
                boolean[] info = parseCardInfo(cards);
                contData.getGame().giveInfo(property, info);
            }
        }
    }

    /**
     * Parses the single string of starting hands into a 2D arrays of card
     * strings
     * @param hands The string with the starting hands send by the server
     * @return A 2D array of the form "ci" for cards with colour c and rank i
     * in the Player's hands. The cards in the Client Player's own hand are
     * empty strings instead.
     */
    protected String[][] parseStartingHands(String hands) {
        String[] splitToCard = null;
        String[][] properHand = null;
        int cardNum = -1;
        int clientPlayerIndex = -1;
        int numPlayer = -1;

        String infoNoBracket = hands.substring(1, hands.length() - 1);

        // After the first split, each member in splitEachHand array will contain
        // the whole hand of each player.
        // However, this split will always result in first element being empty,
        // hence the for loop from Template to real Split
        String[] splitEachHandTemplate =
          infoNoBracket.split("(\\[)|(\\],\\[)|(\\])");
        String[] splitEachHand = new String[splitEachHandTemplate.length - 1];
        numPlayer = splitEachHand.length;
        for (int i = 0; i < splitEachHandTemplate.length - 1; i++) {
            splitEachHand[i] = splitEachHandTemplate[i + 1];
        }

        // This loop split each hand further down to each card in the hand with a
        // bad format, something like this: b1
        // It main purpose is to grab the number of player in the game and how many
        // cards they can have. These value will then be used to initialize the
        // returning array properHand
        for (int i = 0; i < splitEachHand.length; i++) {
            splitToCard = splitEachHand[i].split(",");

            // Checking the number of card in once's hand, only hand with more
            // than 3 cards (so 4 or 5) will the number get set
            if (splitToCard.length > 3) {
                cardNum = splitToCard.length;
            }

            // We marked the client player by the low number of character in their
            // string, his hand will be empty, while other players
            if (splitEachHand[i].length() < 10) {
                clientPlayerIndex = i;
            }
        }

        // Initialize the return starting hand string with information acquired
        properHand = new String[numPlayer][cardNum];

        // This loop will then extract the cards and put them into the right
        // slot of properHand (except for client Player)
        for (int i = 0; i < cardNum; i++) {
            if (i != clientPlayerIndex) {
                splitToCard = splitEachHand[i].split(",");
                for (int k = 0; k < splitToCard.length; k++) {
                    properHand[i][k] = splitToCard[k].strip();
                }
            }
        }

        // Last for loop to fill Client player with empty string in each slot
        for (int i = 0; i < cardNum; i++) {
            properHand[clientPlayerIndex][i] = "";
        }

        return properHand;
    }


    /**
     * Parses the single string with a boolean array of card info into a
     * boolean array
     * @param info The string with the boolean array info sent by the server
     * @return A boolean array with the sent info
     */
    private boolean[] parseCardInfo(String info) {
        //Remove the array brackets before splitting the string on commas and
        // spaces and parsing the remaining boolean strings
        String infoNoBracket = info.substring(1, info.length()-1);
        String[] infoStr = infoNoBracket.split("[, ]+");
        boolean[] infoBool = new boolean[infoStr.length];
        for (int i = 0; i < infoStr.length; i++) {
            infoBool[i] = Boolean.parseBoolean(infoStr[i]);
        }
        return infoBool;
    }
}
