package lsieun.asm.consumer;

import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.sam.consumer.InsnInvokeConsumerGallery;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;
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
                .withByteArrayProcessor(func)
                .run();
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