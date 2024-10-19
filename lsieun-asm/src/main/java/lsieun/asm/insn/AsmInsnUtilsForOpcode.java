package lsieun.asm.insn;

import lsieun.asm.cst.MyAsmConst;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.List;

import static lsieun.asm.cst.MyAsmConst.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * <pre>
 *                                                 ┌─── cst ──────┼─── push()
 *                                                 │
 *                                                 │                           ┌─── pop()
 *                                                 │                           │
 *                                                 │              ┌─── pop ────┼─── pop2()
 *                                                 │              │            │
 *                                                 │              │            └─── ext ──────┼─── popByMethodDesc()
 *                                                 │              │
 *                                                 │              │                           ┌─── dupX1()
 *                                                 │              │            ┌─── dup() ────┤
 *                                                 │              │            │              └─── dupX2()
 *                                                 ├─── only ─────┤            │
 *                                                 │              ├─── dup ────┤              ┌─── dup2X1()
 *                                                 │              │            ├─── dup2() ───┤
 *                                                 │              │            │              └─── dup2X2()
 *                                                 │              │            │
 *                                                 │              │            └─── ext ──────┼─── dupValueOnStack()
 *                                                 │              │
 *                                                 │              │            ┌─── swap()
 *                                                 │              └─── swap ───┤
 *                                                 │                           │              ┌─── swapUpAndDown()
 *                                                 │                           └─── ext ──────┤
 *                                                 │                                          └─── swapLeftAndRight()
 *                                                 │
 *                                                 │                                ┌─── wide/narrow ─────┼─── castPrimitive()
 *                                                 │                                │
 *                          ┌─── operand.stack ────┤              ┌─── primitive ───┤                                                          ┌─── box()
 *                          │                      │              │                 │                     ┌─── box ─────┼─── getBoxedType() ───┤
 *                          │                      │              │                 └─── ref:box/unbox ───┤                                    └─── valueOf()
 *                          │                      │              │                                       │
 *                          │                      │              │                                       └─── unbox ───┼─── unbox()
 *                          │                      │              │
 *                          │                      │              │                 ┌─── new ────┼─── newInstance()
 *                          │                      │              ├─── reference ───┤
 *                          │                      ├─── type ─────┤                 └─── cast ───┼─── checkCast()
 *                          │                      │              │
 *                          │                      │              │                                                       ┌─── newArrayRef()
 *                          │                      │              │                 ┌─── new ──────────┼─── newArray() ───┤
 *                          │                      │              │                 │                                     └─── newArrayPrimitive()
 *                          │                      │              │                 │
 *                          │                      │              │                 ├─── length ───────┼─── arrayLength()
 *                          │                      │              │                 │
 *                          │                      │              └─── array ───────┤                  ┌─── arrayLoad()
 *                          │                      │                                ├─── load/store ───┤
 *                          │                      │                                │                  └─── arrayStore()
 *                          │                      │                                │
 *                          │                      │                                │                  ┌─── arrayFromStackByMethodDesc()
 *                          │                      │                                └─── ext:method ───┤
 *                          │                      │                                                   └─── arrayToStackByMethodDesc()
 *                          │                      │
 *                          │                      │                             ┌─── static ────┼─── invokeStatic()
 *                          │                      │                             │
 *                          │                      └─── method ───┼─── invoke ───┼─── special ───┼─── invokeConstructor()
 * AsmInsnUtilsForOpcode ───┤                                                    │
 *                          │                                                    └─── virtual ───┼─── invokeVirtual()
 *                          │
 *                          │                                                       ┌─── loadInsn()
 *                          │                                         ┌─── insn ────┤
 *                          │                                         │             └─── storeInsn()
 *                          │                                         │
 *                          │                                         ├─── this ────┼─── loadThis()
 *                          │                                         │
 *                          │                                         │             ┌─── slotIndex ────┼─── getArgSlotIndex()
 *                          │                                         ├─── args ────┤
 *                          ├─── stack&lt;-&gt;local ────┼─── load/store ───┤             │                  ┌─── loadArg() ────┼─── loadArgs()
 *                          │                                         │             └─── load/store ───┤
 *                          │                                         │                                └─── storeArg()
 *                          │                                         │
 *                          │                                         │             ┌─── slotIndex ────┼─── getFirstLocalSlotIndex()
 *                          │                                         │             │
 *                          │                                         │             │                  ┌─── list ───┼─── getEmptyLocalTypes()
 *                          │                                         │             ├─── type ─────────┤
 *                          │                                         └─── local ───┤                  │            ┌─── getLocalType()
 *                          │                                                       │                  └─── one ────┤
 *                          │                                                       │                               └─── setLocalType()
 *                          │                                                       │
 *                          │                                                       │                  ┌─── loadLocal()
 *                          │                                                       └─── load/store ───┤
 *                          │                                                                          └─── storeLocal()
 *                          │
 *                          └─── local.variable ───┼─── iinc()
 * </pre>
 * <ul>
 *     <li><code>arg</code> 是方法接收的参数</li>
 *     <li><code>local</code>是方法内定义的局部变量</li>
 * </ul>
 */
