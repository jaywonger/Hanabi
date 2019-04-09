package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HanabiGameTest {
    private HanabiGame G1 = new HanabiGame();

    @Test
    void HanabiTest() {
        // testing game creation works
        HanabiGame G1 = new HanabiGame();
        assertEquals(0, G1.getPlayers().size());
    }

    @Test
    void enterGameTest() {
        // testing enterGame() checking if game id and token were set
        G1.enterGame(1, "sample", "none"); // first player joining
        assertEquals(1, G1.getGameID(), "Game ID should be 1");
        assertEquals("sample", G1.getToken(), "Game token should be 'sample'");
    }

    @Test
    void startGameTest() {
        // testing startGame()
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "g4", "r5"},
            {"b5", "b4", "g3", "g2", "r1"},
          };

        G1.startGame(hands);
        // making sure player objects were created
        assertEquals(3, G1.getPlayers().size());

        // checking that first players hands were made properly
        assertEquals(0, G1.getPlayers().get(0).getHand().getCard(1).getRank(), "First players cards should be 0");

        // checking that second players hands were made properly
        Card p2_c1 = new Card(1, "b", false, false);
        Card p2_c2 = new Card(2, "b", false, false);
        Card p2_c3 = new Card(3, "b", false, false);
        Card p2_c4 = new Card(4, "g", false, false);
        Card p2_c5 = new Card(5, "r", false, false);

        assertEquals(p2_c1.getRank(), G1.getPlayers().get(1).getHand().getCard(1).getRank());
        assertEquals(p2_c2.getRank(), G1.getPlayers().get(1).getHand().getCard(2).getRank());
        assertEquals(p2_c3.getRank(), G1.getPlayers().get(1).getHand().getCard(3).getRank());
        assertEquals(p2_c4.getRank(), G1.getPlayers().get(1).getHand().getCard(4).getRank());
        assertEquals(p2_c5.getRank(), G1.getPlayers().get(1).getHand().getCard(5).getRank());

        // checking that third players hands were made properly
        Card p3_c1 = new Card(5, "b", false, false);
        Card p3_c2 = new Card(4, "b", false, false);
        Card p3_c3 = new Card(3, "b", false, false);
        Card p3_c4 = new Card(2, "g", false, false);
        Card p3_c5 = new Card(1, "r", false, false);

        assertEquals(p3_c1.getRank(), G1.getPlayers().get(2).getHand().getCard(1).getRank());
        assertEquals(p3_c2.getRank(), G1.getPlayers().get(2).getHand().getCard(2).getRank());
        assertEquals(p3_c3.getRank(), G1.getPlayers().get(2).getHand().getCard(3).getRank());
        assertEquals(p3_c4.getRank(), G1.getPlayers().get(2).getHand().getCard(4).getRank());
        assertEquals(p3_c5.getRank(), G1.getPlayers().get(2).getHand().getCard(5).getRank());

    }

    @Test
    public void pCard_Rainbow_Opt1() {
        // players joining
        G1.enterGame(1, "sample", "wild");
        G1.enterGame(1, "sample", "wild");
        G1.enterGame(1, "sample", "wild");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"m1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);

        // setting player turn to be second player
        G1.setPlayerTurn(1);
        Card player_card = G1.getPlayers().get(1).getHand().getCard(1);

        // testing adding a card to the correct pile (starting the pile)
        G1.playCard(1, "r", 1, "b");
        assertEquals(1, G1.getFireworks().get(2).getSize());
        assertEquals(player_card.getRank(), G1.getFireworks().get(2).getCards().get(0).getRank());
        // making sure player got proper draw
        assertEquals(1, G1.getPlayers().get(1).getHand().getCard(1).getRank());
    }

    @Test
    public void pCard_Rainbow_Opt2() {
        // players joining
        G1.enterGame(1, "sample", "firework");
        G1.enterGame(1, "sample", "firework");
        G1.enterGame(1, "sample", "firework");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"m1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);

        // setting player turn to be second player
        G1.setPlayerTurn(1);
        Card player_card = G1.getPlayers().get(1).getHand().getCard(1);

        // testing adding a card to the correct pile (starting the pile)
        G1.playCard(1, "r", 1, "m");
        assertEquals(1, G1.getFireworks().get(5).getSize());
        assertEquals(player_card.getRank(), G1.getFireworks().get(5).getCards().get(0).getRank());
        // making sure player got proper draw
        assertEquals(1, G1.getPlayers().get(1).getHand().getCard(1).getRank());
    }

    @Test
    public void playCardTest() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);

        // setting player turn to be second player
        G1.setPlayerTurn(1);
        Card player_card = G1.getPlayers().get(1).getHand().getCard(1);

        // testing adding a card to the correct pile (starting the pile)
        G1.playCard(1, "r", 1, "b");
        // making sure discard pile was incremented and correct card was added
        assertEquals(1, G1.getFireworks().get(2).getSize());
        assertEquals(player_card.getRank(), G1.getFireworks().get(2).getCards().get(0).getRank());
        assertEquals(player_card.getColour(), G1.getFireworks().get(2).getCards().get(0).getColour());
        // making sure player got proper draw
        assertEquals(1, G1.getPlayers().get(1).getHand().getCard(1).getRank());

        // setting player turn to be third player
        G1.setPlayerTurn(2);
        Card p3_card = G1.getPlayers().get(2).getHand().getCard(1);

        // testing adding a card to the wrong pile, therefore removing that card
        G1.playCard(1, "y", 5, "y");
        // making sure fireworks pile was not added to
        assertEquals(0, G1.getFireworks().get(1).getSize());
        // checking if card was discarded and correct card was added
        assertEquals(1, G1.getDiscardPile().getCards().size());
        assertEquals(p3_card.getRank(), G1.getDiscardPile().getCards().get(0).getRank());
        assertEquals(p3_card.getColour(), G1.getDiscardPile().getCards().get(0).getColour());
        // checking if fuse token was lost
        assertEquals(2, G1.getFuseTokens().getCount());

        // testing adding a card to the wrong pile, therefore removing that card (again)
        G1.playCard(2, "y", 4, "y");
        // making sure fireworks pile was not added to
        assertEquals(0, G1.getFireworks().get(1).getSize());
        // checking if card was discarded and correct card was added
        assertEquals(2, G1.getDiscardPile().getCards().size());
        assertEquals(4, G1.getDiscardPile().getCards().get(1).getRank());
        assertEquals("g", G1.getDiscardPile().getCards().get(1).getColour());
        // checking if fuse token was lost
        assertEquals(1, G1.getFuseTokens().getCount());


        // testing completion of a pile
        G1.setPlayerTurn(1);
        Card p1_card = G1.getPlayers().get(1).getHand().getCard(2);
        G1.playCard(2, "r", 2, "b");
        assertEquals(p1_card.getRank(), G1.getFireworks().get(2).getCards().get(1).getRank());

        p1_card = G1.getPlayers().get(1).getHand().getCard(3);
        G1.playCard(3, "r", 3, "b");
        assertEquals(p1_card.getRank(), G1.getFireworks().get(2).getCards().get(2).getRank());

        p1_card = G1.getPlayers().get(1).getHand().getCard(4);
        G1.playCard(4, "r", 4, "b");
        assertEquals(p1_card.getRank(), G1.getFireworks().get(2).getCards().get(3).getRank());

        p1_card = G1.getPlayers().get(1).getHand().getCard(5);
        G1.playCard(5, "r", 5, "b");
        assertEquals(p1_card.getRank(), G1.getFireworks().get(2).getCards().get(4).getRank());

    }

    @Test
    public void discardCardTest() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);
        // setting player turn to be second player
        G1.setPlayerTurn(1);

        // second player discarding a card
        G1.discardCard(1, "r", 1);

        // asserting card was added
        assertEquals("r", G1.getPlayers().get(1).getHand().getCard(1).getColour());
        // asserting discard pile was incremented and correct card was added
        assertEquals(1, G1.getDiscardPile().getCards().size());
        assertEquals("b", G1.getDiscardPile().getCards().get(0).getColour());
    }

    @Test
    public void giveInfo() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");

        G1.updatePlayersLeft(2);
        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);
        // setting player turn to be first player
        G1.setPlayerTurn(0);

        // player 1 gives info to player 2
        G1.giveInfo(1, "b");

        // making sure info was given for all cards
        assertEquals(true, G1.getPlayers().get(1).getHand().getCard(1).getInfo()[1]);
        assertEquals(true, G1.getPlayers().get(1).getHand().getCard(2).getInfo()[1]);
        assertEquals(true, G1.getPlayers().get(1).getHand().getCard(3).getInfo()[1]);
        assertEquals(true, G1.getPlayers().get(1).getHand().getCard(4).getInfo()[1]);
        assertEquals(true, G1.getPlayers().get(1).getHand().getCard(5).getInfo()[1]);
        // making sure info is correct
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(1).getColour());
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(2).getColour());
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(3).getColour());
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(4).getColour());
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(5).getColour());


        // giving rank information
        G1.giveInfo(1, "1");
        // making sure info was given
        assertEquals(new boolean[]{true, true}[0], G1.getPlayers().get(1).getHand().getCard(1).getInfo()[0]);

        // testing player running client getting info
        G1.setPlayerTurn(1);

        // player 1's imaginary hand {"r1", "r2", "r3", "r4", "r5"}

        // player 2 gives info to player 1
        // bool arr should be all true since all cards are "r"
        G1.giveInfo("r", new boolean[]{true, true, true, true, true});
        // making sure information is now known
        assertEquals(new boolean[]{false, true}[1], G1.getPlayers().get(0).getHand().getCard(1).getInfo()[1]);
        assertEquals(new boolean[]{false, true}[1], G1.getPlayers().get(0).getHand().getCard(2).getInfo()[1]);
        assertEquals(new boolean[]{false, true}[1], G1.getPlayers().get(0).getHand().getCard(3).getInfo()[1]);
        assertEquals(new boolean[]{false, true}[1], G1.getPlayers().get(0).getHand().getCard(4).getInfo()[1]);
        assertEquals(new boolean[]{false, true}[1], G1.getPlayers().get(0).getHand().getCard(5).getInfo()[1]);

        // making sure info is correct
        assertEquals("r", G1.getPlayers().get(0).getHand().getCard(1).getColour());
        assertEquals("r", G1.getPlayers().get(0).getHand().getCard(2).getColour());
        assertEquals("r", G1.getPlayers().get(0).getHand().getCard(3).getColour());
        assertEquals("r", G1.getPlayers().get(0).getHand().getCard(4).getColour());
        assertEquals("r", G1.getPlayers().get(0).getHand().getCard(5).getColour());

    }

    @Test
    public void addActionToLogTest() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.updatePlayersLeft(2);

        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);

        // setting player turn to be second player
        G1.setPlayerTurn(1);
        // testing log when playing a card
        G1.playCard(1, "r", 1, "b");

        // asserting that log was added to
        assertEquals("Play Card", G1.getLog().getActions().get(0).getMoveType());
        assertEquals(1, G1.getLog().getActions().get(0).getPlayer());
        assertEquals("Players handIndex: 1", G1.getLog().getActions().get(0).getMoveParams()[0]);
        assertEquals("Card colour: b", G1.getLog().getActions().get(0).getMoveParams()[1]);
        assertEquals("Card rank: 1", G1.getLog().getActions().get(0).getMoveParams()[2]);

        // testing player discarding card and action being added to the log
        G1.discardCard(1, "y", 1);

        // asserting that the log was added to
        assertEquals("Discard Card", G1.getLog().getActions().get(1).getMoveType());
        assertEquals(1, G1.getLog().getActions().get(1).getPlayer());
        assertEquals("Players handIndex: 1", G1.getLog().getActions().get(1).getMoveParams()[0]);
        assertEquals("Card colour: r", G1.getLog().getActions().get(1).getMoveParams()[1]);
        assertEquals("Card rank: 1", G1.getLog().getActions().get(1).getMoveParams()[2]);

        // testing giving info and action being added to the log
        G1.giveInfo(2, "g");

        // asserting that log was added to
        assertEquals("Give Info", G1.getLog().getActions().get(2).getMoveType());
        assertEquals("Players Index: 2", G1.getLog().getActions().get(2).getMoveParams()[0]);
        assertEquals("Property Known: g", G1.getLog().getActions().get(2).getMoveParams()[1]);
    }

    @Test
    public void toggleViews_eGame() {
        // test case 4
        // toggle discard and view twice making them set and unset
        G1.toggleDiscardView();
        G1.toggleDiscardView();
        G1.toggleLogView();
        G1.toggleLogView();

        assertEquals(false, G1.isDiscardVisible());
        assertEquals(false, G1.isLogVisible());

        // clear the game state (every field should be cleared)
        G1.endGame();

        assertEquals(true, G1.getPlayers().isEmpty());
        assertEquals(true, G1.getFireworks().isEmpty());
        assertEquals(50, G1.getDiscardPile().getCapacity());
        assertEquals(true, G1.getLog().getActions().isEmpty());
    }

    @Test
    public void Case2() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.updatePlayersLeft(2);

        String[][] hands =
          {
            {""},
            {"b1", "r2", "b3", "b4", "b5"},
            {"b1", "r2", "g3", "g2", "g1"},
          };

        G1.startGame(hands);
        // setting player turn to be first player
        G1.setPlayerTurn(0);

        // giving rank information
        G1.giveInfo(1, "1");
        // making sure info was given
        assertEquals(new boolean[]{true, true}[0], G1.getPlayers().get(1).getHand().getCard(1).getInfo()[0]);

        // making sure info is correct
        assertEquals("b", G1.getPlayers().get(1).getHand().getCard(1).getColour());

        // correct info tokens after give info used
        assertEquals(7, G1.getInformationTokens().getCount());

        // log is filled with first action
        assertEquals(false, G1.getLog().getActions().isEmpty());

        // player 2's turn
        G1.setPlayerTurn(1);

        // second player discarding a card and draws y4
        G1.discardCard(2, "y", 4);
        assertEquals("y", G1.getPlayers().get(1).getHand().getCard(2).getColour());

        // correct info tokens after discard card used
        assertEquals(8, G1.getInformationTokens().getCount());

        // discard pile should have a r2 now
        assertEquals("r", G1.getDiscardPile().getCards().get(0).getColour());
        assertEquals("Discard Card", G1.getLog().getActions().get(1).getMoveType());

        // p3's turn
        G1.setPlayerTurn(2);
        G1.playCard(1, "b", 4, "b");

        // fireworks pile should have the first card from p3
        assertEquals("b", G1.getFireworks().get(2).getColour());

        // back to p1
        G1.setPlayerTurn(0);
        assertEquals(0, G1.getPlayerTurn());
        assertEquals("Play Card", G1.getLog().getActions().get(2).getMoveType());
    }

    @Test
    public void StateChangeMsgTest() {
        // players joining
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        G1.enterGame(1, "sample", "none");
        // testing message sent when updatePlayersLeft is called
        G1.updatePlayersLeft(2);
//    System.out.println(G1.getStateChangeMap());
        assertEquals("2", G1.getStateChangeMap().get("Players left"));

        String[][] hands =
          {
            {""},
            {"b1", "b2", "b3", "b4", "b5"},
            {"g5", "g4", "g3", "g2", "g1"},
          };

        G1.startGame(hands);
        // setting player turn to be first player
        G1.setPlayerTurn(0);

        // testing message sent when startGame (players hands)
        assertEquals("hand: b1b2b3b4b5", G1.getStateChangeMap().get("Player1"));
        assertEquals("hand: g5g4g3g2g1", G1.getStateChangeMap().get("Player2"));

        // setting player turn to be second player
        G1.setPlayerTurn(1);
        G1.playCard(1, "r", 1, "");
        // testing message sent when playCard is called and card is played to the right pile
        assertEquals("b1", G1.getStateChangeMap().get("Fireb"));
        // testing map for player who's hand was changed
        assertEquals("hand: r1b2b3b4b5", G1.getStateChangeMap().get("Player1"));

        // testing if wrong card was played (should add TokenFuse and discard pile to map)
        G1.playCard(3, "r", 3, "");
        // testing map for discard pile
        assertEquals("b3", G1.getStateChangeMap().get("DiscardPile"));
        // testing map for new player hand who just played a card (again)
        assertEquals("hand: r1b2r3b4b5", G1.getStateChangeMap().get("Player1"));
        // testing map for fuse token count
        assertEquals("2", G1.getStateChangeMap().get("TokenFuse"));


        // testing map for discarding card
        G1.discardCard(2, "r", 2);
        assertEquals("b3b2", G1.getStateChangeMap().get("DiscardPile"));

        // testing map for giving info (p2 give info to p3 that all cards are green)
        G1.giveInfo(2, "g");
        assertEquals("Players Index: 2, Property Known: g", G1.getStateChangeMap().get("Give Info"));

        // test map for end game (return score)
        G1.endGame();
        assertEquals("1", G1.getStateChangeMap().get("Score"));
    }
}