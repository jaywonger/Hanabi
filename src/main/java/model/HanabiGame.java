package model;

import java.util.*;

/**
 * HanabiGame represents the state of a single game of Hanabi. This includes
 * the identity of the current game, the number of Players in a game, the
 * Players and their hands, the discard and fireworks piles, the fuse and
 * information tokens, and a log of the game’s moves. This also includes
 * trackers for whose turn it is, whether the discard pile and log should be
 * visible to the Player running the Client, and how rainbow cards are
 * handled in the current game.
 */
public class HanabiGame extends Observable implements GameModifier {
    /**
     * The Game ID of the current game.
     */
    private int gameID;
    /**
     * The Token of the current game.
     */
    private String token;
    /**
     * The number of players that still need to join the game.
     */
    private int numPlayersLeft = 5;
    /**
     * The players in the game, each with their own hand of cards.
     */
    private List<Player> players;
    /**
     * The discard pile with all the cards that have been discarded during
     * the game.
     */
    private DiscardPile discardPile;
    /**
     * The fireworks piles, one for each colour, with the successfully played
     * cards.
     */
    private List<FireworksPile> fireworks;
    /**
     * A set of fuse tokens.
     */
    private Token fuseTokens;
    /**
     * A set of information tokens.
     */
    private Token informationTokens;
    /**
     * A log of all the moves made throughout the game so far.
     */
    private Log log;
    /**
     * The index of the Player with the current turn.
     */
    private int playerTurn;
    /**
     * A flag for whether the Player can see the cards in the discard pile.
     */
    private boolean discardVisible;
    /**
     * A flag for whether the Player can see the action in the log.
     */
    private boolean logVisible;
    /**
     * The Player index of the person running the Client
     */
    private int ourIndex;
    /**
     * A flag for whether or not rainbow cards exist in the game
     */
    private boolean hasRainbowCards;
    /**
     * A flag for recognizing how rainbows cards are handled
     * 0: No rainbow cards
     * 1: Rainbow cards have their own pile
     * 2: Rainbow cards are wild and can be used in any pile
     */
    private int gameOption;
    /**
     * A map of state changes made by the game which is pushed to Observer
     * views when they are notified of changes
     */
    private Map<String, String> stateChangeMap = new HashMap<>();

    /**
     * Constructs a new HanabiGame with empty card containers, token
     * counters, logs, and Player lists
     */
    public HanabiGame() {
        this.players = new LinkedList<>();
        this.fireworks = new LinkedList<>();
        this.discardPile = new DiscardPile(50);
        this.informationTokens = new Token(8);
        this.fuseTokens = new Token(3);
        this.log = new Log();
    }

    // ****************** Getters ******************
    protected int getGameID() {
        return gameID;
    }

    protected String getToken() {
        return token;
    }

    protected int getNumPlayersLeft() {
        return numPlayersLeft;
    }

    protected List<Player> getPlayers() {
        return players;
    }

    protected DiscardPile getDiscardPile() {
        return discardPile;
    }

    protected List<FireworksPile> getFireworks() {
        return fireworks;
    }

    protected Token getFuseTokens() {
        return fuseTokens;
    }

    protected Token getInformationTokens() {
        return informationTokens;
    }

    protected Log getLog() {
        return log;
    }

    protected int getPlayerTurn() {
        return playerTurn;
    }

    protected boolean isDiscardVisible() {
        return discardVisible;
    }

    protected boolean isLogVisible() {
        return logVisible;
    }

    public int getOurIndex() {
        return ourIndex;
    }

    public Map<String, String> getStateChangeMap() {
        return stateChangeMap;
    }

    // ****************** Setters ******************
    protected void setGameID(int gameID) {
        this.gameID = gameID;
    }

    protected void setToken(String token) {
        this.token = token;
    }

    protected void setNumPlayersLeft(int numPlayers) {
        this.numPlayersLeft = numPlayers;
    }

    protected void setPlayers(List<Player> players) {
        this.players = players;
    }

    protected void setDiscardPile(DiscardPile discardPile) {
        this.discardPile = discardPile;
    }

    protected void setFireworks(List<FireworksPile> fireworks) {
        this.fireworks = fireworks;
    }

