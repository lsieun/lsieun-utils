package lsieun.asm.search;

import lsieun.asm.match.MatchItem;
import org.junit.jupiter.api.Test;

class MatchItemTest {
    @Test
    void testParse() {
        String str = "FIELD   java/lang/System  out:Ljava/io/PrintStream;";
        MatchItem matchItem = MatchItem.parse(str);
        System.out.println(matchItem);
    }
}