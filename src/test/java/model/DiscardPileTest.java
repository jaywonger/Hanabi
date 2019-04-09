package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscardPileTest {

    @Test
    void testAddAndGetCards(){
        DiscardPile tester = new DiscardPile(2);
        assertEquals(2, tester.getCapacity());
        Card card1 = new Card(1, "red", true, true);
        Card card2 = new Card(2, "blue", true, true);

        //Test that cards get added to the discard pile in order and throws
        // an exception on a full pile
        tester.addCard(card1);
        assertEquals(card1, tester.getCards().get(0), "card1 should have been" +
                " added to the first pos of cards[]");
        assertEquals(1, tester.getSize());
        tester.addCard(card2);
        assertEquals(card2, tester.getCards().get(1), "card2 should have been" +
                " added to the second pos of cards[]");
        assertEquals(2, tester.getSize());
        assertThrows(IllegalStateException.class, () -> tester.addCard(card1)
          , "Added a card to a full pile");
    }

    @Test
    void testClear(){
        DiscardPile tester = new DiscardPile(5);
        Card card = new Card(1, "red", true, true);
        tester.addCard(card);
        List<Card> emptyList = Collections.emptyList();
        List<Card> refilledList = Arrays.asList(card);

        //Test that clear removes all of the cards and does nothing after
        tester.clear();
        assertIterableEquals(emptyList, tester.getCards(),
          "DiscardPile should be empty");
        tester.clear();
        assertIterableEquals(emptyList, tester.getCards(),
          "DiscardPile should be empty");

        //Test that you can add a card again after clearing the pile
        tester.addCard(card);
        assertIterableEquals(refilledList, tester.getCards(),
          "DiscardPile should have 1 card again");
    }
}