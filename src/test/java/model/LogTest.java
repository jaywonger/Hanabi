package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class LogTest {

    @Test
    void logTest() {
        String[] params = {"2", "b3"};
        Action act = new Action(1, "Play", params);
        List<Action> emptyList = Collections.emptyList();
        List<Action> actList = Arrays.asList(act);

        //Check that log creation works
        Log test = new Log();
        assertIterableEquals(emptyList, test.getActions());

        //Check that adding actions and clearnig the log work
        test.addAction(act);
        assertIterableEquals(actList, test.getActions());
        test.clear();
        assertIterableEquals(emptyList, test.getActions());
    }
}