package view;

import java.util.Scanner;

import controller.SendController;

/**
 * The Create Game view. Gets the parameters for a new game from the user and
 * uses them to send a 'create' message to the Server.
 */
public class CreateView {
    controller.SendController sendController;
    private Scanner in;
    private int numplayers;
    private int timeout;
    private String nsid;
    private String rainbows;
    private boolean force;

    /**
     * Creates a new Create Game view, gets the parameters for creating a
     * game, and sends a 'create' message to the Server
     * @param sendController The controller to use to send messages to the
     *                       Server
     * @param _in The input Scanner to use for getting user input
     */
    public CreateView(SendController sendController, Scanner _in) {
        this.sendController = sendController;
        this.in = _in;
        this.run();
        this.sendController.tellCreateGame(numplayers, timeout, rainbows,
          force, nsid);
    }

    /**
     * Gets all of the game parameters needed to create a game from the user
     */
    private void run() {
        boolean proceed = true;
        while (proceed) {
            proceed = false;
            int result;
            try {
                result = Integer.parseInt(runScanner("Please enter number of " +
                  "players(2-5): "));
                if (result <= 5 && result >= 2) {
                    this.setNumberOfPlayers(result);
                } else {
                    printMsg("Cannot have more than 5 players in a game or " +
                      "less than 2 players in a game\n");
                    proceed = true;
                }
                result = Integer.parseInt(runScanner("Please enter timeout " +
                  "period(1s-120s): "));
                if (result <= 120 && result >= 1) {
                    this.setTimeout(result);
                } else {
                    printMsg("Timeout period must be between 1 to 120 s");
                    proceed = true;
                }

            } catch (Exception e) {
                printMsg("Invalid entry");
                proceed = true;
            }
        }
        this.setNsid(this.runScanner("Enter Nsid: "));


        String result = null;

        proceed = true;
        while (proceed) {
            result = this.runScanner("Force Create Game? (Y/N)");
            if (result.equals("Y") || result.equals("N")
              || result.equals("y") || result.equals("n")) {
                proceed = false;
            } else {
                System.out.println("Invalid entry");
            }
        }
        this.setForce(result.equals("Y") || result.equals("y"));

        proceed = true;
        boolean rainbowsCheck;
        while (proceed) {
            result = this.runScanner("Should rainbows be used? (Y/N)");
            if (result.equals("Y") || result.equals("N")
              || result.equals("y") || result.equals("n")) {
                rainbowsCheck = (result.equals("Y") || result.equals("y"));
                proceed = false;
            } else {
                System.out.println("Invalid entry");
            }
        }
        rainbowsCheck = result.equals("Y") || result.equals("y");
        if (!rainbowsCheck) {
            this.rainbows = "none";
        } else if (rainbowsCheck) {
            this.rainbows = this.runScanner("Which kind: Fireworks('f') or " +
              "Wild ('w')");
            if (this.rainbows.equals("f")) {
                this.rainbows = "firework";
            } else if (this.rainbows.equals("w")) {
                this.rainbows = "wild";
            }
        }
    }

    // ----------   Getters   --------------
    public int getNumberOfPlayers() {
        return numplayers;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getNsid() {
        return nsid;
    }

    public boolean isForce() {
        return force;
    }

    // ----------   Setters    --------------
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numplayers = numberOfPlayers;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setNsid(String nsid) {
        this.nsid = nsid;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    private void printMsg(String s) {
        System.out.print(s);
    }

    private String runScanner(String message) {
        printMsg(message);
        return in.nextLine();
    }
}

