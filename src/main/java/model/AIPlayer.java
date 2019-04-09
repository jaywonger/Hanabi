package model;

import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class for making AI Player decisions. It takes in most of the state of
 * the game and uses it to separately determine the best play, discard, and
 * info giving move on their own before ranking them and choosing one move
 * for an AI Player to make.
 */
public class AIPlayer {
    /**
     * Takes most of the state of a Hanabi game and gets the best move for an
     * AI Player to make
     * @param handAI The Hand of the AI Player
     * @param playerHands A map of the other Players indices to their hands
     * @param fireworks The current state of the fireworks piles
     * @param discard The current discard pile
     * @param informationTokens The current information tokens
     * @param hasRainbows A flag for whether rainbows should be accounted for
     *                   in the number of available colours
     * @return A map of move properties and values: the move type, the hand
     * index (for plays and discards), the firework pile to play on (for
     * plays), a player index (for info), and a kind of property and property
     * value (for info)
     */
    public Map<String, String> getMove(Hand handAI,
                                       Map<Integer, Hand> playerHands,
                                       List<FireworksPile> fireworks,
                                       DiscardPile discard,
                                       Token informationTokens,
                                       boolean hasRainbows) {
        //Get the best play, discard, and info moves separately before
        // comparing them to get the best play to return
        PlayAIMove bestPlay = bestPlay(handAI, fireworks);
        DiscardAIMove bestDiscard = bestDiscard(handAI, fireworks, discard,
          hasRainbows);
        InfoAIMove bestInfo = bestInfo(fireworks, playerHands, hasRainbows);
        return bestMove(bestPlay, bestDiscard, bestInfo, informationTokens);
    }

    /**
     * Takes the best move of each kind and the information tokens and
     * determines the best move to make
     * @param bestPlay A PlayAIMove with the best play's playValue, hand index,
     *                and
     * @param bestDiscard A DiscardAIMove with the best discard's playValue and
     *                   hand index
     * @param bestInfo An InfoAIMove with the best info's target Player index,
     *                 kind of property, and property to tell them about
     * @param informationTokens The current information tokens
     * @return A map of move properties and values: the move type, the hand
     * index (for plays and discards), the firework pile to play on (for
     * plays), a player index (for info), and a kind of property and property
     * value (for info)
     */
    protected Map<String, String> bestMove(PlayAIMove bestPlay,
                                         DiscardAIMove bestDiscard,
                                         InfoAIMove bestInfo,
                                         Token informationTokens) {
        //Get the list of allowed moves, excluding discards or info giving if
        // the information token count precludes those. They are added to the
        // list in order of move preference (info, play, discard).
        int nTokens = informationTokens.getCount();
        List<AIMove> allowedMoves = new ArrayList<>();
        if (nTokens > 0) { allowedMoves.add(bestInfo); }
        allowedMoves.add(bestPlay);
        if (nTokens < 8) { allowedMoves.add(bestDiscard); }

        //Selects from the available moves, preferring higher playValues
        AIMove selectedMove;
        for (int p = 0; p <= 3; p++) {
            List<AIMove> pMoves = filterMovesByValue(allowedMoves, p);
            if (!pMoves.isEmpty()) {
                //If there are moves left after filtering for a playValue,
                // then the first item is the move to select since we added
                // them in order of preference (info, play, discard) and
                // filtering lists preserves order
                selectedMove = pMoves.get(0);
                return selectedMove.asMap();
            }
        }

        return null;
    }

