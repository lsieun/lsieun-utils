package lsieun.asm.sam.consumer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public interface MethodVisitorConsumer extends Consumer<MethodVisitor>, Opcodes {
}
