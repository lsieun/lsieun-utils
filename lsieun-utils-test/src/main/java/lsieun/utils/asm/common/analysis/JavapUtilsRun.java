package lsieun.utils.asm.common.analysis;

import lsieun.utils.asm.core.AsmTypeNameUtils;

public class JavapUtilsRun {
    public static void main(String[] args) {
        String jarName = "product.jar";
        String classname = AsmTypeNameUtils.toClassName("com.intellij.ide.b.m.R");
        JavapUtils.print(classname);
        JavapUtils.print(jarName, classname);
    }
}
