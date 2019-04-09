package view;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * The Lobby view. Prints the number of Player slots left as Players join the
 * game until all of the slots are filled and the game starts, generating the
 * Game view.
 */
public class LobbyView implements Observer {
	Scanner in;
	String gameID;
	String gameSecret;

	/**
	 * Creates a new Lobby view with a given input scanner
	 * @param in The input Scanner to use for getting user input
	 */
	LobbyView(Scanner in) {
		this.in = in;
	}
	
	@Override
    public void update(Observable o, Object arg) {
		Map<String, String> state = (Map<String, String>)arg;
		printMsg("\nSlots left: " + state.get("Players left") + "\n");
		printMsg("To begin the game please wait until the lobby is full\n");
		String _gameID = state.get("gameID");
		String _gameSecret = state.get("gameSecret");
		if (!"null".equals(_gameID) && !"null".equals(_gameSecret)) {
			this.gameID = _gameID;
			this.gameSecret = _gameSecret;
			printMsg("Game ID: " + this.gameID + "\n");
			printMsg("Game Secret: " + this.gameSecret+ "\n");
		}

		String res = runScanner("To go to Main Menu ('b') \n");
		if (res.equals("b")) {
			return;
		}
	}

	/** Returns a newline character */
	private String n() {return "\n";}

	/**
	 * Prints a supplied message out to the console
	 * @param s The message to print
	 */
    private void printMsg(String s) { System.out.print(s); }

	/**
	 * Gets the user's input after printing a supplied prompt
	 * @param message The prompt to print out to the console before
	 * @return The input obtained from the user over Standard Input
	 */
	private String runScanner(String message) {
        printMsg(message);
        return in.nextLine();
    }
}
