package lsieun.utils.asm.common;

import lsieun.utils.asm.code.AsmCodeFragment;
import lsieun.utils.asm.code.StdAsmCodeFragmentForPrint;
import lsieun.utils.asm.common.transformation.ClassFileAdviceUtils;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class ClassFileAdviceUtilsTest {
    @Test
    void testApply() {
        Path path = ResourceUtils.readFilePath(HelloWorldForAdviceUtils.class);

        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName("test");
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER,
                StdAsmCodeFragmentForPrint.STACK_TRACE
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_SIMPLE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;

        ByteArrayProcessor func = bytes ->
                ClassFileAdviceUtils.apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withFunction(func);

    }

    static class HelloWorldForAdviceUtils {
        public static void test() {
            System.out.println("Hello Test");
            throw new RuntimeException("abc");
        }

        public static void main(String[] args) {
            test();
        }
    }
}
