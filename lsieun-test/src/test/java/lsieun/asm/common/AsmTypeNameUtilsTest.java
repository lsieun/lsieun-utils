package lsieun.asm.common;

import lsieun.asm.core.AsmTypeNameUtils;
import org.junit.jupiter.api.Test;

class AsmTypeNameUtilsTest {
    @Test
    void testToClassName() {
        String text = "com/jetbrains/b/Q/B";
        String className = AsmTypeNameUtils.toClassName(text);
        System.out.println(className);
    }
}