package model;

import java.util.*;

/**
 * Stores all the actions made in a game of Hanabi so far. Actions are added
 * in chronological order and are cleared at the end of the game.
 */
public class Log {
    private List<Action> actions;

    /** Creates a new empty log */
    public Log () {
        actions = new LinkedList<>();
    }

    /** Returns the list of actions in the log */
    public List<Action> getActions() {
      return this.actions;
    }

    /**
     * Adds a given action to the log
     * @param act The action to add
     */
    public void addAction (Action act) {
        this.actions.add(act);
    }

    /** Removes all of the actions from the log */
    public void clear() {
        this.actions.clear();
    }
}
