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
import lsieun.asm.insn.opcode.AsmInsnUtilsForOpcode;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class InsnTraceAdaptor extends AdviceAdapter {
    private final String currentOwner;

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
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, returnType);
            AsmInsnUtilsForPrint.printValueOnStack(mv, returnType, line + " - [ReturnValue] ");
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (mv == null) {
            return;
        }

        String line = String.format("    %s %s::%s:%s - ",
                OpcodeConst.getOpcodeName(opcode),
                AsmTypeNameUtils.toClassName(owner),
                name, descriptor);
        Type fieldType = Type.getType(descriptor);

        // pre
        if (opcode == PUTFIELD || opcode == PUTSTATIC) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, fieldType);
            AsmInsnUtilsForPrint.printValueOnStack(mv, fieldType, line);
        }

        // field op: get or put
        super.visitFieldInsn(opcode, owner, name, descriptor);

        // post
        if (opcode == GETFIELD || opcode == GETSTATIC) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, fieldType);
            AsmInsnUtilsForPrint.printValueOnStack(mv, fieldType, line);
        }
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (mv == null) {
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
        boolean isInstanceInit = MyAsmConst.CONSTRUCTOR_INTERNAL_NAME.equals(name);
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
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, returnType);
            AsmInsnUtilsForPrint.printValueOnStack(mv, returnType, postMsg);
        }
        else {
            AsmInsnUtilsForPrint.printMessage(mv, postMsg + "void");
        }
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        String line = String.format("    %s %d - ", OpcodeConst.getOpcodeName(opcode), varIndex);
        if (opcode == ISTORE) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.INT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.INT_TYPE, line);
        }
        else if (opcode == LSTORE) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.LONG_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.LONG_TYPE, line);
        }
        else if (opcode == FSTORE) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.FLOAT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.FLOAT_TYPE, line);
        }
        else if (opcode == DSTORE) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.DOUBLE_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.DOUBLE_TYPE, line);
        }
        else if (opcode == ASTORE) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, MyAsmConst.OBJECT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, MyAsmConst.OBJECT_TYPE, line);
        }


        super.visitVarInsn(opcode, varIndex);


        if (opcode == ILOAD) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.INT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.INT_TYPE, line);
        }
        else if (opcode == LLOAD) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.LONG_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.LONG_TYPE, line);
        }
        else if (opcode == FLOAD) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.FLOAT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.FLOAT_TYPE, line);
        }
        else if (opcode == DLOAD) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.DOUBLE_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.DOUBLE_TYPE, line);
        }
        else if (opcode == ALOAD) {
            AsmInsnUtilsForOpcode.dupValueOnStack(mv, MyAsmConst.OBJECT_TYPE);
            AsmInsnUtilsForPrint.printValueOnStack(mv, MyAsmConst.OBJECT_TYPE, line);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        if (mv == null) {
            return;
        }

        String line = String.format("    invokedynamic %s:%s", name, descriptor);
        AsmInsnUtilsForPrint.printMessage(mv, line);
        if (bootstrapMethodHandle != null) {
            for (Object arg : bootstrapMethodArguments) {
                if (arg instanceof Handle) {
                    Handle handle = (Handle) arg;
                    int tag = handle.getTag();
                    String msg = String.format("        %s %s::%s:%s", OpcodeConst.REFERENCE_KIND_NAMES[tag],
                            handle.getOwner(), handle.getName(), handle.getDesc());
                    AsmInsnUtilsForPrint.printMessage(mv, msg);
                }
            }
        }

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (mv == null) {
            return;
        }

//        String line = "    " + OpcodeConst.getOpcodeName(opcode);
//        if (
//                opcode == Opcodes.IFEQ || opcode == Opcodes.IFNE ||
//                        opcode == Opcodes.IFLT || opcode == Opcodes.IFGE ||
//                        opcode == Opcodes.IFGT || opcode == Opcodes.IFLE
//        ) {
//            AsmInsnUtilsForOpcode.dupValueOnStack(mv, Type.INT_TYPE);
//            AsmInsnUtilsForPrint.printValueOnStack(mv, Type.INT_TYPE, line + " - ");
//        }


        AsmInsnUtilsForPrint.printMessage(mv, "    " + OpcodeConst.getOpcodeName(opcode) + " ---> Jump");

        super.visitJumpInsn(opcode, label);

        AsmInsnUtilsForPrint.printMessage(mv, "    " + OpcodeConst.getOpcodeName(opcode) + " ---> XXXX");
    }

    @Override
    public void visitLabel(Label label) {
        if (mv == null) {
            return;
        }

        super.visitLabel(label);

        AsmInsnUtilsForPrint.printMessage(mv, "    Jump <---");
    }
}
