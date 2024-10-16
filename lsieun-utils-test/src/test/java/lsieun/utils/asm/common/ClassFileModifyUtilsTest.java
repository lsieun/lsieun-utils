package lsieun.utils.asm.common;

import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import lsieun.utils.core.bytes.ByteArrayTank;
import lsieun.utils.core.bytes.ByteArrayThreePhase;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

class ClassFileModifyUtilsTest {
    final String TARGET_METHOD_NAME = "test";

    @Test
    void testPrintMethodInfo() {
        Class<?> clazz = HelloWorldForPrint.class;
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(
                TextMatch.equals(TARGET_METHOD_NAME)
        );
        Set<MethodBodyInfoType> options = EnumSet.of(
                MethodBodyInfoType.ENTER,
                MethodBodyInfoType.THREAD_INFO,
                MethodBodyInfoType.EXIT
        );

        printMethodInfo(clazz, methodMatch, options);
    }

    static class HelloWorldForPrint {
        public static void test() {
            System.out.println("test");
        }

        public static void main(String[] args) {
            test();
        }
    }

    private void printMethodInfo(Class<?> clazz,
                                 MethodInfoMatch methodMatch,
                                 Set<MethodBodyInfoType> options) {
        Path path = ResourceUtils.readFilePath(clazz);
        Function<byte[], byte[]> func = bytes -> ClassFileModifyUtils.printMethodInfo(
                bytes, methodMatch, options);
        ByteArrayThreePhase.forFile(path, func);
    }

    @Test
    void testEmptyAndPrint() throws Exception {
        Path path = ResourceUtils.readFilePath(HelloWorldForEmptyAndPrint.class);
        ByteArrayTank.ForFileSystem tank = ByteArrayTank.ForFileSystem.of(path);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName("test");
        EnumSet<MethodBodyInfoType> options = MethodBodyInfoType.STACK_TRACE_ONLY;

        ByteArrayThreePhase.builder()
                .withFromTank(tank)
                .withToTank(tank)
                .withFunction(bytes -> ClassFileModifyUtils.emptyAndPrint(bytes, methodMatch, options))
                .run();
    }


    static class HelloWorldForEmptyAndPrint {
        public void test() {
            System.out.println("test");
        }
    }


    @Test
    void testPatchOneInsnByReturnMethod() throws IOException {
        Class<?> clazz = HelloWorldForPrint.class;
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(
                TextMatch.equals(TARGET_METHOD_NAME)
        );
        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.ByReturn.METHOD;
        InsnInvokeConsumer insnInvokeConsumer = InsnInvokeConsumer.ThreePhase.builder()
                .withPreInvokeConsumer()
                .withOnInvokeConsumer(InsnInvokeConsumer.Default.INSTANCE)
                .withPostInvokeConsumer(InsnInvokeConsumer.Print.DUP_AND_PRINT_VALUE_ON_STACK);
        patchInsnInvoke(clazz, methodMatch, insnInvokeMatch, insnInvokeConsumer);
    }

    private void patchInsnInvoke(Class<?> clazz,
                                 MethodInfoMatch methodMatch,
                                 InsnInvokeMatch insnInvokeMatch,
                                 InsnInvokeConsumer insnInvokeConsumer) {
        Path path = ResourceUtils.readFilePath(clazz);
        Function<byte[], byte[]> func = bytes -> ClassFileModifyUtils.patchInsnInvoke(
                bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer);
        ByteArrayThreePhase.forFile(path, func);
    }
}