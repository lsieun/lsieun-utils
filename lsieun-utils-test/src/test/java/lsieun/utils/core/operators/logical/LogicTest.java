package lsieun.utils.core.operators.logical;

//import lsieun.utils.match.text.TextMatch;
//import org.junit.jupiter.api.Test;
//
//import java.lang.invoke.MethodHandles;
//
//
//import static lsieun.utils.core.operators.logical.Logic.negate;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class LogicTest {
//    @Test
//    void testLogicNegate() {
//        String str = "You don't need wings to fly";
//        TextMatch instance = TextMatch.startsWith("You");
//
//        MethodHandles.Lookup lookup = TextMatch.lookup();
//        TextMatch match = negate(lookup, TextMatch.class, instance);
//        boolean flag = match.test(str);
//        assertFalse(flag);
//
//        TextMatch match2 = negate(lookup, TextMatch.class, match);
//        boolean flag2 = match2.test(str);
//        assertTrue(flag2);
//    }
//}