    protected void setFuseTokens(Token fuseTokens) {
        this.fuseTokens = fuseTokens;
    }

    protected void setInformationTokens(Token informationTokens) {
        this.informationTokens = informationTokens;
    }

    protected void setLog(Log log) {
        this.log = log;
    }

    protected void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    protected void setDiscardVisible(boolean discardVisible) {
        this.discardVisible = discardVisible;
    }

    protected void setLogVisible(boolean logVisible) {
        this.logVisible = logVisible;
    }

    public void setOurIndex(int ourIndex) {
        this.ourIndex = ourIndex;
    }

    public void setStateChangeMap(Map<String, String> stateChangeMap) {
        this.stateChangeMap = stateChangeMap;
    }

    /**
     * Enters a new game with a given identity
     * @param id      The Game ID of the new game to enter
     * @param token   The string token of the new game to enter
     * @param rainbow The String identifier for how rainbow cards are handled
     *                in the new game
     */
    public void enterGame(int id, String token, String rainbow) {
        switch (rainbow) {
            case "none":
                this.hasRainbowCards = false;
                this.gameOption = 0;
                break;
            case "firework":
                this.hasRainbowCards = true;
                this.gameOption = 1;
                break;
            case "wild":
                this.hasRainbowCards = true;
                this.gameOption = 2;
                break;
            default:
                break;
        }
        // setting game ID and token if it is the game creator
        if (this.numPlayersLeft == 5) {
            this.gameID = id;
            this.token = token;
        }
    }

    /**
     * Updates the number of remaining Player slots in the game
     * @param numLeft The number of Player slots left in the game
     */
    public void updatePlayersLeft(int numLeft) {
        this.numPlayersLeft = numLeft;

        setChanged();
        this.stateChangeMap.clear();
        this.stateChangeMap.put(
          "Players left", Integer.toString(this.numPlayersLeft));
        notifyObservers(this.stateChangeMap);
    }


    /**
     * Starts a game of Hanabi using the cards dealt out to each Player. Each
     * of 2-5 Players gets a hand with 5 cards (with 2-3 Players) or 4 cards
     * (with 4-5 Players). The Player running the Client will be given an
     * empty hand since they don’t know what cards they have at the start of
     * the game.
     * @param hands A String array with the hands of cards that the Players
     *              start with. Each card is 2 characters: one for the colour
     *              (r,b,g,y,w,m) and one for the rank (1,2,3,4,5).
     */
    public void startGame(String[][] hands) {
        // cases for different hand sizes
        int handSize;
        if (this.numPlayersLeft > 3) {
            handSize = 4;
        } else {
            handSize = 5;
        }
        // outer loop for making player objects
        int numPlayers = hands.length;
        for (int i = 0; i < numPlayers; i++) {
            // making and adding new players
            Player newPlayer = new Player(handSize);
            this.players.add(newPlayer);
            // inner loop for adding hands to player objects hands
            for (int x = 0; x < handSize; x++) {
                // condition to find the player running the client
                if (hands[i][x].equals("")) {
                    ourIndex = i;
                    // make placeholder cards for this player
                    for (int y = 0; y < handSize; y++) {
                        Card newCard = new Card(0, "", false, false);
                        this.players.get(i).getHand().addCard(newCard);
                    }
                    break;
                } else {
                    // parsing the hand string to store just rank and colour
                    int rank = Character.getNumericValue(hands[i][x].charAt(1));
                    char charColour = hands[i][x].charAt(0);
                    String colour = String.valueOf(charColour);
                    // creating new card object
                    Card newCard = new Card(rank, colour, false, false);
                    // adding card object to player
                    players.get(i).getHand().addCard(newCard);
                }
            }
        }
        // setting the current player to the first player
        this.playerTurn = 0;

        // creating fireworks piles based on gameOption
        FireworksPile p1 = new FireworksPile("w");
        this.fireworks.add(p1);
        FireworksPile p2 = new FireworksPile("y");
        this.fireworks.add(p2);
        FireworksPile p3 = new FireworksPile("b");
        this.fireworks.add(p3);
        FireworksPile p4 = new FireworksPile("r");
        this.fireworks.add(p4);
        FireworksPile p5 = new FireworksPile("g");
        this.fireworks.add(p5);
        if (this.gameOption == 1) {
            // rainbow card pile
            FireworksPile p6 = new FireworksPile("m");
            this.fireworks.add(p6);
        }

        // telling view state has changed
        setChanged();
        // clearing map
        this.stateChangeMap.clear();
        // putting all hands into strings
        String p_hand = "hand: ";
        for (int i = 0; i < numPlayers; i++) {
            for (int x = 0; x < handSize; x++) {
                // if player running client skip
                if (hands[i][x].equals("")) {
                    break;
                }
                p_hand += hands[i][x];
            }
            this.stateChangeMap.put("Player" + i, p_hand);
            p_hand = "hand: ";
        }
        notifyObservers(this.stateChangeMap);
    }

