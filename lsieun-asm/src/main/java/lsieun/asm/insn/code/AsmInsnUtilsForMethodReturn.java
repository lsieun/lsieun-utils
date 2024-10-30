package lsieun.asm.insn.code;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.insn.code.AsmInsnUtilsForPrint.*;
import static lsieun.asm.insn.opcode.OpcodeForStack.dupValueOnStack;


public class AsmInsnUtilsForMethodReturn {
    public static void printReturnValue(@NotNull MethodVisitor mv, @NotNull String owner,
                                        @NotNull String methodName, @NotNull String methodDesc) {
        String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
        printMessage(mv, line);

        Type methodType = Type.getMethodType(methodDesc);
        Type returnType = methodType.getReturnType();
        printReturnValue(mv, returnType);
    }

    /**
     * @see AsmInsnUtilsForPrint#printValueOnStackWithPrefix(MethodVisitor, Type, int)
     */
    public static void printReturnValue(@NotNull MethodVisitor mv, @NotNull Type returnType) {
        dupValueOnStack(mv, returnType);

        printValueOnStackWithPrefix(mv, returnType, 4);
    }
}
