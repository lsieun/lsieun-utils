package lsieun.asm.consumer;

import jakarta.annotation.Nonnull;
import lsieun.asm.insn.AsmInsnUtilsForDesc;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;

public class MethodVisitorStringBuilder implements TextBuilder.Init, TextBuilder.WithAppend, TextBuilder.ToText, Opcodes {
    private final MethodVisitor mv;

    public MethodVisitorStringBuilder(@Nonnull MethodVisitor mv) {
        Objects.requireNonNull(mv);
        this.mv = mv;
    }

    @Override
    public TextBuilder.WithAppend init() {
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        return this;
    }


    @Override
    public TextBuilder.WithAppend append(String str) {
        mv.visitLdcInsn(str);
        return appendValueOnStack(Type.getType(String.class));
    }

    @Override
    public TextBuilder.WithAppend append(MethodVisitorConsumer consumer, Type type) {
        consumer.accept(mv);
        return appendValueOnStack(type);
    }

    @Override
    public TextBuilder.WithAppend appendValueOnStack(Type t) {
        String descriptor = AsmInsnUtilsForDesc.getDescForStringBuilderAppend(t);

        // operand stack: System.out, builder, val
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        return this;
    }

    @Override
    public void toText() {
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    }


    public static TextBuilder.Init builder(MethodVisitor mv) {
        return new MethodVisitorStringBuilder(mv);
    }
}
