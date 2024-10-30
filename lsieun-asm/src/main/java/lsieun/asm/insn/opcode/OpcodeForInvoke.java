package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class OpcodeForInvoke {
    public static void invokeConstructor(@NotNull final MethodVisitor mv,
                                         @NotNull final Type type,
                                         @NotNull final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, method.getName(), method.getDescriptor(), false);
    }

    public static void invokeStatic(@NotNull final MethodVisitor mv,
                                    @NotNull final Type type,
                                    @NotNull final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, method.getName(), method.getDescriptor(), false);
    }

    public static void invokeVirtual(@NotNull final MethodVisitor mv,
                                     @NotNull final Type type,
                                     @NotNull final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, method.getName(), method.getDescriptor(), false);
    }
}
