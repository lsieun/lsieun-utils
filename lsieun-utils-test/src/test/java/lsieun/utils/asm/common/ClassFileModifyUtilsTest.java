package lsieun.utils.asm.common;

import lsieun.utils.asm.common.transformation.ClassFileModifyUtils;
import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayTank;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

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
        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.printMethodInfo(
                bytes, methodMatch, options);
        ByteArrayProcessorBuilder.forFile().withFile(path).withFunction(func);
    }

    @Test
    void testEmptyAndPrint() {
        Path path = ResourceUtils.readFilePath(HelloWorldForEmptyAndPrint.class);
        ByteArrayTank tank = ByteArrayTank.of(path);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName("test");
        EnumSet<MethodBodyInfoType> options = MethodBodyInfoType.STACK_TRACE_ONLY;

        ByteArrayProcessorBuilder.builder()
                .withFromTank(tank)
                .withToTank(tank)
                .withFunction(bytes -> ClassFileModifyUtils.emptyAndPrint(bytes, methodMatch, null, options))
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
        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.modifyInsnInvoke(
                bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer);
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withFunction(func);
    }
}