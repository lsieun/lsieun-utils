package lsieun.utils.asm.description;

import org.junit.jupiter.api.Test;

public class MemberDescTest {
    @Test
    void testParseLine() {
        String line = "com.intellij.ide.b.m.M::b:(Lcom/intellij/ide/b/m/L;J)V";
        MemberDesc desc = MemberDesc.of(line);
        System.out.println(desc);
    }
}
