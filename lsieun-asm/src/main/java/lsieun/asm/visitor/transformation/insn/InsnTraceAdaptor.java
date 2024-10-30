package lsieun.asm.visitor.transformation.insn;

import lsieun.annotation.mind.logic.ThinkStep;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.core.AsmTypeUtils;
import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.cst.OpcodeConst;
import lsieun.asm.format.MethodInfoFormat;
import lsieun.asm.insn.code.AsmInsnUtilsForMethodInvoke;
import lsieun.asm.insn.code.AsmInsnUtilsForMethodParameter;
import lsieun.asm.insn.code.AsmInsnUtilsForPrint;
import lsieun.asm.insn.opcode.OpcodeForArray;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import static lsieun.asm.cst.MyAsmConst.ArrayType.OBJECT_ARRAY_TYPE;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static lsieun.asm.cst.MyAsmConst.RefType.OBJECT_TYPE;
import static lsieun.asm.insn.opcode.OpcodeForStack.dupValueOnStack;

public class InsnTraceAdaptor extends AdviceAdapter {
    private final String currentOwner;
    private boolean methodEnterHasBeenCalled = false;

    InsnTraceAdaptor(MethodVisitor methodVisitor,
                     String currentOwner,
                     int access, String name, String descriptor) {
        super(MyAsmConst.ASM_API_VERSION, methodVisitor, access, name, descriptor);
        this.currentOwner = currentOwner;
    }

    @Override
    protected void onMethodEnter() {
        if (mv == null) {
            return;
        }

        // enter
        String className = AsmTypeNameUtils.toClassName(currentOwner);
        String line = MethodInfoFormat.methodEnter(className, methodAccess, getName(), methodDesc);
        AsmInsnUtilsForPrint.printMessage(mv, line);

        // method args
        AsmInsnUtilsForMethodParameter.printParameters(mv, methodAccess, methodDesc);

        methodEnterHasBeenCalled = true;
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (mv == null) {
            return;
        }

        String className = AsmTypeNameUtils.toClassName(currentOwner);
        if (opcode == ATHROW) {
            String line = MethodInfoFormat.methodExitThrown(className, methodAccess, getName(), methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
        else if (opcode == RETURN) {
            String line = MethodInfoFormat.methodExitReturn(className, methodAccess, getName(), methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
        else {
            String line = MethodInfoFormat.methodExitReturn(className, methodAccess, getName(), methodDesc);
            Type methodType = Type.getMethodType(methodDesc);
            Type returnType = methodType.getReturnType();
            dupValueOnStack(mv, returnType);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, returnType, line + " - [ReturnValue] ");
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (mv == null) {
            return;
        }


        if (isNotReady()) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
            return;
        }

        String line = String.format("    %s %s::%s:%s - ",
                OpcodeConst.getOpcodeName(opcode),
                AsmTypeNameUtils.toClassName(owner),
                name, descriptor);
        Type fieldType = Type.getType(descriptor);

        // pre
        if (opcode == PUTFIELD || opcode == PUTSTATIC) {
            dupValueOnStack(mv, fieldType);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, fieldType, line);
        }

        // field op: get or put
        super.visitFieldInsn(opcode, owner, name, descriptor);

        // post
        if (opcode == GETFIELD || opcode == GETSTATIC) {
            dupValueOnStack(mv, fieldType);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, fieldType, line);
        }
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (mv == null) {
            return;
        }


        if (isNotReady()) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            return;
        }

        // method instruction pre
        String preMsg = String.format("    %s %s::%s:%s [>>>]",
                OpcodeConst.getOpcodeName(opcodeAndSource),
                AsmTypeNameUtils.toClassName(owner),
                name, descriptor);
        AsmInsnUtilsForPrint.printMessage(mv, preMsg);

        // method args
        @ThinkStep("(1) 如果是 invokestatck 指令，说明是 static 方法，不存在 this")
        boolean isStatic = opcodeAndSource == Opcodes.INVOKESTATIC;
        @ThinkStep("(2) 如果方法名称是 <init>，说明是 constructor 方法，此时的 receiver type 是 UNINITIALIZED_THIS")
        boolean isInstanceInit = INIT_METHOD_NAME.equals(name);
        @ThinkStep("(3) 排除这两种情况")
        boolean hasReceiverType = !(isStatic || isInstanceInit);
        AsmInsnUtilsForMethodInvoke.printInvokeMethodInsnParams(mv, hasReceiverType, descriptor, 8);

        // method invoke
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);

        // method return value
        Type methodType = Type.getMethodType(descriptor);
        Type returnType = methodType.getReturnType();

