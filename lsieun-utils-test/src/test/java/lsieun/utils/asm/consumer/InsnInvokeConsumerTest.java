package lsieun.utils.asm.consumer;

import lsieun.utils.asm.core.AsmTypeNameUtils;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.AsmTypeMatch;
import lsieun.utils.asm.common.ClassFileModifyUtils;
import lsieun.utils.core.bytes.ByteArrayThreePhase;
import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import lsieun.utils.core.log.LogLevel;
import lsieun.utils.core.log.Logger;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

class InsnInvokeConsumerTest {
    final String TARGET_METHOD_NAME = "test";

    @Test
    void testPopFromStack() throws IOException {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Class<?> clazz = HelloWorldForPop.class;
        byte[] bytes = ResourceUtils.readClassBytes(clazz);
        byte[] newBytes = ClassFileModifyUtils.patchInsnInvoke(bytes,
                MethodInfoMatch.Bool.TRUE,
                InsnInvokeMatch.byReturnType(AsmTypeMatch.byType(void.class)),
                InsnInvokeConsumer.Common.POP_FROM_STACK
        );
        Type t = Type.getType(clazz);
        Path path = Path.of(".", "target", "classes", AsmTypeNameUtils.toJarEntryName(t));
        FileContentUtils.writeBytes(path, newBytes);
    }

    private static class HelloWorldForPop {
        void test() {
            System.out.println("Hello World");
        }
    }

    @Test
    void testPrintInvokeMethodInsnParamsAndReturn() {
        Path path = ResourceUtils.readFilePath(InsnInvokeConsumerGalleryTest.HelloWorldForPrintInvokeMethodInsnParamsAndReturn.class);

        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(TARGET_METHOD_NAME);
        Function<byte[], byte[]> func = bytes ->
                ClassFileModifyUtils.patchInsnInvoke(
                        bytes, methodMatch, InsnInvokeMatch.All.INSTANCE,
                        InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn()
                );
        ByteArrayThreePhase.forFile(path, func);
    }
}