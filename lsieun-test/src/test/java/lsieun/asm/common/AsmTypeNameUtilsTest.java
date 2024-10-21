package lsieun.asm.common;

import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.box.BoxUtils;
import org.junit.jupiter.api.Test;

class AsmTypeNameUtilsTest {
    @Test
    void testToClassName() {
        String text = "com/abc/Xyz";
        String className = AsmTypeNameUtils.toClassName(text);
        String internalName = AsmTypeNameUtils.toInternalName(text);
        String descriptor = AsmTypeNameUtils.toDescriptor(text);
        String jarEntryName = AsmTypeNameUtils.toJarEntryName(text);

        String[][] matrix = {
                {"className", className},
                {"internalName", internalName},
                {"descriptor", descriptor},
                {"jarEntryName", jarEntryName},
        };
        BoxUtils.printTable(matrix);
    }
}