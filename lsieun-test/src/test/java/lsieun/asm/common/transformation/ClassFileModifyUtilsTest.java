package lsieun.asm.common.transformation;

import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.core.match.text.TextMatch;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;
import lsieun.core.processor.bytes.ByteArrayTank;
import org.junit.jupiter.api.Test;

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

    private void printMethodInfo(Class<?> clazz,
                                 MethodInfoMatch methodMatch,
                                 Set<MethodBodyInfoType> options) {
        Path path = ResourceUtils.readFilePath(clazz);
        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.printMethodInfo(
                bytes, methodMatch, options);
        ByteArrayProcessorBuilder.forFile().withFile(path).withByteArrayProcessor(func);
    }

    static class HelloWorldForPrint {
        public static void test() {
            System.out.println("test");
        }

        public static void main(String[] args) {
            test();
        }
    }

    @Test
    void testEmptyMethodToTrue() {
        Path path = ResourceUtils.readFilePath(HelloWorldForEmptyToTrue.class);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(TARGET_METHOD_NAME);
        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.emptyMethodBody(bytes, methodMatch, true);
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withByteArrayProcessor(func);
    }

    static class HelloWorldForEmptyToTrue {
        static boolean test() {
            System.out.println("test");
            return false;
        }

        public static void main(String[] args) {
            boolean flag = test();
            System.out.println("flag = " + flag);
        }
    }

    @Test
    void testEmptyAndPrint() {
        Path path = ResourceUtils.readFilePath(HelloWorldForEmptyAndPrint.class);
        ByteArrayTank tank = ByteArrayTank.of(path);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName("test");
        EnumSet<MethodBodyInfoType> options = MethodBodyInfoType.STACK_TRACE_ONLY;

        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.emptyAndPrint(bytes, methodMatch, null, options);

        ByteArrayProcessorBuilder.builder()
                .withFromTank(tank)
                .withToTank(tank)
                .withByteArrayProcessor(func);
    }


    static class HelloWorldForEmptyAndPrint {
        public void test() {
            System.out.println("test");
        }
    }





    @Test
    void testPatchOneInsnByReturnMethod() {
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
                .withByteArrayProcessor(func);
    }

    @Test
    void testAddToString() {
        Class<?> clazz = HelloWorldForToString.class;
        Path path = ResourceUtils.readFilePath(clazz);
        ByteArrayProcessor func = ClassFileModifyUtils::addToString;
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withByteArrayProcessor(func);
    }

    @SuppressWarnings("all")
    static class HelloWorldForToString {
        private boolean boolValue = true;
        private short shortValue = 1;
        private char charValue = 'B';
        private int intValue = 3;
        private long longValue = 4;
        private float floatValue = 5;
        private double doubleValue = 6;
        private String stringValue = "7";
        private Object objectValue = new Object();

        public static void main(String[] args) {
            HelloWorldForToString obj = new HelloWorldForToString();
            System.out.println(obj);
        }
    }
}