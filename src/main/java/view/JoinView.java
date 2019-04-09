package view;

import java.util.Scanner;

/**
 * The Join Game view. Gets the identification for a game to join from the
 * user and sends a 'join' message to the Server.
 */
public class JoinView {
    private Scanner in;
    private int gameID;
    private String gameToken;
    private String Nsid;

    /**
     * Creates a new Join Game view, gets the parameters for joining a game,
     * and sends a 'join' message to the Server
     * @param newIn The input Scanner to use for getting user input
     */
    public JoinView(Scanner newIn) {
        in = newIn;
        this.run();
    }

    /**
     * Gets the game parameters needed to join a game from the user
     */
    private void run() {
        boolean proceed = true;
        while(proceed){
            proceed = false;
            try {
                this.setGameID(Integer.parseInt(this.runScanner("Enter " +
                  "GameID: ")));
            } catch(Exception e){
                printMsg("Invalid entry");
                proceed = true;
            }
        }
        this.setGameToken(this.runScanner("Enter GameToken: "));
        this.setNsid(this.runScanner("Enter Nsid to join: "));
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameToken() {
        return gameToken;
    }

    public void setGameToken(String gameToken) {
        this.gameToken = gameToken;
    }

    public String getNsid() {
        return Nsid;
    }

    public void setNsid(String nsid) {
        Nsid = nsid;
    }

    /**
     * Prints a supplied message out to the console
     * @param s The message to print
     */
    protected void printMsg(String s) { System.out.print(s); }

    /**
     * Gets the user's input after printing a supplied prompt
     * @param message The prompt to print out to the console before
     * @return The input obtained from the user over Standard Input
     */
    protected String runScanner(String message) {
        printMsg(message);
        return in.nextLine();
    }
}