    /**
     * Takes an AI Player's hand and the current fireworks piles and determines
     * the best play to make
     * @param handAI The Hand of the AI Player's cards
     * @param fireworks A list of the FireworksPiles in their current state
     * @return A PlayAIMove object with the best move's playValue, hand index,
     * and firework pile index
     */
    protected PlayAIMove bestPlay(Hand handAI,
                                List<FireworksPile> fireworks) {
        //Get the heights of the current fireworks piles
        int[] heights = getFireworkHeights(fireworks);

        //Initialize the parameters of a play to an invalid default
        int handIndex = 1; //Lower is taken to resolve ties
        int playValue = 3; //Lower is better (for better plays)
        int playPile = -1; //Track the pile that a card should be played on (to
                           // disambiguate wild rainbows)
        int rankOfPlay = 0; //Higher is better for plays with a known rank

        //Check each card in the AI Player's hand for its worth as a play
        for (int i = 1; i <= handAI.getSize(); i++) {
            Card curCard = handAI.getCard(i);
            if (curCard != null) {
                boolean[] cardInfo = curCard.getInfo();

                //Check if the card is fully known (i.e. has a known colour and
                // rank) and is the next card in a fireworks pile
                if (cardInfo[0] && cardInfo[1]) {
                    //Find the list index of the matching firework pile
                    int fireIndex = getFireworkByColour(fireworks, curCard);

                    //Check if the card is the next rank in its fireworks
                    // pile. If fireIndex is still -1, then this is a rainbow
                    // card in a game where there are wildcards, so all of
                    // the fireworks piles have to be checked.
                    if (fireIndex != -1) {
                        if (curCard.getRank() == heights[fireIndex] + 1) {
                            //This card is the next card in a fireworks pile.
                            // Check if its better than the current play.
                            if (playValue > 0
                              || rankOfPlay < curCard.getRank()) {
                                playValue = 0;
                                handIndex = i;
                                rankOfPlay = curCard.getRank();
                                playPile = fireIndex;
                            }
                        }
                    } else {
                        for (int f = 0; f < fireworks.size(); f++) {
                            if (curCard.getRank() == heights[f] + 1) {
                                //This rainbow card can be the next card in a
                                // fireworks pile (which is implicitly the
                                // leftmost fireworks pile). Check if its
                                // better than the current play.
                                if (playValue > 0
                                  || rankOfPlay < curCard.getRank()) {
                                    playValue = 0;
                                    handIndex = i;
                                    rankOfPlay = curCard.getRank();
                                    playPile = f;
                                    //The first firework pile a rainbow card can
                                    // fit in is selected
                                    break;
                                }
                            }
                        }
                    }
                }

                //Check for if the card has a known rank and could be the
                // next card in a fireworks pile based on that
                if (cardInfo[0] && playValue > 0) {
                    for (int f = 0; f < fireworks.size(); f++) {
                        if (curCard.getRank() == heights[f] + 1) {
                            //This card could be the next card in this fireworks
                            // pile (which is implicitly the leftmost pile).
                            // Check if its better than the current play.
                            if (playValue > 1 || rankOfPlay < curCard.getRank()) {
                                playValue = 1;
                                handIndex = i;
                                rankOfPlay = curCard.getRank();
                                playPile = f;
                                //The leftmost pile is selected
                                break;
                            }
                        }
                    }
                }

                //Check for if the card has a known colour (and therefore
                // would be a play slightly better than awful)
                if (cardInfo[1] && playValue > 1) {
                    //This card is a decent fallback option to play, even if
                    // its a rainbow card. Check if its better than the
                    // current play.
                    if (playValue > 2) {
                        playValue = 2;
                        handIndex = i;
                        rankOfPlay = 0;

                        //Get the index of the right firework pile to play it on
                        // based on the colour, defaulting to the first pile for
                        // wild rainbows
                        playPile = 0;
                        for (int f = 0; f < fireworks.size(); f++) {
                            if (fireworks.get(f).getColour()
                              .equals(curCard.getColour())) {
                                playPile = f;
                            }
                        }
                    }
                }

                //If nothing is known about this card, leave the playValue alone
            }
        }

        //If nothing is known about any card in the hand, then pick a random
        // non-null card
        if (playValue == 3) {
            Card randomCard = null;
            Random rand = new Random();
            while (randomCard == null) {
                handIndex = rand.nextInt(5) + 1;
                randomCard = handAI.getCard(handIndex);
            }

            //...and find the right firework to play it on, defaulting to the
            // first pile for wild rainbows
            playPile = 0;
            for (int f = 0; f < fireworks.size(); f++) {
                if (fireworks.get(f).getColour()
                  .equals(handAI.getCard(handIndex).getColour())) {
                    playPile = f;
                }
            }
        }

        //Pack the play parameters into a PlayAIMove and return it
        PlayAIMove bestPlay = new PlayAIMove();
        bestPlay.playValue = playValue;
        bestPlay.handIndex = handIndex;
        bestPlay.pileColour = fireworks.get(playPile).getColour();
        return bestPlay;
    }