    /**
     * Pushes an update to the game's observers whenever a card is played
     * @param changedPile The fireworks pile that was changed
     * @param changedPlayerIndex The player whose hand changed
     */
    private void playCardNotifyHelper(FireworksPile changedPile,
                                      int changedPlayerIndex) {
        // informing the view about new state
        setChanged();

        // getting the fireworks pile that was changed
        String fireWorksPile = "Fire" + changedPile.getColour();
        String cards = "";
        for (int i = 0; i < changedPile.getSize(); i++) {
            String colour = changedPile.getCards().get(i).getColour();
            int rank_int = changedPile.getCards().get(i).getRank();
            String rank = Integer.toString(rank_int);
            cards += colour + "" + rank;
        }
        this.stateChangeMap.clear();
        this.stateChangeMap.put(fireWorksPile, cards);

        // getting the player that got a new card
        Player cur_player = this.players.get(changedPlayerIndex);
        String player = "Player" + changedPlayerIndex;
        String hand = "hand: ";
        for (int i = 1; i < cur_player.getHand().getSize() + 1; i++) {
            String colour = cur_player.getHand().getCard(i).getColour();
            int rank_int = cur_player.getHand().getCard(i).getRank();
            String rank = Integer.toString(rank_int);
            hand += colour + "" + rank;
        }
        this.stateChangeMap.put(player, hand);

        // getting the new discard pile
        String discardPile = "DiscardPile";
        String discard_cards = "";
        for (int i = 0; i < this.discardPile.getSize(); i++) {
            String colour = this.discardPile.getCards().get(i).getColour();
            int rank_int = this.discardPile.getCards().get(i).getRank();
            String rank = Integer.toString(rank_int);
            discard_cards += colour + "" + rank;
        }
        this.stateChangeMap.put(discardPile, discard_cards);

        // getting the new fuse token count
        int fuseCount = this.fuseTokens.getCount();
        String fuseCountString = Integer.toString(fuseCount);
        this.stateChangeMap.put("TokenFuse", fuseCountString);

        // notifying view
        notifyObservers(this.stateChangeMap);
    }

    /**
     * Switches a card in the current Player's hand with a drawn card
     * @param handIndex The index of the card to replace in the current
     *                  Player's hand
     * @param drawRank The rank of the Player's drawn card
     * @param drawColour: The colour of the Player's drawn card
     */
    private void switchingCardsHelper(int handIndex, String drawColour,
                                     int drawRank) {
        // switching players card with new card
        this.players.get(this.playerTurn).getHand().removeCard(handIndex);
        Card new_card = new Card(drawRank, drawColour, false, false);
        this.players.get(playerTurn).getHand().addCard(new_card, handIndex);
    }

