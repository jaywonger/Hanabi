package view;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * The main View class and the Main Menu and Game Over views. Displays a main
 * menu to the user and prompts them to select whether to create or join a
 * game or whether to close the Client. When the end of a game is detected,
 * it displays a different menu asking whether to go back to the main menu or
 * close the Client.
 */
public class DisplayC implements Observer {
    private Scanner in;
    controller.SendController sendController;

    /**
     * Creates a new Main Menu view and displays the main menu
     * @param sendController The controller used by other spawned views to send
     *                       messages to the server
     */
    public DisplayC(controller.SendController sendController) {
        this.in = new Scanner(System.in);
        this.sendController = sendController;
        printMsg("Welcome to Hanabi!\n");
        displayMainMenu();
    }

    /**
     * Displays the Main Menu view, gets the users input, and either spawns
     * the appropriate view or closes the Client
     */
    public void displayMainMenu() {
        String mainMenu =
              "1. Create Game\n"
            + "2. Join Game\n"
            + "3. Exit Game\n"
            + "Your choice: ";
        String choice = runScanner(mainMenu);

        switch (choice) {
            case "1":
                new CreateView(sendController, in);
                break;
            case "2":
                //new JoinView();
                break;
            case "3":
            	printMsg("\nThank you for playing!");
                System.exit(0);
                return;
            default:
                break;
        }
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

    @Override
    public void update(Observable o, Object arg) {
        Map<String, String> state = (Map<String, String>)arg;
        String finalScore = state.get("Score");
        if (!"null".equals(finalScore)) {
        	String input = this.gameOverC(finalScore);
        	if (input.equals("m")) {
        		displayMainMenu();
        	} else if (input.equals("q")) {
        		System.exit(0);
        	}
        }
        
        String[] fireworks = {
    		state.get("Fireb"), 
    		state.get("Firer"), 
    		state.get("Fireg"), 
    		state.get("Firew"),
    		state.get("Firey"), 
    		state.get("Firem")
        };
        String[][] hands = {
            {state.get("Player1")},
            {state.get("Player2")},
            {state.get("Player3")},
            {state.get("Player4")},
            {state.get("player5")}
        };
        String tokens = state.get("TokenFuse");
        String info = state.get("TokenInfo");
        String discard = state.get("DiscardPile");
    }

    /**
     * Prints a menu for the Game Over view that asks the user whether to go
     * back to the Main Menu aor close the Client, gets their input, and
     * returns the choice as a character string
     * @param scoreString The final score for the game as a string
     * @return 'm' if the user wants to go back to the Main Menu, or 'q' if
     * they want to quit and close the Client
     */
    private String gameOverC(String scoreString) {
		printMsg("Game Over\n");
		int score = Integer.parseInt(scoreString); 
		String result = "";
		if (score <= 5) {
			result = "Oh dear! The crowd booed.";
		} else if (score <= 10) {
			result = "Poor! Hardly applaused.";
		} else if (score <= 15) {
			result = "OK! The viewers have seen better.";
		} else if (score <= 20) {
			result = "Good! The audience is pleased.";
		} else if (score <= 24) {
			result = "Very good! The audience is enthusiastic!";
		} else {
			result = "Legendary! The audience will never forget this show!";
		}
		printMsg("Your Score is: " + score + result + "\n");

		return runScanner("('m') for Main Menu\n" +
				 "('q') for Quit");
	}
}
