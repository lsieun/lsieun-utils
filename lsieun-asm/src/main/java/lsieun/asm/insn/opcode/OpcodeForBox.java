package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static lsieun.asm.cst.MyAsmConst.Box.*;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static lsieun.asm.cst.MyAsmConst.RefType.*;
import static lsieun.asm.insn.opcode.OpcodeForConst.push;
import static lsieun.asm.insn.opcode.OpcodeForInvoke.*;
import static lsieun.asm.insn.opcode.OpcodeForStack.*;
import static lsieun.asm.insn.opcode.OpcodeForType.checkCast;
import static lsieun.asm.insn.opcode.OpcodeForType.newInstance;

public class OpcodeForBox {
    public static void castPrimitive(@NotNull final MethodVisitor methodVisitor,
                                     @NotNull final Type from,
                                     @NotNull final Type to) {
        if (from != to) {
            if (from == Type.DOUBLE_TYPE) {
                if (to == Type.FLOAT_TYPE) {
                    methodVisitor.visitInsn(Opcodes.D2F);
                }
                else if (to == Type.LONG_TYPE) {
                    methodVisitor.visitInsn(Opcodes.D2L);
                }
                else {
                    methodVisitor.visitInsn(Opcodes.D2I);
                    castPrimitive(methodVisitor, Type.INT_TYPE, to);
                }
            }
            else if (from == Type.FLOAT_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    methodVisitor.visitInsn(Opcodes.F2D);
                }
                else if (to == Type.LONG_TYPE) {
                    methodVisitor.visitInsn(Opcodes.F2L);
                }
                else {
                    methodVisitor.visitInsn(Opcodes.F2I);
                    castPrimitive(methodVisitor, Type.INT_TYPE, to);
                }
            }
            else if (from == Type.LONG_TYPE) {
                if (to == Type.DOUBLE_TYPE) {
                    methodVisitor.visitInsn(Opcodes.L2D);
                }
                else if (to == Type.FLOAT_TYPE) {
                    methodVisitor.visitInsn(Opcodes.L2F);
                }
                else {
                    methodVisitor.visitInsn(Opcodes.L2I);
                    castPrimitive(methodVisitor, Type.INT_TYPE, to);
                }
            }
            else {
                if (to == Type.BYTE_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2B);
                }
                else if (to == Type.CHAR_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2C);
                }
                else if (to == Type.DOUBLE_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2D);
                }
                else if (to == Type.FLOAT_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2F);
                }
                else if (to == Type.LONG_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2L);
                }
                else if (to == Type.SHORT_TYPE) {
                    methodVisitor.visitInsn(Opcodes.I2S);
                }
            }
        }
    }

    /**
     * Generates the instructions to box the top stack value.
     * This value is replaced by its boxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public static void box(@NotNull final MethodVisitor mv,
                           @NotNull final Type type) {
        if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            Type boxedType = getBoxedType(type);
            // operand stack: [x]
            newInstance(mv, boxedType);
            // operand stack: [x, UNINITIALIZED_THIS]
            if (type.getSize() == 2) {
                // x = value2, value1
                // operand stack: [value2, value1, UNINITIALIZED_THIS]
                dupX2(mv);
                // operand stack: [UNINITIALIZED_THIS, value2, value1, UNINITIALIZED_THIS]
                dupX2(mv);
                // operand stack: [UNINITIALIZED_THIS, UNINITIALIZED_THIS, value2, value1, UNINITIALIZED_THIS]
                pop(mv);
                // operand stack: [UNINITIALIZED_THIS, UNINITIALIZED_THIS, value2, value1]
            }
            else {
                // x = value1
                // operand stack: [value1, UNINITIALIZED_THIS]
                dupX1(mv);
                // operand stack: [UNINITIALIZED_THIS, value1, UNINITIALIZED_THIS]
                swap(mv);
                // operand stack: [UNINITIALIZED_THIS, UNINITIALIZED_THIS, value1]
            }
            invokeConstructor(mv, boxedType, new Method(INIT_METHOD_NAME, Type.VOID_TYPE, new Type[]{type}));
        }
    }

    /**
     * Generates the instructions to box the top stack value using Java 5's valueOf() method.
     * This value is replaced by its boxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public static void valueOf(@NotNull final MethodVisitor mv,
                               @NotNull final Type type) {
        if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
            return;
        }
        if (type == Type.VOID_TYPE) {
            push(mv, (String) null);
        }
        else {
            Type boxedType = getBoxedType(type);
            invokeStatic(mv, boxedType, new Method("valueOf", boxedType, new Type[]{type}));
        }
    }

    /**
     * Generates the instructions to unbox the top stack value.
     * This value is replaced by its unboxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public static void unbox(@NotNull final MethodVisitor mv,
                             @NotNull final Type type) {
        Type boxedType = NUMBER_TYPE;
        Method unboxMethod;
        switch (type.getSort()) {
            case Type.VOID:
                return;
            case Type.CHAR:
                boxedType = CHARACTER_TYPE;
                unboxMethod = CHAR_VALUE;
                break;
            case Type.BOOLEAN:
                boxedType = BOOLEAN_TYPE;
                unboxMethod = BOOLEAN_VALUE;
                break;
            case Type.DOUBLE:
                unboxMethod = DOUBLE_VALUE;
                break;
            case Type.FLOAT:
                unboxMethod = FLOAT_VALUE;
                break;
            case Type.LONG:
                unboxMethod = LONG_VALUE;
                break;
            case Type.INT:
            case Type.SHORT:
            case Type.BYTE:
                unboxMethod = INT_VALUE;
                break;
            default:
                unboxMethod = null;
                break;
        }
        if (unboxMethod == null) {
            checkCast(mv, type);
        }
        else {
            checkCast(mv, boxedType);
            invokeVirtual(mv, boxedType, unboxMethod);
        }
    }

    public static Type getBoxedType(@NotNull final Type type) {
        switch (type.getSort()) {
            case Type.BYTE:
                return BYTE_TYPE;
            case Type.BOOLEAN:
                return BOOLEAN_TYPE;
            case Type.SHORT:
                return SHORT_TYPE;
            case Type.CHAR:
                return CHARACTER_TYPE;
            case Type.INT:
                return INTEGER_TYPE;
            case Type.FLOAT:
                return FLOAT_TYPE;
            case Type.LONG:
                return LONG_TYPE;
            case Type.DOUBLE:
                return DOUBLE_TYPE;
            default:
                return type;
        }
    }

}
