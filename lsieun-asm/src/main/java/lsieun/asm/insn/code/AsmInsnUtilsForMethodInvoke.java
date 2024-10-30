package lsieun.asm.insn.code;

import lsieun.base.text.StrConst;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.cst.MyAsmConst.RefType.OBJECT_TYPE;
import static lsieun.asm.insn.code.AsmInsnUtilsForPrint.printValueOnStackWithPrefix;
import static lsieun.asm.insn.opcode.OpcodeForArray.arrayFromStackByMethodDesc;
import static lsieun.asm.insn.opcode.OpcodeForArray.arrayLoad;
import static lsieun.asm.insn.opcode.OpcodeForBox.unbox;
import static lsieun.asm.insn.opcode.OpcodeForConst.push;
import static lsieun.asm.insn.opcode.OpcodeForStack.*;

public class AsmInsnUtilsForMethodInvoke {

    public static void printInvokeMethodInsnParams(@NotNull MethodVisitor mv, boolean hasReceiverType,
                                                   @NotNull String methodDesc, int spaceCount) {
        // operand stack: [...]
        arrayFromStackByMethodDesc(mv, methodDesc);
        // operand stack: [..., array]

        if (hasReceiverType) {
            // operand stack: [..., receiver, array]
            swap(mv);
            // operand stack: [..., array, receiver]
            dupX1(mv);
            // operand stack: [..., receiver, array, receiver]
            AsmInsnUtilsForStackValue.convertValueOnStackToTypeName(mv);
            // operand stack: [..., receiver, array, str]
            String prefix = String.format("%s[receiver] - ", StrConst.SPACE.repeat(spaceCount));
            printValueOnStackWithPrefix(mv, Type.getType(String.class), prefix);
            // operand stack: [..., receiver, array]
        }

        // operand stack: [x, array]
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int count = argumentTypes.length;
        for (int i = 0; i < count; i++) {
            Type argumentType = argumentTypes[i];

            // operand stack: [x, array]
            dup(mv);
            // operand stack: [x, array, array]
            push(mv, i);
            // operand stack: [x, array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [x, array, obj]
            unbox(mv, argumentType);
            // operand stack: [x, array, arg]
            String prefix = String.format("%sargs[%d] - ", StrConst.SPACE.repeat(spaceCount), i);
            printValueOnStackWithPrefix(mv, argumentType, prefix);
            // operand stack: [x, array]
        }

        for (int i = 0; i < count; i++) {
            Type argumentType = argumentTypes[i];

            // operand stack: [x, array]
            dup(mv);
            // operand stack: [x, array, array]
            push(mv, i);
            // operand stack: [x, array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [x, array, obj]
            unbox(mv, argumentType);
            // operand stack: [x, array, arg]
            swapLeftAndRight(mv, OBJECT_TYPE, argumentType);
            // operand stack: [x, arg, array]
        }

        // pop array
        // operand stack: [..., array]
        pop(mv);
        // operand stack: [...]
    }
}
