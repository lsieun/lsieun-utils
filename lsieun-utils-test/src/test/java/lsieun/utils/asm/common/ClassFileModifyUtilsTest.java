package lsieun.utils.asm.common;

import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.visitor.modify.method.MethodBodyInfoType;
import lsieun.utils.core.bytes.ByteArrayTank;
import lsieun.utils.core.bytes.ByteArrayThreePhase;
import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;

class ClassFileModifyUtilsTest {
    @Test
    void testPrintMethod() throws IOException {
        String relativePath = "sample/HelloWorld.class";
        Path path = ResourceUtils.readFilePath(relativePath);
        byte[] bytes1 = FileContentUtils.readBytes(path);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(
                TextMatch.equals("test")
        );
        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.ByReturn.METHOD;
        InsnInvokeConsumer insnInvokeConsumer = InsnInvokeConsumer.ThreePhase.builder()
                .withPreInvokeConsumer()
                .withOnInvokeConsumer(InsnInvokeConsumer.Default.INSTANCE)
                .withPostInvokeConsumer(InsnInvokeConsumer.Print.DUP_AND_PRINT_VALUE_ON_STACK);
        byte[] bytes2 = ClassFileModifyUtils.patchInsnInvoke(bytes1, methodMatch, insnInvokeMatch, insnInvokeConsumer);
        FileContentUtils.writeBytes(path, bytes2);
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
}