    /**
     * Takes the AI Player's current hand and the discard pile and determines
     * the best discard move to make
     * @param handAI The Hand of the AI Player's cards
     * @param fireworks A list of the FireworksPiles in their current state
     * @param discard The DiscardPile in its current state
     * @param hasRainbows A flag for whether or not the game has rainbow cards
     * @return A DiscardAIMove object with the best move's playValue and hand
     * index
     */
    protected DiscardAIMove bestDiscard(Hand handAI,
                                      List<FireworksPile> fireworks,
                                      DiscardPile discard,
                                      boolean hasRainbows) {
        //Declare the number of cards of each rank for a given colour
        final int[] numPerRank = {3, 2, 2, 2, 1};

        //Get the heights of the current fireworks piles and the cards in the
        // discard pile
        int[] heights = getFireworkHeights(fireworks);
        List<Card> discardCards = discard.getCards();

        //Initialize the parameters of a discard to an invalid default
        int handIndex = 1; //Lower is taken to resolve ties
        int playValue = 3; //Lower is better (for better discards)
        int rankOfDiscard = 6; //Lower is better for discards with a known rank

        //Check each card in the AI Player's hand for its worth as a discard
        for (int i = 1; i <= handAI.getSize(); i++) {
            Card curCard = handAI.getCard(i);
            if (curCard != null) {
                boolean[] cardInfo = curCard.getInfo();

                //Check if the card is fully known and duplicates a card in a
                // firework pile
                if (cardInfo[0] && cardInfo[1]) {
                    //Find the list index of the matching firework pile
                    int fireIndex = getFireworkByColour(fireworks, curCard);

                    //Check if the card is duplicated in its fireworks pile. If
                    // fireIndex is still -1, then this is a rainbow card in a
                    // game where there are wildcards, so all of the fireworks
                    // piles have to be checked.
                    if (fireIndex != -1) {
                        //Check if the cards rank is not greater than the top
                        // rank of its firework pile
                        if (curCard.getRank() <= heights[fireIndex]) {
                            //This card is a duplicate. Check whether its better
                            // than the current discard
                            if (playValue > 0
                              || curCard.getRank() < rankOfDiscard) {
                                handIndex = i;
                                playValue = 0;
                                rankOfDiscard = curCard.getRank();
                            }
                        } else {
                            if (curCard.getRank()
                              <= Arrays.stream(heights).min().getAsInt()) {
                                //This rainbow card is a duplicate in all of the
                                // fireworks piles. Check if its better than the
                                // current discard.
                                if (playValue > 0
                                  || curCard.getRank() < rankOfDiscard) {
                                    handIndex = i;
                                    playValue = 0;
                                    rankOfDiscard = curCard.getRank();
                                }
                            }
                        }
                    }
                }

                //Check if the card is fully known and not the last copy of its
                // kind based on the discard pile. Rainbow cards are treated
                // as a distinct colour even if they are wild.
                if (cardInfo[0] && cardInfo[1] && playValue > 0) {
                    //Count the number of cards in the discard pile that
                    // match the current card
                    int counter = 0;
                    for (Card c : discardCards) {
                        if (curCard.equals(c)) {
                            counter++;
                        }
                    }

                    //Check that this isn't the last card of its kind across the
                    // hand and discard pile
                    int maxNumPerKinid = numPerRank[curCard.getRank() - 1];
                    if (counter + 1 < maxNumPerKinid) {
                        //This card is good to discard. Check its better than
                        // the current discard.
                        if (playValue > 1
                          || curCard.getRank() < rankOfDiscard) {
                            handIndex = i;
                            playValue = 1;
                            rankOfDiscard = curCard.getRank();
                        }
                    }
                }

                //Check that a card has 1 known property and might not be the
                // last card of its kind
                if (cardInfo[0] || cardInfo[1] && playValue > 1) {
                    //Count the number of cards in the discard pile that match
                    // the known property
                    int counter = 0;
                    for (Card c : discardCards) {
                        if (cardInfo[0] && curCard.sameRank(c)
                          || cardInfo[1] && curCard.sameColour(c)) {
                            counter++;
                        }
                    }

                    //Get the number of cards for a single given property and
                    // check that this isn't the last card of its kind across
                    // the hand and discard pile
                    int maxNumofKind;
                    if (cardInfo[0]) {
                        int numColours = hasRainbows ? 6 : 5;
                        maxNumofKind =
                          numColours * numPerRank[curCard.getRank() - 1];
                    } else {
                        maxNumofKind = Arrays.stream(numPerRank).sum();
                    }

                    if (counter + 1 < maxNumofKind) {
                        //This card is good to discard. Check its better than
                        // the current discard.
                        if (playValue > 2
                          || curCard.getRank() < rankOfDiscard) {
                            handIndex = i;
                            playValue = 2;
                            rankOfDiscard = curCard.getRank();
                        }
                    }
                }


                //If nothing is known about this card, leave the playValue alone
            }
        }

        //If nothing is known about any card in the hand, then pick a random
        // card
        if (playValue == 3) {
            Random rand = new Random();
            handIndex = rand.nextInt(5) + 1;
        }

        //Pack the discard parameters into a map and return it
        DiscardAIMove bestDiscard = new DiscardAIMove();
        bestDiscard.playValue = playValue;
        bestDiscard.handIndex = handIndex;
        return bestDiscard;

    }