    /**
     * Makes the current Player play a card from their hand and replace it
     * with a drawn card.
     * @param handIndex The index of the card to play in
     *                    the current Player’s hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param pileColour The colour of the firework to play the card on, or
     *                   an empty string if a pile wasn't provided
     */
    public void playCard(int handIndex, String drawColour, int drawRank,
                         String pileColour) {
        // current player plays card
        Card card_to_add =
          this.players.get(this.playerTurn).getHand().getCard(handIndex);

        // property for log
        String[] property = {"Players handIndex: " + handIndex,
          "Card colour: " + card_to_add.getColour(),
          "Card rank: " + card_to_add.getRank()};

        // if no rainbow cards just use the cards colour
        if (pileColour.equals("")) {
            pileColour =
              this.players.get(this.playerTurn)
                .getHand().getCard(handIndex).getColour();
        }

        // finding the pile index, rainbow cards case
        int pile_index = 0;
        for (int i = 0; i < this.fireworks.size(); i++) {
            if (this.fireworks.get(i).getColour().equals(pileColour)) {
                pile_index = i;
                break;
            }
        }
        FireworksPile cur_fireWorks_pile = this.fireworks.get(pile_index);

        // if the pile can still be added to
        if (cur_fireWorks_pile.size < 5) {
            try {
                cur_fireWorks_pile.addCard(card_to_add);

                // switching players card with new card
                switchingCardsHelper(handIndex, drawColour, drawRank);

                // calling helper to inform view of state change
                playCardNotifyHelper(cur_fireWorks_pile, this.playerTurn);

                // adding action to log
                addActionToLog("Play Card", property);
            } catch (IllegalArgumentException e) {
                // switching players card with new card
                switchingCardsHelper(handIndex, drawColour, drawRank);

                // discard card and lose fuse token
                this.discardPile.addCard(card_to_add);
                this.fuseTokens.removeToken();

                // calling helper to inform view of state change
                playCardNotifyHelper(cur_fireWorks_pile, this.playerTurn);
            }
        }
        // if the pile is full
        else {
            // switching players card with new card
            switchingCardsHelper(handIndex, drawColour, drawRank);

            // discard card
            this.discardPile.addCard(card_to_add);

            // calling helper to inform view of state change
            playCardNotifyHelper(cur_fireWorks_pile, this.playerTurn);
        }
    }

    /**
     * Informs the current Player of the rank and colour of a card they are
     * playing, then plays that card from their hand and replaces it with a
     * drawn card
     * @param handIndex The index of the card to play in the current Player’s
     *                 hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param pileColour The index of the pile in which to
     *                    play the card.
     * @param ownColour The colour of the Player's played card
     * @param ownRank The rank of the Player's played card
     */
    public void playCard(int handIndex, String drawColour, int drawRank,
                         String pileColour, String ownColour, int ownRank) {
        Player cur_player = this.players.get(this.playerTurn);

        // making a new card that has the info known
        Card players_card_known = new Card(ownRank, ownColour, true, true);

        // removing players actual card and replacing it with known card
        cur_player.getHand().removeCard(handIndex);
        cur_player.getHand().addCard(players_card_known, handIndex);

        playCard(handIndex, drawColour, drawRank, pileColour);
    }

    /**
     * Makes the current Player discard a card from their hand and replace it
     * with a drawn card
     * @param handIndex The index of the discarded card in the current
     *                  Player’s hand
     * @param drawColour: The colour of the Player's drawn card
     * @param drawRank: The rank of the Player's drawn card
     */
    public void discardCard(int handIndex, String drawColour, int drawRank) {
        Player cur_player = this.players.get(playerTurn);
        // removing players card
        Card card_to_remove = cur_player.getHand().getCard(handIndex);
        cur_player.getHand().removeCard(handIndex);

        // add a new card to the index
        Card card_to_add = new Card(drawRank, drawColour, false, false);
        this.players.get(playerTurn).getHand().addCard(card_to_add, handIndex);

        // adding discarded card to discard pile
        this.discardPile.addCard(card_to_remove);
        // increment info token
        if (this.informationTokens.getCount() < 8) {
            this.informationTokens.addToken();
        }
        String[] property = {"Players handIndex: " + handIndex,
          "Card colour: " + card_to_remove.getColour(),
          "Card rank: " + card_to_remove.getRank()};
        addActionToLog("Discard Card", property);

        // informing the view about new state
        setChanged();
        // getting the new discard pile
        String discardPile = "DiscardPile";
        String discard_cards = "";
        for (int i = 0; i < this.discardPile.getSize(); i++) {
            String colour = this.discardPile.getCards().get(i).getColour();
            int rank_int = this.discardPile.getCards().get(i).getRank();
            String rank = Integer.toString(rank_int);
            discard_cards += colour + "" + rank;
        }
        this.stateChangeMap.put(discardPile, discard_cards);
        notifyObservers(this.stateChangeMap);
    }

