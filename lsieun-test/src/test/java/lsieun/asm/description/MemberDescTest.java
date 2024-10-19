package lsieun.asm.description;

import lsieun.asm.description.MemberDesc;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberDescTest {
    @Test
    void testParseLine() {
        String line = "com.intellij.ide.b.m.M::b:(Lcom/intellij/ide/b/m/L;J)V";
        MemberDesc desc = MemberDesc.of(line);
        System.out.println(desc);
    }

    @Test
    void testParseMultipleLines() {
        List<String> lines = List.of(
                "com.intellij.ide.b.m.M::b:(Lcom/intellij/ide/b/m/L;J)V",
                "com.intellij.ide.b.m.M::b:(Lcom/intellij/ide/b/m/L;J)V"
        );
        List<MemberDesc> memberDescList = MemberDesc.ofList(lines);
        assertEquals(memberDescList.size(), 1);
    }
}
