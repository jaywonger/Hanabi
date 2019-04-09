package view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * The Game view. Displays the current state of the game as notified by the
 * model and asks the user for a move to make
 */
public class GameView {
	Scanner in;
	String[] fireworks; 
	String[][] hands;
	String tokens, info, discard;

	/**
	 * Creates a new Game view with a given input scanner
	 * @param in The input Scanner to use for getting user input
	 */
	GameView(Scanner in) {
		this.in = in;
	}

	/**
	 * Updates the view's known state of the game
	 * @param fireworks The new state of the fireworks piles as a String array
	 *                  with the top of the piles
	 * @param hands The new state of the Player's hands as a 2D array of card
	 *              strings
	 * @param tokens The new state of the token counters
	 * @param info The new state of the other Player's info about their cards
	 * @param discard The new state of the discard pile
	 */
	public void updateGame(String[] fireworks, String[][] hands,
						   String tokens, String info, String discard) {
		this.fireworks = fireworks;
		this.hands = hands;
		this.tokens = tokens;
		this.info = info;
		this.discard = discard;
	}

	/**
	 * Prints the current set of fireworks piles known to the view
	 */
	void printFireworks() {
		printMsg(
				"F  [" 
				+ constructPrintHand(this.fireworks) 
				+ "]\t Info : "
				+ this.info
				+ n());
	}

	/**
	 * Prints the current set of Player hands known to the view
	 */
	void printPlayers() {
		String res = "";
		for (int i = 0; i < this.hands.length; i++) {
			String hand = this.constructPrintHand(this.hands[i]);
			if (!"null".equals(hand)) {
				res += 
						"P" + (i+1) + " ["
								+ hand  
								+ "]";
				if (i == 0) {
					res += "\t Fuses: " + tokens;
				}
				res += n();
			}
		}
		printMsg(res);
	}

	/**
	 * Constructs the hand of an individual Player for printing as known to
	 * the view
	 * @param hand The Player's current hand as a string array
	 * @return A single string of a Player's hand
	 */
	private String constructPrintHand(String[] hand) {
		String res = "";
		for (String pile: hand) {
			if (pile == null) {
				return "null";
			} else {
			List<String> cards = this.split(pile, 2);
			for (int i = 0; i < cards.size(); i++ ) {
		    	  if (i == cards.size()-1) {
		    		  res += cards.get(i);
		    	  }else {
		    		  res += cards.get(i) + ", ";
		    	  }
		      }
			}
		}
		return res;
	}

	/**
	 * Prints the status of the game as known to the view and a menu for
	 * getting their next move
	 */
	private void printAll() {
		this.printFireworks();
		this.printPlayers();
		this.printMenu();
	}

	/**
	 * Prints a menu for the kinds of moves that can be made and gets one
	 * move from the user
	 */
	private void printMenu() {
		printMsg(n() + "Your options:" + n());
		printMsg("1. Play('p')  2. Discard('d')  3. Info('i')  4. Quit('q')" + n());
		String input = runScanner("Your choice: ");
		switch (input) {
			case "p":
				this.chooseCard("play"); _n();
				this.choosePile(); _n();
				break;
			case "d":
				this.chooseCard("discard"); _n();
				break;
			case "i":
				this.choosePlayerIno(); _n(); // Verify which is chosen (can't
				                              // choose oneself)
				this.chooseInfo(); _n(); // Info
				break;
			case "q":
				// return main menu here
				return;
			default:
				break;
		}
	}

	/**
	 * Prints a menu for choosing a card in the hand of a Player and gets a
	 * hand index from the user
	 * @param type The type of move that is being made with the selected card
	 */
	private void chooseCard(String type) {
		int cardNumber; 
		while (true) {
			cardNumber = Integer.parseInt(runScanner(
					"Which card would you like to " + type + "? "));
			if (this.choiceChange()) {
				break;
			}
		}
		this.printMsg("You chose to " + type + " card #" + cardNumber + n());
	}

	/**
	 * Prints a menu about selecting a firework pile to play a card onto and
	 * gets a pile from the user
	 */
	private void choosePile() {
		int pileNumber; 
		while (true) {
			pileNumber = Integer.parseInt(runScanner(
					"Which pile would you like choose? "));
			if (this.choiceChange()) {
				break;
			}
		}
		this.printMsg("You chose to to play on pile #" + pileNumber + n());
	}

	/**
	 * Prints a menu for giving info to another player and gets from the
	 * user the Player to give info to and the card to use as an example of
	 * one of 2 properties to them them about
	 */
	private void choosePlayerIno() {
		String player, card;
		while (true) {
			player = runScanner("Choose a player: ");
			card = runScanner("Choose which card: ");
			printMsg("You chose Player " + player + " and their card #" + card + n());
			if (this.choiceChange()) {
				break;
			}
		}
		int playerIndex = Integer.parseInt(player) - 1;	
	}

	/**
	 * Prints a menu for determining what property of another Player's
	 * selected card they should be told about and gets that property from
	 * the user
	 */
	private void chooseInfo() {
		
	}
	
	// ------------------------- HELPERS ------------------------- //
	/**
	 * Splits a single string into a list of equally sized substrings of a
	 * given size
	 * @param string The string to split
	 * @param splitSize The size of the split parts of the string
	 * @return A list of the split parts of the string
	 */
	private List<String> split(String string, int splitSize) {
        List<String> res = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=splitSize) {
            res.add(string.substring(i, Math.min(len, i + splitSize)));
        }
        return res;
    }

	/**
	 * Prints a menu to confirm whether the user wants to change their
	 * choices, gets their choice, and returns the corresponding boolean
	 * @return Whether or not the user wants to change their choices
	 */
	private boolean choiceChange() {
    	String input = runScanner("Do you want to change your choice? (y/n) ");
    	if (input.equals("n")) {
			return true;
		}
    	return false;
    }

    /** Returns a newline character */
    private String n() {return "\n";}

    /** Prints a newline character */
    private void _n() { printMsg(n());}

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
