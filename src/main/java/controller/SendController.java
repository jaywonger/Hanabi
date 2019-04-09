package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A controller class that handles requests for message sending and immediate
 * model changes from the view.
 */
public class SendController {
    private final ControllerData contData;
    private final MessageSender sender;
    private final MessageReceiver pairReceiver;

    /**
     * Creates a new SendController with access to a common block of
     * controller data and a ServerComm
     * @param cd The block of controller data
     * @param ms The ServerComm used for sending messages
     */
    public SendController(ControllerData cd, MessageSender ms) {
        contData = cd;
        sender = ms;
        pairReceiver = getMessageReceiver();
    }

    protected MessageReceiver getMessageReceiver() {
        return new ReceiveController(contData, sender);
    }

    /**
     * Reacts to a button press in the view to send a message about creating
     * a new game
     * @param numPlayers The number of players in the game
     * @param timeout The length in seconds of the game's timeout
     * @param rainbowType The way the game should treat rainbow cards: "none"
     *                    excludes them, "firework" makes them a separate 6th
     *                    colour, and "wild" makes them wildcards that can go
     *                    on any other pile
     * @param force Whether or not to force game creation if another game
     *              exists with the same NSID
     * @param nsid The NSID of the Player creating the game
     */
    public void tellCreateGame(int numPlayers, int timeout,
                               String rainbowType, boolean force, String nsid) {
        try {
            String secret = getUserSecret(nsid);
            synchronized (contData) {
                contData.setNsid(nsid);
                contData.setRainbow(rainbowType);
            }
            sender.connect();
            sender.startReceiving(pairReceiver);
            sender.sendCreate(numPlayers, timeout, force, rainbowType, nsid,
              secret);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to get secret for nsid " + nsid);
        } catch (IOException e) {
            System.err.println("Failed to connect to server");
        }
    }

    /**
     * Reacts to a button press in the view to send a message about joining
     * an existing game
     * @param gameID The ID of the game to join
     * @param token The token of the game to join
     * @param nsid The NSID of the Player joining the game
     */
    public void tellJoinGame(int gameID, String token, String nsid) {
        try {
            String secret = getUserSecret(nsid);
            synchronized (contData) {
                contData.setNsid(nsid);
                contData.setGameID(gameID);
                contData.setToken(token);
            }
            sender.connect();
            sender.startReceiving(pairReceiver);
            sender.sendJoin(gameID, token, nsid, secret);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to get secret for nsid " + nsid);
        } catch (IOException e) {
            System.err.println("Failed to connect to server");
        }
    }

    /**
     * Reacts to a button press in the view to create a new AI Player and
     * have them join the current game
     */
    public void tellAddAI() {
        //TODO Confirm that this will work
        //Get the parameters of the current Client's Java runtime environment
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator +
          "java";
        String classpath = System.getProperty("java.class.path");
        String clientMain = "com.cmpt370.f1.controller.hanabiClient.Main";

        //Get string forms of the arguments to the Client for the AI Player
        synchronized (contData) {
            String idAI = Integer.toString(contData.getGameID());
            String tokenAI = contData.getToken();

            //Start a new Client process for the AI Player
            ProcessBuilder aiProcess = new ProcessBuilder(javaBin, "-cp",
              classpath, clientMain, "-g", idAI, "-t", tokenAI);
            try {
                Process p = aiProcess.start();
            } catch (IOException e) {
                System.err.println("Could not start new process for AI Player");
            }
        }
    }

    /**
     * Reacts to button presses in the view to send a message about the
     * Client Player playing a card
     * @param handIndex The index in the Player's hand to play from
     * @param pileColour A specifier for which firework pile to play the card
     *                   on (from 'r', 'b', 'g', 'y', 'w', and 'm'). An empty
     *                   string will not include the pile in the message.
     */
    public void tellPlayCard(int handIndex, String pileColour) {
        synchronized (contData) {
            contData.setLastMoveIndex(handIndex);
            contData.setLastPileColour(pileColour);
        }
        sender.sendPlay(handIndex, pileColour);
    }

    /**
     * Reacts to button presses in the view to send a message about the
     * Client Player discarding a card
     * @param handIndex The index in the player's hand to discard from
     */
    public void tellDiscardCard(int handIndex) {
        synchronized (contData) {
            contData.setLastMoveIndex(handIndex);
        }
        sender.sendDiscard(handIndex);
    }

    /**
     * Reacts to button presses in the view to send a message about giving
     * another Player info about their hand
     * @param playerIndex The index of the other Player to give info to
     * @param property A string with the property to tell the other Player
     *                 about (from a single digit rank from 1 - 5 or a colour
     *                 from 'r', 'b', 'g', 'y', 'w', and 'm')
     */
    public void tellGiveInfo(int playerIndex, String property) {
        synchronized (contData) {
            contData.setLastMoveIndex(playerIndex);
            contData.setLastMoveProperty(property);
        }
        sender.sendInfo(playerIndex, property);
    }

    /**
     * Reacts to a button press in the view to tell the current game to
     * toggle the flag for viewing the discard pile
     */
    public void tellToggleDiscard() {
        synchronized (contData) {
            contData.getGame().toggleDiscardView();
        }
    }

    /**
     * Reacts to a button press in the view to tell the current game to toggle
     * the flag for viewing the action log
     */
    public void tellToggleLog() {
        synchronized (contData) {
            contData.getGame().toggleLogView();
        }
    }

    /**
     * Reacts to button presses in the view to leave a game (either from the
     * lobby or game screens) and clear the model
     */
    public void tellLeaveGame() {
        synchronized (contData) {
            contData.getGame().endGame();
        }
        try {
            sender.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to disconnect from Hanabi server. Did " +
              "you already disconnect?");
            //Should something else happen here (throw exception, tell view,
            // etc.?
        }
    }

    /**
     * Finds and returns the hash secret associated with the given NSID.
     * These are expected to be in a file called 'hanabisecret.txt' in the
     * user's home directory in the format of NSID:Secret on each line.
     * @param nsid The NSID to get the secret for
     * @return The user's secret as a string
     * @throws IllegalArgumentException If the given NSID and its secret
     * cannot be found
     */
    private String getUserSecret(String nsid) {
        //Find the user's config file with their NSIDs and secrets
        String userHome = System.getProperty("user.home");
        String secretsPath = userHome + File.separator + "hanabisecret.txt";

        //Read all of the NSID:Secret lines in the config file into a map
        Map<String, String> secrets;
        try {
            //Use a stream to read the lines and put them into a map.
            //Filter only grabs lines in the form NSID:Secret
            //Collectors.toMap grabs the NSID and Secret separately to make a
            // new map entry
            Stream<String> secretStream = Files.lines(Paths.get(secretsPath));
            secrets = secretStream
              .filter(s -> s.matches("^\\w+:\\w+$"))
              .collect(Collectors.toMap(
                k -> k.split(":")[0], v -> v.split(":")[1]));
            secretStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("hanabisecret.txt not found in " +
              "home directory");
        }

        //Look for the given NSID in the new map and return its corresponding
        // secret
        if (secrets.containsKey(nsid)) {
            return secrets.get(nsid);
        }
        throw new IllegalArgumentException(nsid + "not found in hanabisecret" +
          ".txt");
    }
}
