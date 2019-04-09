package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void testHandCreation() {
        List<Card> emptyHand = Arrays.asList(null, null, null, null);
        Hand testHand = new Hand(4);
        assertEquals(4, testHand.getCapacity());
        assertEquals(0, testHand.getSize());
        assertIterableEquals(emptyHand, testHand.getCards());
    }

    @Test
    void testAddCard(){
        Hand testHand = new Hand(4);
        Card card1 = new Card(1, "red", true, true);
        Card card2 = new Card(2, "blue", true, true);
        Card card3 = new Card(3, "green", true, true);
        Card card4 = new Card(4, "yellow", true, true);

        //Test that the first 2 cards added with addCard(c) are inserted in
        // order
        testHand.addCard(card1);
        assertNotNull(testHand.getCards().get(0),
          "Position 1 should not be null");
        assertEquals(1, testHand.getSize());
        testHand.addCard(card2);
        assertNotNull(testHand.getCards().get(1),
          "Position 2 should not be null");
        assertEquals(2, testHand.getSize());

        //Test that adding the next 2 cards with addCard(c, i)
        testHand.addCard(card4, 4);
        assertNotNull(testHand.getCards().get(3),
          "Position 4 should not be null");
        assertEquals(3, testHand.getSize());
        testHand.addCard(card3, 3);
        assertNotNull(testHand.getCards().get(2),
          "Position 3 should not be null");
        assertEquals(4, testHand.getSize());

        //Test that using addCard(c) on a full hand throws an exception
        assertThrows(IllegalStateException.class,
          () -> testHand.addCard(card1), "addCard did not throw exception");

        //Test that adding in a filled position doesn't override it
        assertThrows(IllegalStateException.class,
          () -> testHand.addCard(card1, 4),
          "Position 4 should not have been changed");
    }

    @Test
    void testGetCard(){
        Hand testHand = new Hand(4);
        Card card1 = new Card(1, "red", true, true);
        Card card2 = new Card(2, "blue", true, true);
        Card card3 = new Card(3, "green", true, true);
        Card card4 = new Card(4, "yellow", true, true);

        //Test that adding 4 cards in order and getting them returns the same
        // cards
        testHand.addCard(card1, 1);
        testHand.addCard(card2, 2);
        testHand.addCard(card3, 3);
        testHand.addCard(card4, 4);
        assertEquals(card1, testHand.getCard(1), "should have returned card1");
        assertEquals(card2, testHand.getCard(2), "should have returned card2");
        assertEquals(card3, testHand.getCard(3), "should have returned card3");
        assertEquals(card4, testHand.getCard(4), "should have returned card4");

        //Test for thrown exceptions when given an invalid index on either side
        assertThrows(ArrayIndexOutOfBoundsException.class,
          () -> testHand.getCard(5), "getCard did not throw exception");
        assertThrows(ArrayIndexOutOfBoundsException.class,
          () -> testHand.getCard(0), "getCard did not throw exception");
    }

    @Test
    void testRemoveCard(){
        Hand testHand = new Hand(4);
        Card card1 = new Card(1, "red", true, true);
        Card card2 = new Card(2, "blue", true, true);
        Card card3 = new Card(3, "green", true, true);
        Card card4 = new Card(4, "yellow", true, true);
        testHand.addCard(card1, 1);
        testHand.addCard(card2, 2);
        testHand.addCard(card3, 3);
        testHand.addCard(card4, 4);
        assertEquals(4, testHand.getSize());

        //Test that removing a card makes its position null and that trying
        // to remove it again raises an exception
        testHand.removeCard(2);
        assertEquals(3, testHand.getSize());
        assertNull(testHand.getCard(2), "Card's position should now be null");
        assertThrows(IllegalStateException.class,
          () -> testHand.removeCard(2),
          "removeCard didn't throw exception");
    }
}