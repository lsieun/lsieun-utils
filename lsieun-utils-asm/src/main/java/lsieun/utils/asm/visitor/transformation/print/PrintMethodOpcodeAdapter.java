package lsieun.utils.asm.visitor.transformation.print;

import lsieun.utils.asm.utils.OpcodeConst;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PrintMethodOpcodeAdapter extends PrintMethodAdapter {
    private static final String OPCODE_INSTRUCTION_FORMAT = "%-14s%-6s";

    public PrintMethodOpcodeAdapter(MethodVisitor mv) {
        super(mv);
    }

    // ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
    // ISTORE, LSTORE, FSTORE, DSTORE, ASTORE
    // or RET.
    @Override
    public void visitVarInsn(int opcode, int index) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), index);
        printMessage(instruction);

        if (opcode == ILOAD) {
            super.visitVarInsn(opcode, index);
            super.visitInsn(DUP);
            printValueOnStack("(I)V");
        }
        else if (opcode == LLOAD) {
            super.visitVarInsn(opcode, index);
            super.visitInsn(DUP2);
            printValueOnStack("(J)V");
        }
        else if (opcode == FLOAD) {
            super.visitVarInsn(opcode, index);
            super.visitInsn(DUP);
            printValueOnStack("(F)V");
        }
        else if (opcode == DLOAD) {
            super.visitVarInsn(opcode, index);
            super.visitInsn(DUP);
            printValueOnStack("(D)V");
        }
        else if (opcode == ALOAD) {
            super.visitVarInsn(opcode, index);
            super.visitInsn(DUP);
            printValueOnStack("(Ljava/lang/Object;)V");
        }
        else if (opcode == ISTORE) {
            super.visitInsn(DUP);
            super.visitVarInsn(opcode, index);
            printValueOnStack("(I)V");
        }
        else if (opcode == LSTORE) {
            super.visitInsn(DUP2);
            super.visitVarInsn(opcode, index);
            printValueOnStack("(J)V");
        }
        else if (opcode == FSTORE) {
            super.visitInsn(DUP);
            super.visitVarInsn(opcode, index);
            printValueOnStack("(F)V");
        }
        else if (opcode == DSTORE) {
            super.visitInsn(DUP2);
            super.visitVarInsn(opcode, index);
            printValueOnStack("(D)V");
        }
        else if (opcode == ASTORE) {
            super.visitInsn(DUP);
            super.visitVarInsn(opcode, index);
            printValueOnStack("(Ljava/lang/Object;)V");
        }
        else {
            super.visitVarInsn(opcode, index);
            super.visitLdcInsn("not supported");
            printValueOnStack("(Ljava/lang/Object;)V");
        }
    }

    @Override
    public void visitInsn(int opcode) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        super.visitInsn(opcode);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        String opcode_arg = String.format("%s %s", var, increment);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "iinc", opcode_arg);
        printMessage(instruction);

        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        String opcode_arg = String.valueOf(operand);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "LDC", value);
        printMessage(instruction);

        super.visitLdcInsn(value);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "tableswitch", "");
        printMessage(instruction);

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "lookupswitch", "");
        printMessage(instruction);

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        String opcode_arg = descriptor + ":" + numDimensions;
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "multianewarray", opcode_arg);
        printMessage(instruction);

        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitFieldInsn(opcode, owner, name, descriptor);
        if (opcode == GETFIELD || opcode == GETSTATIC) {
            Type t = Type.getType(descriptor);
            dupAndPrintValueOnStack(t);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        Type methodType = Type.getMethodType(descriptor);
        Type returnType = methodType.getReturnType();
        if (!returnType.equals(Type.VOID_TYPE)) {
            dupAndPrintValueOnStack(returnType);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        String opcode_arg = String.format("%s:%s", name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "invokedynamic", opcode_arg);
        printMessage(instruction);

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);

        Type methodType = Type.getMethodType(descriptor);
        Type returnType = methodType.getReturnType();
        dupAndPrintValueOnStack(returnType);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), type);
        printMessage(instruction);

        super.visitTypeInsn(opcode, type);
    }
}