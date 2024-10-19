package lsieun.base.operators.logical;


import lsieun.core.match.LogicAssistant;
import lsieun.core.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogicTest {
    @Test
    void testLogicNegate() {
        String str = "You don't need wings to fly";
        TextMatch instance = TextMatch.startsWith("You");

        LogicAssistant<TextMatch> logic = TextMatch.logic();
        TextMatch match = logic.not(instance);
        boolean flag = match.test(str);
        assertFalse(flag);

        TextMatch match2 = logic.not(match);
        boolean flag2 = match2.test(str);
        assertTrue(flag2);
    }
}
