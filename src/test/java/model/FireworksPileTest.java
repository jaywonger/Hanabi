package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FireworksPileTest {

    @Test
    void fireworkTest(){
        Card r1 = new Card(1, "r", true, true);
        Card r2 = new Card(2, "r", true, true);
        Card y3 = new Card(3, "y", true, true);
        Card r4 = new Card(4, "r" ,true, true);

        //Test that FireworkPile creation works
        FireworksPile test = new FireworksPile("r");
        assertEquals(5, test.getCapacity());
        assertEquals(0, test.getSize());
        assertEquals("r", test.getColour());

        //Test that adding correct colour and rank cards works
        List<Card> expectedCards = Arrays.asList(r1, r2);
        test.addCard(r1);
        assertEquals(1, test.getSize());
        test.addCard(r2);
        assertEquals(2, test.getSize());
        assertIterableEquals(expectedCards, test.getCards());

        //Test that adding wrong colour or rank cards throws exceptions
        assertThrows(IllegalArgumentException.class, () -> test.addCard(y3));
        assertThrows(IllegalArgumentException.class, () -> test.addCard(r4));
    }
}