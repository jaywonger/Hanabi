package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void createCardTest(){
        Card test = new Card(5, "b", true, false);
        boolean[] info = {true, false};

        assertEquals(5, test.getRank());
        assertEquals("b", test.getColour());
        assertArrayEquals(info, test.getInfo());
    }

    @Test
    void compareCardTest() {
        Card c1 = new Card(4, "b", true, true);
        Card c2 = new Card(4, "b", true, true);
        Card c3 = new Card (2, "b", true, true);
        Card c4 = new Card (4, "r", true, true);

        assertTrue(c1.equals(c2));
        assertTrue(c1.sameColour(c3));
        assertFalse(c1.sameRank(c3));
        assertFalse(c1.sameColour(c4));
        assertTrue(c1.sameRank(c4));
    }
}