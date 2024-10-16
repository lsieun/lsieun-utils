package lsieun.utils.asm.consumer;

import lsieun.utils.asm.common.ClassFileModifyUtils;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.bytes.ByteArrayThreePhase;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.function.Function;

class InsnInvokeConsumerGalleryTest {
    final String TARGET_METHOD_NAME = "test";

    @Test
    void testPrintInvokeMethodInsnParamsAndReturn() {
        Path path = ResourceUtils.readFilePath(HelloWorldForPrintInvokeMethodInsnParamsAndReturn.class);

        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(TARGET_METHOD_NAME);
        Function<byte[], byte[]> func = bytes ->
                ClassFileModifyUtils.patchInsnInvoke(
                        bytes, methodMatch, InsnInvokeMatch.All.INSTANCE,
                        InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn()
                );
        ByteArrayThreePhase.forFile(path, func);
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