    /**
     * Takes the hands of the AI Player's fellow players and the current
     * fireworks piles and determines the best info to give
     * @param fireworks A list of the FireworksPiles in their current state
     * @param playerHands A map of the other Player's indices to their Hands
     * @return An InfoAIMove object with the best info's playValue, player
     * index, and either the rank or colour of the info
     */
    protected InfoAIMove bestInfo(List<FireworksPile> fireworks,
                                Map<Integer, Hand> playerHands,
                                boolean hasRainbows) {
        //Get the heights of the current fireworks piles
        int[] heights = getFireworkHeights(fireworks);

        //Initialize the parameters of an info move to an invalid default
        int playerIndex = 1; //Lower is taken to resolve ties
        int handIndex = 1; //Lower is taken to resolve ties
        int playValue = 3; //Lower is better (for giving better info)
        boolean rankInfo = false; //Flag for whether the current info is
                                  // about rank or colour
        int infoRank = 0; //Higher is better for info about ranks

        //Initialize trackers for total information
        Map<Integer, Integer> totalUnknowns = new LinkedHashMap<>();

        //Check all of the cards in the other player's hands that aren't
        // fully known to them to find the best info to give
        for (Map.Entry<Integer, Hand> curPlayer : playerHands.entrySet()) {
            int curPlayerIndex = curPlayer.getKey();

            //Get the total number of properties this player doesn't know
            totalUnknowns.put(curPlayerIndex,
              totalUnknownProperties(curPlayer.getValue()));
            //totalUnknowns[curPlayerIndex - 1] =
            //  totalUnknownProperties(curPlayer.getValue());

            for (int i = 1; i <= curPlayer.getValue().getSize(); i++) {
                Card curCard = curPlayer.getValue().getCard(i);
                if (curCard != null) {
                    boolean[] cardInfo = curCard.getInfo();
                    if (!(cardInfo[0] && cardInfo[1])) {
                        //Check to see if the card is the next card in a
                        // firework pile. If its a wild rainbow card, then
                        // we need to check if it could be next in any
                        // firework pile.
                        int fireIndex = getFireworkByColour(fireworks, curCard);
                        if (fireIndex != -1) {
                            //Non-wild case
                            if (curCard.getRank() == heights[fireIndex] + 1) {
                                //This card is the next card in a firework pile.
                                // Determine what info to give and check if its
                                // better than the current info.
                                if (cardInfo[1] && (playValue > 1
                                  || curCard.getRank() > infoRank)) {
                                    //If they know about the colour already,
                                    // tell them about the rank of this card
                                    playValue = 1;
                                    playerIndex = curPlayerIndex;
                                    handIndex = i;
                                    rankInfo = true;
                                    infoRank = curCard.getRank();
                                } else if (playValue > 1 || !rankInfo) {
                                    //If they know about the rank already or
                                    // don't know anything, tell them about the
                                    // colour of this card
                                    playValue = 1;
                                    playerIndex = curPlayerIndex;
                                    handIndex = i;
                                    rankInfo = false;
                                    infoRank = 0;
                                }
                            }
                        } else {
                            //Wild rainbow case
                            for (int f = 0; f < fireworks.size(); f++) {
                                if (curCard.getRank() == heights[f] + 1) {
                                    //This rainbow card is the next card in a
                                    // firework pile. Determine what info to
                                    // give and check if its better than the
                                    // current info.
                                    if (cardInfo[1] && (playValue > 1
                                      || curCard.getRank() > infoRank)) {
                                        //If they know about the colour already,
                                        // tell them about the rank of this card
                                        playValue = 1;
                                        playerIndex = curPlayerIndex;
                                        handIndex = i;
                                        rankInfo = true;
                                        infoRank = curCard.getRank();
                                        break; //Select leftmost pile
                                    } else if (playValue > 1 || !rankInfo) {
                                        //If they know about the rank already or
                                        // don't know anything, tell them
                                        // about the colour of this card
                                        playValue = 1;
                                        playerIndex = curPlayerIndex;
                                        handIndex = i;
                                        rankInfo = false;
                                        infoRank = 0;
                                        break; //Select leftmost pile
                                    }
                                }
                            }
                        }
                    }

                    //Check to see if the card has rank 5 and this isn't known
                    if (!cardInfo[0] && curCard.getRank() == 5
                      && playValue > 1) {
                        //We should tell them about the rank 5. Determine if
                        // this is better than the current info
                        if (playValue > 2) {
                            playValue = 2;
                            playerIndex = curPlayerIndex;
                            handIndex = i;
                            rankInfo = true;
                            infoRank = 5;
                        }
                    }

                    //If this card isn't the next one in a firework or a rank 5
                    // card, leave the playValue alone.
                }
            }
        }

        //If good info was found, then tell the other player that info
        if (playValue < 3) {
            InfoAIMove bestInfo = new InfoAIMove();
            bestInfo.playValue = playValue;
            bestInfo.playerIndex = playerIndex;

            //Stuff the rank or colour to inform about for playValue=1 moves,
            // or rank 5 for playValue=2 moves
            Card bestCard = playerHands.get(playerIndex).getCard(handIndex);
            if (playValue == 2) {
                bestInfo.rank = infoRank;
            } else if (rankInfo) {
                bestInfo.rank = bestCard.getRank();
            } else {
                bestInfo.colour = bestCard.getColour();
            }
            return bestInfo;
        } else {
            //If no better info was found, then tell the most info you can.
            //Start by finding who knows the least about their hand
            Map.Entry<Integer, Integer> minUnknowns = null;
            for (Map.Entry<Integer, Integer> tu : totalUnknowns.entrySet()) {
                if (minUnknowns == null
                  || tu.getValue() < minUnknowns.getValue()) {
                    minUnknowns = tu;
                }
            }
            int leastIndex = minUnknowns.getKey();

            //Get the least known property to tell them about
            Map<String, String> leastProperty =
              leastKnownProperty(playerHands.get(leastIndex), hasRainbows);

            //Tell the other player about the info that tells them the most
            InfoAIMove bestInfo = new InfoAIMove();
            bestInfo.playValue = 3;
            bestInfo.playerIndex = leastIndex;
            if (leastProperty.get("property").equals("rank")) {
                bestInfo.rank = Integer.parseInt(leastProperty.get("rank"));
            } else {
                bestInfo.colour = leastProperty.get("colour");
            }
            return bestInfo;
        }
    }