        String postMsg = String.format("    %s %s::%s:%s [<<<] ",
                OpcodeConst.getOpcodeName(opcodeAndSource), AsmTypeNameUtils.toClassName(owner), name, descriptor);
        if (AsmTypeUtils.hasValidValue(returnType)) {
            dupValueOnStack(mv, returnType);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, returnType, postMsg);
        }
        else {
            AsmInsnUtilsForPrint.printMessage(mv, postMsg + "void");
        }
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if (mv == null) {
            return;
        }

        if (isNotReady()) {
            super.visitVarInsn(opcode, varIndex);
            return;
        }

        String line = String.format("    %s %d - ", OpcodeConst.getOpcodeName(opcode), varIndex);
        if (opcode == ISTORE) {
            dupValueOnStack(mv, Type.INT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.INT_TYPE, line);
        }
        else if (opcode == LSTORE) {
            dupValueOnStack(mv, Type.LONG_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.LONG_TYPE, line);
        }
        else if (opcode == FSTORE) {
            dupValueOnStack(mv, Type.FLOAT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.FLOAT_TYPE, line);
        }
        else if (opcode == DSTORE) {
            dupValueOnStack(mv, Type.DOUBLE_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.DOUBLE_TYPE, line);
        }
        else if (opcode == ASTORE) {
            dupValueOnStack(mv, OBJECT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, OBJECT_TYPE, line);
        }


        super.visitVarInsn(opcode, varIndex);


        if (opcode == ILOAD) {
            dupValueOnStack(mv, Type.INT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.INT_TYPE, line);
        }
        else if (opcode == LLOAD) {
            dupValueOnStack(mv, Type.LONG_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.LONG_TYPE, line);
        }
        else if (opcode == FLOAD) {
            dupValueOnStack(mv, Type.FLOAT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.FLOAT_TYPE, line);
        }
        else if (opcode == DLOAD) {
            dupValueOnStack(mv, Type.DOUBLE_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, Type.DOUBLE_TYPE, line);
        }
        else if (opcode == ALOAD) {
            dupValueOnStack(mv, OBJECT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, OBJECT_TYPE, line);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        if (mv == null) {
            return;
        }

        if (isNotReady()) {
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            return;
        }

        // enter
        String line = String.format("    invokedynamic %s:%s", name, descriptor);
        AsmInsnUtilsForPrint.printMessage(mv, line);

        // method args
        AsmInsnUtilsForMethodInvoke.printInvokeMethodInsnParams(mv, false, descriptor, 8);

        // bootstrapMethodHandle
        printHandle(bootstrapMethodHandle);

        // other method handle
        if (bootstrapMethodArguments != null) {
            for (Object arg : bootstrapMethodArguments) {
                if (arg instanceof Handle) {
                    Handle handle = (Handle) arg;
                    printHandle(handle);
                }
            }
        }

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);

        // method return value
        Type methodType = Type.getMethodType(descriptor);
        Type returnType = methodType.getReturnType();

        String postMsg = String.format("    invokedynamic %s:%s [<<<] ", name, descriptor);
        if (AsmTypeUtils.hasValidValue(returnType)) {
            dupValueOnStack(mv, returnType);
            AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, returnType, postMsg);
        }
        else {
            AsmInsnUtilsForPrint.printMessage(mv, postMsg + "void");
        }
    }

    private void printHandle(Handle handle) {
        int tag = handle.getTag();
        String msg = String.format("        %s %s::%s:%s", OpcodeConst.REFERENCE_KIND_NAMES[tag],
                handle.getOwner(), handle.getName(), handle.getDesc());
        AsmInsnUtilsForPrint.printMessage(mv, msg);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (mv == null) {
            return;
        }

        if (isNotReady()) {
            super.visitJumpInsn(opcode, label);
            return;
        }

        String line = "    " + OpcodeConst.getOpcodeName(opcode) + " ---> Jump";
        if (
                opcode == Opcodes.IFEQ || opcode == Opcodes.IFNE ||
                        opcode == Opcodes.IFLT || opcode == Opcodes.IFGE ||
                        opcode == Opcodes.IFGT || opcode == Opcodes.IFLE
        ) {
            AsmInsnUtilsForPrint.dupAndPrintValueOnStack(mv, Type.INT_TYPE, line + " - ", null);
        }
        else if (
                opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IF_ICMPNE ||
                        opcode == Opcodes.IF_ICMPLT || opcode == Opcodes.IF_ICMPGE ||
                        opcode == Opcodes.IF_ICMPGT || opcode == Opcodes.IF_ICMPLE
        ) {
            Type[] types = {Type.INT_TYPE, Type.INT_TYPE};
            // operand stack: int, int
            OpcodeForArray.arrayFromStack(mv, types);
            // operand stack: array
            AsmInsnUtilsForPrint.dupAndPrintValueOnStack(mv, OBJECT_ARRAY_TYPE, line + " - ", null);
            // operand stack: array
            OpcodeForArray.arrayToStack(mv, types, true);
            // operand stack: int, int
        }
        else if (opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ACMPNE) {
            Type[] types = {OBJECT_TYPE, OBJECT_TYPE};
            // operand stack: obj, obj
            OpcodeForArray.arrayFromStack(mv, types);
            // operand stack: array
            AsmInsnUtilsForPrint.dupAndPrintValueOnStack(mv, OBJECT_ARRAY_TYPE, line + " - ", null);
            // operand stack: array
            OpcodeForArray.arrayToStack(mv, types, true);
            // operand stack: obj, obj
        }
        else if (opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL) {
            AsmInsnUtilsForPrint.dupAndPrintValueOnStack(mv, OBJECT_TYPE, line + " - ", null);
        }
        else {
            AsmInsnUtilsForPrint.printMessage(mv, "    " + OpcodeConst.getOpcodeName(opcode) + " ---> Jump");
        }


        super.visitJumpInsn(opcode, label);

        if (opcode != Opcodes.GOTO) {
            AsmInsnUtilsForPrint.printMessage(mv, "    " + OpcodeConst.getOpcodeName(opcode) + " ---> XXXX");
        }
    }

    @Override
    public void visitLabel(Label label) {
        if (mv == null) {
            return;
        }

        if (isNotReady()) {
            super.visitLabel(label);
            return;
        }

        super.visitLabel(label);

        AsmInsnUtilsForPrint.printMessage(mv, "    Jump <---");
    }

    private boolean isReady() {
        return methodEnterHasBeenCalled;
    }

    private boolean isNotReady() {
        return !isReady();
    }
}
