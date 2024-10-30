package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.*;

import static lsieun.asm.cst.MyAsmConst.CLASS_DESCRIPTOR;

public class OpcodeForConst {
    public static void push(@NotNull final MethodVisitor mv, final boolean value) {
        push(mv, value ? 1 : 0);
    }

    public static void push(@NotNull final MethodVisitor mv, final int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        }
        else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        }
        else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(@NotNull final MethodVisitor mv, final long value) {
        if (value == 0L || value == 1L) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(@NotNull final MethodVisitor mv, final float value) {
        int bits = Float.floatToIntBits(value);
        if (bits == 0L || bits == 0x3F800000 || bits == 0x40000000) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(@NotNull final MethodVisitor mv, final double value) {
        long bits = Double.doubleToLongBits(value);
        if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(@NotNull final MethodVisitor mv, @Nullable final String value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(@NotNull final MethodVisitor mv, @Nullable final Type value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            switch (value.getSort()) {
                case Type.BOOLEAN:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.CHAR:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.BYTE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.SHORT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.INT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.FLOAT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.LONG:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case Type.DOUBLE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", CLASS_DESCRIPTOR);
                    break;
                default:
                    mv.visitLdcInsn(value);
                    break;
            }
        }
    }

    public static void push(@NotNull final MethodVisitor mv,
                            @Nullable final Handle handle) {
        if (handle == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(handle);
        }
    }

    public static void push(@NotNull final MethodVisitor mv,
                            @Nullable final ConstantDynamic constantDynamic) {
        if (constantDynamic == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(constantDynamic);
        }
    }

    public static void push(@NotNull final MethodVisitor mv,
                            @Nullable final Object obj) {
        if (obj == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            if (obj instanceof Boolean) {
                push(mv, (boolean) obj);
            }
            else if (obj instanceof Integer) {
                push(mv, (int) obj);
            }
            else if (obj instanceof Long) {
                push(mv, (long) obj);
            }
            else if (obj instanceof Float) {
                push(mv, (float) obj);
            }
            else if (obj instanceof Double) {
                push(mv, (double) obj);
            }
            else if (obj instanceof String) {
                push(mv, (String) obj);
            }
            else if (obj instanceof Type) {
                push(mv, (Type) obj);
            }
            else if (obj instanceof Handle) {
                push(mv, (Handle) obj);
            }
            else if (obj instanceof ConstantDynamic) {
                push(mv, (ConstantDynamic) obj);
            }
            else {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
        }
    }
}
