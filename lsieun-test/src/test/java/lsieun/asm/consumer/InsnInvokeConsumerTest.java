package lsieun.asm.consumer;

import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.sam.consumer.InsnInvokeConsumer;
import lsieun.asm.sam.consumer.InsnInvokeConsumerGallery;
import lsieun.asm.sam.match.AsmTypeMatch;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.io.file.FileContentUtils;
import lsieun.base.io.resource.ResourceUtils;
import lsieun.base.log.LogLevel;
import lsieun.base.log.Logger;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import java.nio.file.Path;

class InsnInvokeConsumerTest {
    final String TARGET_METHOD_NAME = "test";

    @Test
    void testPopFromStack() {
        Logger.CURRENT_LEVEL = LogLevel.DEBUG;
        Class<?> clazz = HelloWorldForPop.class;
        byte[] bytes = ResourceUtils.readClassBytes(clazz);
        byte[] newBytes = ClassFileModifyUtils.modifyInsnInvoke(bytes,
                MethodInfoMatch.LOGIC.alwaysTrue(),
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
        ByteArrayProcessor func = bytes ->
                ClassFileModifyUtils.modifyInsnInvoke(
                        bytes, methodMatch, InsnInvokeMatch.LOGIC.alwaysTrue(),
                        InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn()
                );
        ByteArrayProcessorBuilder.forFile()
                .withFile(path)
                .withByteArrayProcessor(func)
                .run();
    }
}