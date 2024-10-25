package lsieun.core.match;

import lsieun.asm.sam.match.AsmTypeMatch;
import lsieun.asm.sam.match.MemberInfoMatch;
import lsieun.core.sam.match.text.TextMatch;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchLogicTest {
    @Test
    void testGetTrueInstance() {
        MemberInfoMatch memberMatch = MemberInfoMatch.LOGIC.alwaysTrue();
        assertNotNull(memberMatch);
        System.out.println(memberMatch);
    }

    @Test
    void testGetFalseInstance() {
        MemberInfoMatch memberMatch = MemberInfoMatch.LOGIC.alwaysFalse();
        assertNotNull(memberMatch);
        System.out.println(memberMatch);
    }

    @Test
    void testGetTwoEnumInstance() {
        MemberInfoMatch true1 = MemberInfoMatch.LOGIC.alwaysTrue();
        MemberInfoMatch true2 = MemberInfoMatch.LOGIC.alwaysTrue();
        assertEquals(true1, true2);
        System.out.println(true1);
        System.out.println(true2);
    }

    // region logic and
    @Test
    void testLogicAndTrueUsingTypeMatchByMethodInvoke() {
        Type t = Type.getType(String.class);

        LogicAssistant<AsmTypeMatch> logic = AsmTypeMatch.LOGIC;
        AsmTypeMatch asmTypeMatch = logic.and(
                AsmTypeMatch.bySimpleName(TextMatch.startsWith("S")),
                AsmTypeMatch.bySimpleName(TextMatch.endsWith("g"))
        );
        boolean flag = asmTypeMatch.test(t);
        System.out.println(flag);
    }

    @Test
    void testLogicAndFalseUsingTypeMatchByMethodInvoke() {
        Type t = Type.getType(String.class);
        List<AsmTypeMatch> list = List.of(
                AsmTypeMatch.bySimpleName(TextMatch.startsWith("S")),
                AsmTypeMatch.bySimpleName(TextMatch.endsWith("x"))
        );
        LogicAssistant<AsmTypeMatch> logic = AsmTypeMatch.LOGIC;
        AsmTypeMatch asmTypeMatch = logic.and(list);
        boolean flag = asmTypeMatch.test(t);
        assertFalse(flag);
    }

    @Test
    void testLogicAndTrueUsingTextMatchByMethodInvoke() {
        String str = "You don't need wings to fly";
        List<TextMatch> list = List.of(
                TextMatch.startsWith("You"),
                TextMatch.endsWith("fly")
        );
        TextMatch match = TextMatch.LOGIC.and(list);
        boolean flag = match.test(str);
        assertTrue(flag);
    }

    @Test
    void testLogicAndFalseUsingTextMatchByMethodInvoke() {
        String str = "You don't need wings to fly";
        List<TextMatch> list = List.of(
                TextMatch.startsWith("You"),
                TextMatch.endsWith("flx")
        );
        TextMatch match = TextMatch.LOGIC.and(list);
        boolean flag = match.test(str);
        assertFalse(flag);
    }
    // endregion

    // region logic or
    @Test
    void testLogicOrTrueUsingTypeMatchByMethodInvoke() {
        Type t = Type.getType(String.class);
        List<AsmTypeMatch> list = List.of(
                AsmTypeMatch.bySimpleName(TextMatch.startsWith("S")),
                AsmTypeMatch.bySimpleName(TextMatch.endsWith("x"))
        );
        LogicAssistant<AsmTypeMatch> logic = AsmTypeMatch.LOGIC;
        AsmTypeMatch asmTypeMatch = logic.or(list);
        boolean flag = asmTypeMatch.test(t);
        assertTrue(flag);
    }

    @Test
    void testLogicOrFalseUsingTypeMatchByMethodInvoke() {
        Type t = Type.getType(String.class);
        List<AsmTypeMatch> list = List.of(
                AsmTypeMatch.bySimpleName(TextMatch.startsWith("A")),
                AsmTypeMatch.bySimpleName(TextMatch.endsWith("x"))
        );
        LogicAssistant<AsmTypeMatch> logic = AsmTypeMatch.LOGIC;
        AsmTypeMatch asmTypeMatch = logic.or(list);
        boolean flag = asmTypeMatch.test(t);
        assertFalse(flag);
    }
    // endregion

    // region logic negate
    @Test
    void testLogicNegate() {
        String str = "You don't need wings to fly";
        TextMatch instance = TextMatch.startsWith("You");

        LogicAssistant<TextMatch> logic = TextMatch.LOGIC;
        TextMatch match = logic.not(instance);
        boolean flag = match.test(str);
        assertFalse(flag);

        TextMatch match2 = logic.not(match);
        boolean flag2 = match2.test(str);
        assertTrue(flag2);
    }
    // endregion
}