    /**
     * Gets the heights of the current fireworks piles
     * @param fireworks The list of the fireworks piles
     * @return An integer array with the heights of the fireworks
     */
    private int[] getFireworkHeights(List<FireworksPile> fireworks) {
        int[] heights = new int[fireworks.size()];
        for (int i = 0; i < fireworks.size(); i++) {
            heights[i] = fireworks.get(i).getSize();
        }
        return heights;
    }

    /**
     * Gets the index of the firework whose colour matches
     * @param fireworks A list of the fireworks piles
     * @param card The card to find the matching colour firework for
     * @return An integer index from 0-4 of the matching colour firework, or
     * -1 if a matching pile can't be found (implying a rainbow card in a
     * game with wild rainbows)
     */
    private int getFireworkByColour(List<FireworksPile> fireworks, Card card) {
        int fireIndex = -1;
        for (int f = 0; f < fireworks.size(); f++) {
            if (fireworks.get(f).getColour().equals(card.getColour())) {
                fireIndex = f;
            }
        }
        return fireIndex;
    }

    /**
     * Determines the total number of unknown properties in a hand
     * @param hand The hand to get the number of unknown properties for
     * @return The total number of unknown ranks and colours
     */
    private int totalUnknownProperties(Hand hand) {
        int unknowns = 0;
        for (Card c : hand.getCards()) {
            if (c != null){
                boolean[] info = c.getInfo();
                if (info[0]) { unknowns++; }
                if (info[1]) { unknowns++; }
            }
        }
        return unknowns;
    }

