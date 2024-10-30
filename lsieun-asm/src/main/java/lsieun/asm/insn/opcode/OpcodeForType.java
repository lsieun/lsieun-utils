package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static lsieun.asm.cst.MyAsmConst.RefType.OBJECT_TYPE;


public class OpcodeForType {
    public static void newInstance(@NotNull final MethodVisitor mv,
                                   @NotNull final Type type) {
        mv.visitTypeInsn(Opcodes.NEW, type.getInternalName());
    }

    public static void checkCast(@NotNull final MethodVisitor mv,
                                 @NotNull final Type type) {
        if (!type.equals(OBJECT_TYPE)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
        }
    }
}
