package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static lsieun.asm.cst.MyAsmConst.RefType.OBJECT_TYPE;
import static lsieun.asm.insn.opcode.OpcodeForBox.box;
import static lsieun.asm.insn.opcode.OpcodeForBox.unbox;
import static lsieun.asm.insn.opcode.OpcodeForConst.push;
import static lsieun.asm.insn.opcode.OpcodeForStack.*;

public class OpcodeForArray {
    public static void newArray(@NotNull final MethodVisitor mv,
                                @NotNull final Type type) {
        int arrayType;
        switch (type.getSort()) {
            case Type.BOOLEAN:
                arrayType = Opcodes.T_BOOLEAN;
                break;
            case Type.CHAR:
                arrayType = Opcodes.T_CHAR;
                break;
            case Type.BYTE:
                arrayType = Opcodes.T_BYTE;
                break;
            case Type.SHORT:
                arrayType = Opcodes.T_SHORT;
                break;
            case Type.INT:
                arrayType = Opcodes.T_INT;
                break;
            case Type.FLOAT:
                arrayType = Opcodes.T_FLOAT;
                break;
            case Type.LONG:
                arrayType = Opcodes.T_LONG;
                break;
            case Type.DOUBLE:
                arrayType = Opcodes.T_DOUBLE;
                break;
            default:
                mv.visitTypeInsn(Opcodes.ANEWARRAY, type.getInternalName());
                return;
        }
        mv.visitIntInsn(Opcodes.NEWARRAY, arrayType);
    }

    public static void newArrayPrimitive(@NotNull final MethodVisitor mv,
                                         @NotNull final Type type, int count) {
        if (mv == null) {
            return;
        }
        int sort = type.getSort();
        if (sort < Type.BOOLEAN || sort > Type.DOUBLE) {
            return;
        }

        push(mv, count);
        int diff = sort - Type.BOOLEAN;
        int arrayType = Opcodes.T_BOOLEAN + diff;
        mv.visitIntInsn(Opcodes.NEWARRAY, arrayType);
    }

    public static void newArrayRef(@NotNull final MethodVisitor mv,
                                   @NotNull final Type type, int count) {
        push(mv, count);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, type.getInternalName());
    }

    /**
     * Generates the instruction to compute the length of an array.
     */
    public static void arrayLength(@NotNull final MethodVisitor mv) {
        mv.visitInsn(Opcodes.ARRAYLENGTH);
    }

    public static void arrayLoad(@NotNull final MethodVisitor mv,
                                 @NotNull final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IALOAD));
    }

    /**
     * Generates the instruction to store an element in an array.
     *
     * @param type the type of the array element to be stored.
     */
    public static void arrayStore(@NotNull final MethodVisitor mv,
                                  @NotNull final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IASTORE));
    }

    public static void arrayFromStackByMethodDesc(@NotNull final MethodVisitor mv,
                                                  @NotNull final String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        arrayFromStack(mv, argumentTypes);
    }

    public static void arrayFromStack(@NotNull final MethodVisitor mv,
                                      @NotNull final Type[] types) {
        int length = types.length;

        // operand stack: [x]
        push(mv, length);
        // operand stack: [x, count]
        newArray(mv, OBJECT_TYPE);
        // operand stack: [x, array]

        for (int i = length - 1; i >= 0; i--) {
            Type argType = types[i];
            // x = val
            // operand stack: [val, array]
            swapLeftAndRight(mv, argType, OBJECT_TYPE);
            // operand stack: [array, val]
            box(mv, types[i]);
            // operand stack: [array, boxedVal]
            swap(mv);
            // operand stack: [boxedVal, array]
            dupX1(mv);
            // operand stack: [array, boxedVal, array]
            swap(mv);
            // operand stack: [array, array, boxedVal]
            push(mv, i);
            // operand stack: [array, array, boxedVal, index]
            swapLeftAndRight(mv, OBJECT_TYPE, Type.INT_TYPE);
            // operand stack: [array, array, index, boxedVal]
            arrayStore(mv, OBJECT_TYPE);
            // operand stack: [array]
        }
    }

    public static void arrayToStackByMethodDesc(@NotNull final MethodVisitor mv,
                                                @NotNull final String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        arrayToStack(mv, argumentTypes, true);
    }

    public static void arrayToStack(@NotNull final MethodVisitor mv,
                                    @NotNull final Type[] types,
                                    boolean popArray) {
        // (1) check parameters
        int length = types.length;
        if (length == 0) {
            return;
        }

        // (2) load val to stack from array
        // operand stack: [..., array]
        for (int i = 0; i < length; i++) {
            Type t = types[i];

            // operand stack: [..., array]
            dup(mv);
            // operand stack: [..., array, array]
            push(mv, i);
            // operand stack: [..., array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [..., array, boxedValue]
            unbox(mv, t);
            // operand stack: [..., array, val]
            swapLeftAndRight(mv, OBJECT_TYPE, t);
            // operand stack: [..., val, array]
        }

        // (3) pop array from stack
        if (popArray) {
            // operand stack: [..., array]
            pop(mv);
            // operand stack: [...]
        }
    }
}
