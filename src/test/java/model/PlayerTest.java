package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getHandTest() {
        Card c1 = new Card(1, "r", true, true);
        Card c2 = new Card(1, "r", true, true);
        Card c3 = new Card(1, "r", true, true);
        Card c4 = new Card(1, "r", true, true);
        Card c5 = new Card(1, "r", true, true);
        List<Card> emptyHand = Arrays.asList(null, null, null, null, null);
        List<Card> fullHand = Arrays.asList(c1, c2, c3, c4, c5);

        //Test that a new Player's hand exists, has the right size, and is
        // empty
        Player testPlayer = new Player(5);
        assertNotNull(testPlayer.getHand());
        assertEquals(0, testPlayer.getHand().getSize());
        assertIterableEquals(emptyHand, testPlayer.getHand().getCards());

        //Test that a Player's hand can be initialized properly with the
        // abstract addCard() method
        testPlayer.getHand().addCard(c1);
        testPlayer.getHand().addCard(c2);
        testPlayer.getHand().addCard(c3);
        testPlayer.getHand().addCard(c4);
        testPlayer.getHand().addCard(c5);
        assertEquals(5, testPlayer.getHand().getSize());
        assertIterableEquals(fullHand, testPlayer.getHand().getCards());

        //No further tests for Player, since any other would duplicate Hand
        // tests
    }
}