    /**
     * Informs the current Player of the rank and colour of a card they are
     * discarding, then discards that card from their hand and replaces it
     * with a drawn card
     * @param handIndex The index of the card to discard in the current
     *                  Player’s hand
     * @param drawColour The colour of the Player's drawn card
     * @param drawRank The rank of the Player's drawn card
     * @param ownColour The colour of the Player's discarded card
     * @param ownRank The rank of the Player's discarded card
     */
    public void discardCard(int handIndex, String drawColour, int drawRank,
                            String ownColour, int ownRank) {
        Player cur_player = this.players.get(this.playerTurn);

        // making a new card that has the info known
        Card players_card_known = new Card(ownRank, ownColour, true, true);

        // removing players actual card and replacing it with known card
        cur_player.getHand().removeCard(handIndex);
        cur_player.getHand().addCard(players_card_known, handIndex);

        discardCard(handIndex, drawColour, drawRank);
    }

    /**
     * Tells the Player running the client information about their hand that
     * matches the given property. This version handles the case of the
     * Client Player being given info.
     * @param property The property of the other Player’s hand to tell them
     *                 about.
     * @param info An array of which cards in the Client Player's hand have
     *             the given property
     */
    public void giveInfo(String property, boolean[] info) {
        // getting player
        Player player = this.players.get(ourIndex);
        // checking if have enough info tokens
        if (this.informationTokens.getCount() > 0) {
            this.informationTokens.removeToken();

            // parsing the info given to figure out if it is colour or rank
            boolean isRank = true;
            for (char c : property.toCharArray()) {
                if (!Character.isDigit(c)) {
                    isRank = false;
                }
            }

            // property for the log
            String[] property_to_add_to_log =
              {"Players Index: " + Integer.toString(ourIndex),
                "Property Known: " + property};

            // for player running client
            for (int i = 0; i < info.length; i++) {
                if (info[i]) {
                    boolean[] card_info =
                      player.getHand().getCard(i + 1).getInfo();
                    giveInfoHelper(player, isRank, card_info, i + 1, property);
                }
            }

            // adding action to the log
            addActionToLog("Give Info", property_to_add_to_log);

            // informing the view about new state
            setChanged();
            // properties of the info given (getting the hand of the player
            // who was given a property)
            this.stateChangeMap.put("Give Info",
              property_to_add_to_log[0] + ", " + property_to_add_to_log[1]);
            notifyObservers(this.stateChangeMap);
        }
    }

    /**
     * Tells the given Player information about their hand that matches the
     * given property. This version handles the case of a Player other than
     * the Client being given info.
     * @param property The property of the other Player’s hand to tell them
     *                 about
     * @param otherPlayerIndex The other Player's index
     */
    public void giveInfo(int otherPlayerIndex, String property) {
        // getting player
        Player player = this.players.get(otherPlayerIndex);
        // checking if have enough info tokens
        if (this.informationTokens.getCount() > 0) {
            this.informationTokens.removeToken();

            // parsing the info given to figure out if it is colour or rank
            boolean isRank = true;
            for (char c : property.toCharArray()) {
                if (!Character.isDigit(c)) {
                    isRank = false;
                }
            }

            // property for the log
            String[] property_to_add_to_log =
              {"Players Index: " + Integer.toString(otherPlayerIndex),
                "Property Known: " + property};

            // for all other players
            for (int i = 1; i <= 5; i++) {
                boolean[] card_info = player.getHand().getCard(i).getInfo();
                giveInfoHelper(player, isRank, card_info, i, property);
            }

            // adding action to the log
            addActionToLog("Give Info", property_to_add_to_log);

            // informing the view about new state
            setChanged();
            // making a map for what has changed
            String giveInfo = "Give Info";

            // properties of the info given (getting the hand of the player
            // who was given a property)
            this.stateChangeMap.put(giveInfo,
              property_to_add_to_log[0] + ", " + property_to_add_to_log[1]);
            notifyObservers(this.stateChangeMap);
        }
    }

