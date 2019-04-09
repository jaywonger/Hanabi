package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;



class ActionTest {

    @Test
    void actionTest() {
//Simply create an action and check that the getters work
        String[] params = {"2", "b3"};
        Action test  = new Action(1, "Play", params);
        assertEquals(1, test.getPlayer());
        assertEquals("Play", test.getMoveType());
        assertArrayEquals(params, test.getMoveParams());

    }
}