package model;

/**
 * Represents a counter for a set of either fuse or information tokens. The
 * counter increases and decreases by one as the game progresses.
 */
public class Token {
    private int numTokens;

    /**
     * Creates a token counter with a starting number of tokens
     * @param n The starting number of tokens
     */
    public Token(int n) {
        this.numTokens = n;
    }

    /** Increases the number of tokens by one */
    public void addToken() {
        this.numTokens++;
    }

    /**
     * Decreases the number of tokens by one
     * @throws IllegalStateException If there are no tokens to remove
     */
    public void removeToken() {
        if (numTokens <= 0) {
            throw new IllegalStateException("No tokens left to remove");
        }
        this.numTokens--;
    }

    /** Returns the number of tokens */
    public int getCount() {
        return this.numTokens;
    }
}