    /**
     * Generates a new card and swaps it into a Player's hand when they are
     * given more info about a card in their hand
     * @param player The Player being given info
     * @param isRank Whether or not the property is a rank or a colour
     * @param cardInfo The rank and colour information of the card
     * @param i The index of the card in the Player's hand
     * @param property The property the Player was told about
     */
    private void giveInfoHelper(Player player, boolean isRank,
                                boolean[] cardInfo, int i, String property) {
        // if the rank or colour is not known and property passed was rank
        if (!cardInfo[0] && isRank && !cardInfo[1]) {
            // getting existing card rank and colour
            String colour = player.getHand().getCard(i).getColour();
            // let player know about rank by replacing existing card with new
            // one
            Card replacement =
              new Card(Character.getNumericValue(property.charAt(0)), colour,
                true, false);
            player.getHand().removeCard(i);
            player.getHand().addCard(replacement, i);
        }
        // if the colour and rank is not known and the property passed was
        // colour
        else if (!cardInfo[1] && !isRank && !cardInfo[0]) {
            // getting existing cards rank and colour
            int rank = player.getHand().getCard(i).getRank();
            // let player know about colour by replacing existing card with
            // new one
            Card replacement = new Card(rank, property, false, true);
            player.getHand().removeCard(i);
            player.getHand().addCard(replacement, i);
        }
        // if the rank was known but colour was not and property was colour
        else if (cardInfo[0] && !cardInfo[1] && !isRank) {
            // getting existing cards rank and colour
            int rank = player.getHand().getCard(i).getRank();
            // let player know about colour by replacing existing card with
            // new one
            Card replacement = new Card(rank, property, true, true);
            player.getHand().removeCard(i);
            player.getHand().addCard(replacement, i);
        }
        // if rank is not known but colour is and property was rank
        else if (!cardInfo[0] && isRank) {
            // getting existing cards rank and colour
            String colour = player.getHand().getCard(i).getColour();
            // let player know about colour by replacing existing card with
            // new one
            Card replacement =
              new Card(Character.getNumericValue(property.charAt(0)), colour,
                true, true);
            player.getHand().removeCard(i);
            player.getHand().addCard(replacement, i);
        }
    }

    /**
     * Toggles the flag for making the log of game moves visible to the Player
     * running the Client.
     */
    public void toggleLogView() {
        logVisible = !logVisible;
    }

    /**
     * Toggles the flag for making the discarded cards visible to the Player
     * running the Client
     */
    public void toggleDiscardView() {
        discardVisible = !discardVisible;
    }

    /**
     * Ends the game of Hanabi, clearing all of the piles and removing all of
     * the Players from the game
     */
    public void endGame() {
        // calculating score
        int score = 0;
        for (FireworksPile firework : this.fireworks) {
            score += firework.getSize();
        }

        this.fireworks.clear();
        this.discardPile.clear();
        this.log.clear();
        this.players.clear();
        this.gameID = 0;
        this.token = "";

        // informing the view about new state
        setChanged();
        // making a map for what has changed
        this.stateChangeMap.put("Score", Integer.toString(score));
        notifyObservers(this.stateChangeMap);
    }


    /**
     * Gets a move for an AI Player based on the state of the game
     * @return A map of move properties and values: the move type, the hand
     * index (for plays and discards), the firework pile to play on (for
     * plays), a player index (for info), and a kind of property and property
     * value (for info)
     */
    public Map<String, String> getAIMove() {
        // calls the getMove method in AIPlayer and returns the result
        AIPlayer AI_player = new AIPlayer();
        Player cur_player = this.players.get(ourIndex);
        // getting other players and storing in a map
        Map<Integer, Hand> player_map = new HashMap<>();
        // put every value list to Map (should be all players excluding the AI)
        for (int i = 0; i < this.players.size(); i++) {
            if (i != ourIndex) {
                player_map.put(i, this.players.get(i).getHand());
            }
        }

        Map<String, String> move_map = AI_player.getMove(cur_player.getHand(),
          player_map, this.fireworks, this.discardPile, this.informationTokens,
          hasRainbowCards);

        return move_map;
    }

    /**
     * Adds a move with specified parameters to the log
     * @param move A String with the type of move to add: Play, Discard, or
     *             Info
     * @param params A String array with the parameters of the move. This is
     *               either the hand index and colour and rank of a card for
     *               plays and discards, or a Player index and the property
     *               that was mentioned for info giving.
     */
    private void addActionToLog(String move, String[] params) {
        Action new_action = new Action(this.playerTurn, move, params);
        this.log.addAction(new_action);
    }
}
