package lsieun.utils.asm.search;

import lsieun.utils.asm.match.result.MatchItem;
import org.junit.jupiter.api.Test;

class MatchItemTest {
    @Test
    void testParse() {
        String str = "FIELD   java/lang/System  out:Ljava/io/PrintStream;";
        MatchItem matchItem = MatchItem.parse(str);
        System.out.println(matchItem);
    }
}