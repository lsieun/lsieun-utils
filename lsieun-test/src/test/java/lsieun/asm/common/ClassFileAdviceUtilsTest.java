package lsieun.asm.common;

import lsieun.asm.sam.consumer.AsmCodeFragment;
import lsieun.asm.sam.consumer.StdAsmCodeFragmentForPrint;
import lsieun.asm.common.transformation.ClassFileAdviceUtils;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;
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
                .withByteArrayProcessor(func)
                .run();

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
