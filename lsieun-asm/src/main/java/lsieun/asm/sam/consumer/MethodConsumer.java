package lsieun.asm.sam.consumer;

import org.objectweb.asm.MethodVisitor;

@FunctionalInterface
public interface MethodConsumer {
    void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc);
}