    /**
     * Determines the least known property of a hand
     * @param hand The hand to get the least known property for
     * @param hasRainbows A flag for whether to check for rainbow cards as well
     * @return A map with what kind of property and which property is the
     * least known
     */
    private Map<String, String> leastKnownProperty(Hand hand,
                                                   boolean hasRainbows) {
        Map<Integer, Integer> unknownRank = new LinkedHashMap<>();
        unknownRank.put(1, 0);
        unknownRank.put(2, 0);
        unknownRank.put(3, 0);
        unknownRank.put(4, 0);
        unknownRank.put(5, 0);
        Map<String, Integer> unknownColour = new LinkedHashMap<>();
        unknownColour.put("r", 0);
        unknownColour.put("b", 0);
        unknownColour.put("g", 0);
        unknownColour.put("y", 0);
        unknownColour.put("w", 0);
        if (hasRainbows) { unknownColour.put("m", 0); }

        //Iterate through the hand's cards and count each unknown property
        for (Card c : hand.getCards()) {
            if (c != null) {
                boolean[] info = c.getInfo();
                if (!info[0]) {
                    //Count the rank of this card as unknown
                    int cRank = c.getRank();
                    unknownRank.put(cRank, unknownRank.get(cRank) + 1);
                }
                if (!info[1]) {
                    //Count the colour of this card as unknown
                    String cColour = c.getColour();
                    unknownColour.put(cColour, unknownColour.get(cColour) + 1);
                }
            }
        }

        //Iterate through the colours and ranks to find the least known of
        // property (i.e. the property with the most unknown flags), preferring
        // higher rank cards
        Map.Entry<String, Integer> maxColour = null;
        Map.Entry<Integer, Integer> maxRank = null;
        for (Map.Entry<String, Integer> colour : unknownColour.entrySet()) {
            if (maxColour == null || colour.getValue() > maxColour.getValue()) {
                maxColour = colour;
            }
        }
        for (Map.Entry<Integer, Integer> rank: unknownRank.entrySet()) {
            if (maxRank == null) {
                maxRank = rank;
            } else {
                boolean greaterUnknown = rank.getValue() >= maxRank.getValue();
                if (greaterUnknown
                  || (rank.getKey() > maxRank.getKey())) {
                    maxRank = rank;
                }
            }
        }

        //Return the least known property, preferring colour over rank in a tie
        Map<String, String> property = new HashMap<>();
        if (maxRank.getValue() > maxColour.getValue()) {
            property.put("property", "rank");
            property.put("rank", maxRank.getKey().toString());
        } else {
            property.put("property", "colour");
            property.put("colour", maxColour.getKey());
        }
        return property;
    }

    /**
     * Filters a list of AIMoves by their playValue
     * @param moves The list of AIMoves to filter
     * @param p The playValue to filter for
     * @return The filtered list of moves
     */
    private List<AIMove> filterMovesByValue(List<AIMove> moves,
                                           int p) {
        return moves.stream()
          .filter(m -> m.playValue == p)
          .collect(Collectors.toList());
    }

    /**
     * A base data class for passing around different kinds of moves the AI
     * could make. All moves should eventually be turned into Maps when
     * leaving the purview of AIPlayer.
     */
    protected class AIMove {
        int playValue;

        public Map<String, String> asMap() {
            Map<String, String> m = new HashMap<>();
            m.put("move", "null");
            return m;
        }
    }

    /**
     * A data class for passing around Play Moves the AI could make
     */
    protected class PlayAIMove extends AIMove {
        int handIndex;
        String pileColour;

        public Map<String, String> asMap() {
            Map<String, String> m = new HashMap<>();
            m.put("move", "play");
            m.put("handIndex", Integer.toString(handIndex));
            m.put("pileColour", pileColour);
            return m;
        }
    }

    /**
     * A data class for passing around Discard Moves the AI could make
     */
    protected class DiscardAIMove extends AIMove {
        int handIndex;

        public Map<String, String> asMap() {
            Map<String, String> m = new HashMap<>();
            m.put("move", "discard");
            m.put("handIndex", Integer.toString(handIndex));
            return m;
        }
    }

    /**
     * A data class for passing around Info Moves the AI could make
     */
    protected class InfoAIMove extends AIMove {
        int playerIndex;
        int rank = 0;
        String colour = "";

        public Map<String, String> asMap() {
            Map<String, String> m = new HashMap<>();
            m.put("move", "info");
            m.put("playerIndex", Integer.toString(playerIndex));
            if (rank > 0) {
                m.put("property", "rank");
                m.put("rank", Integer.toString(rank));
            } else {
                m.put("property", "colour");
                m.put("colour", colour);
            }
            return m;
        }
    }
}

