package lsieun.utils.asm.consumer;

import lsieun.utils.asm.common.transformation.ClassFileModifyUtils;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class InsnInvokeConsumerGalleryTest {
    final String TARGET_METHOD_NAME = "test";

    @Test
    void testPrintInvokeMethodInsnParamsAndReturn() {
        Path path = ResourceUtils.readFilePath(HelloWorldForPrintInvokeMethodInsnParamsAndReturn.class);

        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(TARGET_METHOD_NAME);
        ByteArrayProcessor func = bytes ->
                ClassFileModifyUtils.modifyInsnInvoke(
                        bytes, methodMatch, InsnInvokeMatch.All.INSTANCE,
                        InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn()
                );
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withFunction(func);
    }

    static class HelloWorldForPrintInvokeMethodInsnParamsAndReturn {
        public static void test() {
            String text = "You don't need wings to fly";
            String str = text.substring(10, 20);
            System.out.println(str);
        }

        public static void main(String[] args) {
            test();
        }
    }
}