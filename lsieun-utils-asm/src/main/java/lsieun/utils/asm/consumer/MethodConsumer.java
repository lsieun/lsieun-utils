package lsieun.utils.asm.consumer;

import org.objectweb.asm.MethodVisitor;

@FunctionalInterface
public interface MethodConsumer {
    void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc);
}