public class AsmInsnUtilsForOpcode {


    /**
     * Returns the internal names of the given types.
     *
     * @param types a set of types.
     * @return the internal names of the given types (see {@link Type#getInternalName()}).
     */
    public static String[] getInternalNames(final Type[] types) {
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = types[i].getInternalName();
        }
        return names;
    }

    // region type - primitive
    public static void castPrimitive(final MethodVisitor methodVisitor, final Type from, final Type to) {
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
    public static void box(MethodVisitor mv, final Type type) {
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
            invokeConstructor(mv, boxedType, new Method(MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, Type.VOID_TYPE, new Type[]{type}));
        }
    }

    /**
     * Generates the instructions to box the top stack value using Java 5's valueOf() method.
     * This value is replaced by its boxed equivalent on top of the stack.
     *
     * @param type the type of the top stack value.
     */
    public static void valueOf(MethodVisitor mv, final Type type) {
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
    public static void unbox(MethodVisitor mv, final Type type) {
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

    public static Type getBoxedType(final Type type) {
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
    // endregion

    // region type -reference
    public static void newInstance(MethodVisitor mv, final Type type) {
        mv.visitTypeInsn(Opcodes.NEW, type.getInternalName());
    }

    public static void checkCast(MethodVisitor mv, final Type type) {
        if (!type.equals(OBJECT_TYPE)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
        }
    }


    // endregion


    // region array
    public static void newArray(final MethodVisitor mv, final Type type) {
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

    public static void newArrayPrimitive(final MethodVisitor mv, final Type type, int count) {
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

    public static void newArrayRef(final MethodVisitor mv, final Type type, int count) {
        push(mv, count);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, type.getInternalName());
    }

    /**
     * Generates the instruction to compute the length of an array.
     */
    public static void arrayLength(MethodVisitor mv) {
        mv.visitInsn(Opcodes.ARRAYLENGTH);
    }

    public static void arrayLoad(final MethodVisitor mv, final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IALOAD));
    }

    /**
     * Generates the instruction to store an element in an array.
     *
     * @param type the type of the array element to be stored.
     */
    public static void arrayStore(final MethodVisitor mv, final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IASTORE));
    }

    public static void arrayFromStackByMethodDesc(MethodVisitor mv, String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int count = argumentTypes.length;

        // operand stack: [x]
        push(mv, count);
        // operand stack: [x, count]
        newArray(mv, OBJECT_TYPE);
        // operand stack: [x, array]

        for (int i = count - 1; i >= 0; i--) {
            Type argType = argumentTypes[i];
            // x = arg
            // operand stack: [arg, array]
            swapLeftAndRight(mv, argType, OBJECT_TYPE);
            // operand stack: [array, arg]
            box(mv, argumentTypes[i]);
            // operand stack: [array, boxedArg]
            swap(mv);
            // operand stack: [boxedArg, array]
            dupX1(mv);
            // operand stack: [array, boxedArg, array]
            swap(mv);
            // operand stack: [array, array, boxedArg]
            push(mv, i);
            // operand stack: [array, array, boxedArg, index]
            swapLeftAndRight(mv, OBJECT_TYPE, Type.INT_TYPE);
            // operand stack: [array, array, index, boxedArg]
            arrayStore(mv, OBJECT_TYPE);
            // operand stack: [array]
        }
    }

    public static void arrayToStackByMethodDesc(MethodVisitor mv, String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int count = argumentTypes.length;
        // operand stack: [..., array]
        for (int i = 0; i < count; i++) {
            Type argType = argumentTypes[i];

            // operand stack: [..., array]
            dupX1(mv);
            // operand stack: [..., array, array]
            push(mv, i);
            // operand stack: [..., array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [..., array, boxedValue]
            unbox(mv, argType);
            // operand stack: [..., array, arg]
            swapLeftAndRight(mv, OBJECT_TYPE, argType);
            // operand stack: [..., arg, array]
        }
        // operand stack: [..., array]
        pop(mv);
        // operand stack: [...]
    }
    // endregion

    // region invokeXxxInsn

    public static void invokeConstructor(MethodVisitor mv, final Type type, final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, method.getName(), method.getDescriptor(), false);
    }

    public static void invokeStatic(MethodVisitor mv, final Type type, final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, method.getName(), method.getDescriptor(), false);
    }

    public static void invokeVirtual(MethodVisitor mv, final Type type, final Method method) {
        String owner = type.getSort() == Type.ARRAY ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, method.getName(), method.getDescriptor(), false);
    }
    // endregion

    // region operand stack
    public static void pop(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? POP : POP2;
            mv.visitInsn(opcode);
        }
    }

    public static void popByMethodDesc(MethodVisitor mv, boolean isStatic, String methodDesc) {
        if (mv == null) {
            return;
        }

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

    public static void pop(MethodVisitor mv) {
        mv.visitInsn(Opcodes.POP);
    }

    public static void pop2(MethodVisitor mv) {
        mv.visitInsn(Opcodes.POP2);
    }

    public static void dupValueOnStack(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? DUP : DUP2;
            mv.visitInsn(opcode);
        }
    }

    public static void dup(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP);
    }

    public static void dup2(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2);
    }

    public static void dupX1(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP_X1);
    }

    public static void dupX2(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP_X2);
    }

    public static void dup2X1(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2_X1);
    }

    public static void dup2X2(MethodVisitor mv) {
        mv.visitInsn(Opcodes.DUP2_X2);
    }

    public static void swap(MethodVisitor mv) {
        mv.visitInsn(Opcodes.SWAP);
    }

    public static void swapUpAndDown(MethodVisitor mv, Type up, Type down) {
        // (1) parameter verification
        if (mv == null || up == null || down == null) {
            return;
        }

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

    public static void swapLeftAndRight(MethodVisitor mv, final Type leftType, final Type rightType) {
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
    // endregion

    // region push constants
    public static void push(MethodVisitor mv, final boolean value) {
        push(mv, value ? 1 : 0);
    }

    public static void push(MethodVisitor mv, final int value) {
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

    public static void push(MethodVisitor mv, final long value) {
        if (value == 0L || value == 1L) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(MethodVisitor mv, final float value) {
        int bits = Float.floatToIntBits(value);
        if (bits == 0L || bits == 0x3F800000 || bits == 0x40000000) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(MethodVisitor mv, final double value) {
        long bits = Double.doubleToLongBits(value);
        if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(MethodVisitor mv, final String value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    public static void push(MethodVisitor mv, final Type value) {
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

    public static void push(MethodVisitor mv, final Handle handle) {
        if (handle == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(handle);
        }
    }

    public static void push(MethodVisitor mv, final ConstantDynamic constantDynamic) {
        if (constantDynamic == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(constantDynamic);
        }
    }

    public static void push(MethodVisitor mv, final Object obj) {
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
            else if(obj instanceof Type) {
                push(mv, (Type)obj);
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
    // endregion

    // region load and store method arguments
    private static void loadInsn(MethodVisitor mv, final Type type, final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
    }

    private static void storeInsn(MethodVisitor mv, final Type type, final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), index);
    }

    public static void loadThis(MethodVisitor mv, int methodAccess) {
        if ((methodAccess & Opcodes.ACC_STATIC) != 0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
    }

    private static int getArgSlotIndex(int methodAccess, String methodDesc, final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int index = (methodAccess & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        for (int i = 0; i < argIndex; i++) {
            index += argumentTypes[i].getSize();
        }
        return index;
    }

    public static void loadArg(MethodVisitor mv, int methodAccess, String methodDesc, final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        loadInsn(mv, argumentTypes[argIndex], getArgSlotIndex(methodAccess, methodDesc, argIndex));
    }

    public static void loadArgs(MethodVisitor mv, int methodAccess, String methodDesc, final int argIndex, final int count) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int index = getArgSlotIndex(methodAccess, methodDesc, argIndex);
        for (int i = 0; i < count; ++i) {
            Type argumentType = argumentTypes[argIndex + i];
            loadInsn(mv, argumentType, index);
            index += argumentType.getSize();
        }
    }

    public static void loadArgs(MethodVisitor mv, int methodAccess, String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        loadArgs(mv, methodAccess, methodDesc, 0, argumentTypes.length);
    }

    public static void loadArgArray(MethodVisitor mv, int methodAccess, String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        // operand stack: []
        push(mv, argumentTypes.length);
        // operand stack: [count]
        newArray(mv, OBJECT_TYPE);
        // operand stack: [array]
        for (int i = 0; i < argumentTypes.length; i++) {
            // operand stack: [array]
            dup(mv);
            // operand stack: [array, array]
            push(mv, i);
            // operand stack: [array, array, index]
            loadArg(mv, methodAccess, methodDesc, i);
            // operand stack: [array, array, index, value]
            box(mv, argumentTypes[i]);
            // operand stack: [array, array, index, boxedValue]
            arrayStore(mv, OBJECT_TYPE);
            // operand stack: [array]
        }
    }

    public static void storeArg(MethodVisitor mv, int methodAccess, String methodDesc, final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        storeInsn(mv, argumentTypes[argIndex], getArgSlotIndex(methodAccess, methodDesc, argIndex));
    }

    public static int getFirstLocalSlotIndex(int methodAccess, String methodDesc) {
        boolean isStatic = (Opcodes.ACC_STATIC & methodAccess) != 0;
        return getFirstLocalSlotIndex(isStatic, methodDesc);
    }

    public static int getFirstLocalSlotIndex(boolean isStatic, String methodDesc) {
        int slotIndex = isStatic ? 0 : 1;
        for (Type argumentType : Type.getArgumentTypes(methodDesc)) {
            slotIndex += argumentType.getSize();
        }
        return slotIndex;
    }

    public static List<Type> getEmptyLocalTypes() {
        return new ArrayList<>();
    }

    public Type getLocalType(int methodAccess, String methodDesc, List<Type> localTypes, final int localSlotIndex) {
        int firstLocalSlotIndex = getFirstLocalSlotIndex(methodAccess, methodDesc);
        return localTypes.get(localSlotIndex - firstLocalSlotIndex);
    }


    public void setLocalType(int methodAccess, String methodDesc,
                             List<Type> localTypes, final int localSlotIndex, final Type type) {
        int firstLocalSlotIndex = getFirstLocalSlotIndex(methodAccess, methodDesc);
        int index = localSlotIndex - firstLocalSlotIndex;
        while (localTypes.size() < index + 1) {
            localTypes.add(null);
        }
        localTypes.set(index, type);
    }

    public void loadLocal(MethodVisitor mv, int methodAccess, String methodDesc,
                          List<Type> localTypes, final int localSlotIndex) {
        Type localType = getLocalType(methodAccess, methodDesc, localTypes, localSlotIndex);
        loadInsn(mv, localType, localSlotIndex);
    }

    public void loadLocal(MethodVisitor mv, int methodAccess, String methodDesc,
                          List<Type> localTypes, final int localSlotIndex, final Type type) {
        setLocalType(methodAccess, methodDesc, localTypes, localSlotIndex, type);
        loadInsn(mv, type, localSlotIndex);
    }

    public void storeLocal(MethodVisitor mv, int methodAccess, String methodDesc,
                           List<Type> localTypes, final int localSlotIndex) {
        Type type = getLocalType(methodAccess, methodDesc, localTypes, localSlotIndex);
        storeInsn(mv, type, localSlotIndex);
    }

    public void storeLocal(MethodVisitor mv, int methodAccess, String methodDesc,
                           List<Type> localTypes, final int localSlotIndex, final Type type) {
        setLocalType(methodAccess, methodDesc, localTypes, localSlotIndex, type);
        storeInsn(mv, type, localSlotIndex);
    }

    public static void iinc(MethodVisitor mv, final int localSlotIndex, final int amount) {
        mv.visitIincInsn(localSlotIndex, amount);
    }
    // endregion
}
