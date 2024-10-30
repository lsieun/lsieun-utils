package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class OpcodeForStack {
    public static void pop(@NotNull final MethodVisitor mv,
                           @NotNull final Type t) {
        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? POP : POP2;
            mv.visitInsn(opcode);
        }
    }

    public static void popByMethodDesc(@NotNull final MethodVisitor mv,
                                       final boolean isStatic,
                                       @NotNull final String methodDesc) {
        // pop arg types
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        int argCount = argumentTypes.length;
        for (int i = argCount - 1; i >= 0; i--) {
            Type argType = argumentTypes[i];
            pop(mv, argType);
        }

        // pop this
        if (!isStatic) {
            pop(mv, Type.getType(Object.class));
        }
    }

    public static void pop(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.POP);
    }

    public static void pop2(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.POP2);
    }

    public static void dupValueOnStack(@NotNull final MethodVisitor mv,
                                       @NotNull final Type t) {
        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? DUP : DUP2;
            mv.visitInsn(opcode);
        }
    }

    public static void dup(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP);
    }

    public static void dup2(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2);
    }

    public static void dupX1(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP_X1);
    }

    public static void dupX2(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP_X2);
    }

    public static void dup2X1(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2_X1);
    }

    public static void dup2X2(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2_X2);
    }

    public static void swap(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.SWAP);
    }

    public static void swapUpAndDown(@NotNull final MethodVisitor mv,
                                     @NotNull final Type up,
                                     @NotNull final Type down) {
        // (1) parameter verification
        int upSort = up.getSort();
        if (upSort < Type.BOOLEAN || upSort > Type.OBJECT) {
            return;
        }

        int downSort = down.getSort();
        if (downSort < Type.BOOLEAN || downSort > Type.OBJECT) {
            return;
        }

        // (2) swap
        int upSize = up.getSize();
        int downSize = down.getSize();

        if (upSize == 1 && downSize == 1) {
            mv.visitInsn(Opcodes.SWAP);
        }
        else if (upSize == 1 && downSize == 2) {
            // operand stack: value3, value2, value1
            mv.visitInsn(Opcodes.DUP_X2);
            // operand stack: value1, value3, value2, value1
            mv.visitInsn(Opcodes.POP);
            // operand stack: value1, value3, value2
        }
        else if (upSize == 2 && downSize == 1) {
            // operand stack: value3, value2, value1
            mv.visitInsn(Opcodes.DUP2_X1);
            // operand stack: value2, value1, value3, value2, value1
            mv.visitInsn(Opcodes.POP2);
            // operand stack: value2, value1, value3
        }
        else {
            assert upSize == 2 && downSize == 2;
            // operand stack: value4, value3, value2, value1
            mv.visitInsn(Opcodes.DUP2_X2);
            // operand stack: value2, value1, value4, value3, value2, value1
            mv.visitInsn(Opcodes.POP2);
            // operand stack: value2, value1, value4, value3
        }
    }

    public static void swapLeftAndRight(@NotNull final MethodVisitor mv,
                                        @NotNull final Type leftType,
                                        @NotNull final Type rightType) {
        if (rightType.getSize() == 1) {
            if (leftType.getSize() == 1) {
                swap(mv); // Same as dupX1 pop.
            }
            else {
                dupX2(mv);
                pop(mv);
            }
        }
        else {
            if (leftType.getSize() == 1) {
                dup2X1(mv);
                pop2(mv);
            }
            else {
                dup2X2(mv);
                pop2(mv);
            }
        }
    }
}
