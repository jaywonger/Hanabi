package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void tokenTest() {
        //Test token creation
        Token test  = new Token(0);
        assertEquals(0, test.getCount());

        //Test simple addition and removal of tokens
        test.addToken();
        assertEquals(1, test.getCount());
        test.removeToken();
        assertEquals(0, test.getCount());

        //Test exception throwing on removing from an empty token pile
        assertThrows(IllegalStateException.class, () -> test.removeToken());
    }
}