package lsieun.asm.insn.opcode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static lsieun.asm.cst.MyAsmConst.RefType.OBJECT_TYPE;
import static lsieun.asm.insn.opcode.OpcodeForArray.arrayStore;
import static lsieun.asm.insn.opcode.OpcodeForArray.newArray;
import static lsieun.asm.insn.opcode.OpcodeForBox.box;
import static lsieun.asm.insn.opcode.OpcodeForConst.push;
import static lsieun.asm.insn.opcode.OpcodeForStack.dup;

/**
 * <ul>
 *     <li><code>arg</code> 是方法接收的参数</li>
 *     <li><code>local</code>是方法内定义的局部变量</li>
 * </ul>
 */
public class OpcodeForLocalVariable {
    private static void loadInsn(@NotNull final MethodVisitor mv,
                                 @NotNull final Type type,
                                 @Range(from = 0, to = 255) final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
    }

    private static void storeInsn(@NotNull final MethodVisitor mv,
                                  @NotNull final Type type,
                                  @Range(from = 0, to = 255) final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), index);
    }

    public static void loadThis(@NotNull final MethodVisitor mv,
                                @Range(from = 0, to = 2 ^ 16) final int methodAccess) {
        if ((methodAccess & Opcodes.ACC_STATIC) != 0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
    }

    private static int getArgSlotIndex(@Range(from = 0, to = 2 ^ 16) final int methodAccess,
                                       @NotNull final String methodDesc,
                                       @Range(from = 0, to = 255) final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int index = (methodAccess & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        for (int i = 0; i < argIndex; i++) {
            index += argumentTypes[i].getSize();
        }
        return index;
    }

    public static void loadArg(@NotNull final MethodVisitor mv,
                               @Range(from = 0, to = 2 ^ 16) final int methodAccess,
                               @NotNull final String methodDesc,
                               @Range(from = 0, to = 255) final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        loadInsn(mv, argumentTypes[argIndex], getArgSlotIndex(methodAccess, methodDesc, argIndex));
    }

    public static void loadArgs(@NotNull final MethodVisitor mv,
                                @Range(from = 0, to = 2 ^ 16) final int methodAccess,
                                @NotNull final String methodDesc,
                                @Range(from = 0, to = 255) final int argIndex,
                                final int count) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int index = getArgSlotIndex(methodAccess, methodDesc, argIndex);
        for (int i = 0; i < count; ++i) {
            Type argumentType = argumentTypes[argIndex + i];
            loadInsn(mv, argumentType, index);
            index += argumentType.getSize();
        }
    }

    public static void loadArgs(@NotNull final MethodVisitor mv,
                                @Range(from = 0, to = 2 ^ 16) final int methodAccess,
                                @NotNull final String methodDesc) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        loadArgs(mv, methodAccess, methodDesc, 0, argumentTypes.length);
    }

    public static void loadArgArray(@NotNull final MethodVisitor mv,
                                    @Range(from = 0, to = 2 ^ 16) final int methodAccess,
                                    @NotNull final String methodDesc) {
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

    public static void storeArg(@NotNull final MethodVisitor mv,
                                @Range(from = 0, to = 2 ^ 16) int methodAccess,
                                @NotNull final String methodDesc,
                                @Range(from = 0, to = 255) final int argIndex) {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        storeInsn(mv, argumentTypes[argIndex], getArgSlotIndex(methodAccess, methodDesc, argIndex));
    }

    public static int getFirstLocalSlotIndex(@Range(from = 0, to = 2 ^ 16) int methodAccess,
                                             @NotNull final String methodDesc) {
        boolean isStatic = (Opcodes.ACC_STATIC & methodAccess) != 0;
        return getFirstLocalSlotIndex(isStatic, methodDesc);
    }

    public static int getFirstLocalSlotIndex(final boolean isStatic,
                                             @NotNull final String methodDesc) {
        int slotIndex = isStatic ? 0 : 1;
        for (Type argumentType : Type.getArgumentTypes(methodDesc)) {
            slotIndex += argumentType.getSize();
        }
        return slotIndex;
    }

    public static List<Type> getEmptyLocalTypes() {
        return new ArrayList<>();
    }

    public Type getLocalType(@Range(from = 0, to = 2 ^ 16) int methodAccess,
                             @NotNull final String methodDesc,
                             @NotNull final List<Type> localTypes,
                             @Range(from = 0, to = 255) final int localSlotIndex) {
        int firstLocalSlotIndex = getFirstLocalSlotIndex(methodAccess, methodDesc);
        return localTypes.get(localSlotIndex - firstLocalSlotIndex);
    }


    public void setLocalType(@Range(from = 0, to = 2 ^ 16) int methodAccess,
                             @NotNull final String methodDesc,
                             @NotNull final List<Type> localTypes,
                             @Range(from = 0, to = 255) final int localSlotIndex,
                             @NotNull final Type type) {
        int firstLocalSlotIndex = getFirstLocalSlotIndex(methodAccess, methodDesc);
        int index = localSlotIndex - firstLocalSlotIndex;
        while (localTypes.size() < index + 1) {
            localTypes.add(null);
        }
        localTypes.set(index, type);
    }

    public void loadLocal(@NotNull final MethodVisitor mv,
                          @Range(from = 0, to = 2 ^ 16) int methodAccess,
                          @NotNull final String methodDesc,
                          @NotNull final List<Type> localTypes, final int localSlotIndex) {
        Type localType = getLocalType(methodAccess, methodDesc, localTypes, localSlotIndex);
        loadInsn(mv, localType, localSlotIndex);
    }

    public void loadLocal(@NotNull final MethodVisitor mv,
                          @Range(from = 0, to = 2 ^ 16) int methodAccess,
                          @NotNull final String methodDesc,
                          @NotNull List<Type> localTypes,
                          @Range(from = 0, to = 255) final int localSlotIndex,
                          @NotNull final Type type) {
        setLocalType(methodAccess, methodDesc, localTypes, localSlotIndex, type);
        loadInsn(mv, type, localSlotIndex);
    }

    public void storeLocal(@NotNull final MethodVisitor mv,
                           @Range(from = 0, to = 2 ^ 16) int methodAccess,
                           @NotNull final String methodDesc,
                           @NotNull final List<Type> localTypes,
                           @Range(from = 0, to = 255) final int localSlotIndex) {
        Type type = getLocalType(methodAccess, methodDesc, localTypes, localSlotIndex);
        storeInsn(mv, type, localSlotIndex);
    }

    public void storeLocal(@NotNull final MethodVisitor mv,
                           @Range(from = 0, to = 2 ^ 16) int methodAccess,
                           @NotNull final String methodDesc,
                           @NotNull final List<Type> localTypes,
                           @Range(from = 0, to = 255) final int localSlotIndex,
                           @NotNull final Type type) {
        setLocalType(methodAccess, methodDesc, localTypes, localSlotIndex, type);
        storeInsn(mv, type, localSlotIndex);
    }

    public static void iinc(@NotNull final MethodVisitor mv,
                            @Range(from = 0, to = 255) final int localSlotIndex,
                            final int amount) {
        mv.visitIincInsn(localSlotIndex, amount);
    }
}
