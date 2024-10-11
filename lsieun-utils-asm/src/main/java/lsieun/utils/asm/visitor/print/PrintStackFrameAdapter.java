package lsieun.utils.asm.visitor.print;

import lsieun.utils.asm.utils.OpcodeConst;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PrintStackFrameAdapter extends PrintMethodAdapter {
    private static final String OPCODE_INSTRUCTION_FORMAT = "%-14s%-6s";


    private final String owner;
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;


    public PrintStackFrameAdapter(MethodVisitor methodVisitor, String owner,
                                  int methodAccess, String methodName, String methodDesc) {
        super(methodVisitor);
        this.owner = owner;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visitCode() {
        boolean isStaticMethod = ((methodAccess & ACC_STATIC) != 0);
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();


        printMessage("=================================================================================");
        String line = String.format("Method Enter: %s.%s:%s", owner, methodName, methodDesc);
        printMessage(line);

        int slotIndex = isStaticMethod ? 0 : 1;
        for (Type t : argumentTypes) {
            int size = t.getSize();
            int opcode = t.getOpcode(ILOAD);
            super.visitVarInsn(opcode, slotIndex);

            printValueOnStack(t);
            slotIndex += size;
        }


        int localIndex = 0;
        if (!isStaticMethod) {
            pushByte(0);
            super.visitLdcInsn("this");
            Type thisType = Type.getObjectType("java/lang/String");
            addLocal(thisType);
            localIndex += 1;
        }

        for (Type t : argumentTypes) {
            pushByte(localIndex);
            int opcode = t.getOpcode(ILOAD);
            super.visitVarInsn(opcode, localIndex);
            addLocal(t);

            localIndex += t.getSize();
        }
        printStackFrame();

        super.visitCode();
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
            dupAndPushStack(Type.INT_TYPE);
            printStackFrame();
        }
        else if (opcode == LLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPushStack(Type.LONG_TYPE);
            printStackFrame();
        }
        else if (opcode == FLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPushStack(Type.FLOAT_TYPE);
            printStackFrame();
        }
        else if (opcode == DLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPushStack(Type.DOUBLE_TYPE);
            printStackFrame();
        }
        else if (opcode == ALOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPushStack(OBJECT_TYPE);
            printStackFrame();
        }
        else if (opcode == ISTORE) {
            Type t = Type.INT_TYPE;
            // local variables
            dup(t);
            pushByte(index);
            dup_x(t);
            pop();
            addLocal(t);

            // operand stack
            popStack(t.getSize());
            printStackFrame();

            super.visitVarInsn(opcode, index);
        }
        else if (opcode == LSTORE) {
            Type t = Type.LONG_TYPE;
            // local variables
            dup(t);
            pushByte(index);
            dup_x(t);
            pop();
            addLocal(t);

            // operand stack
            popStack(t.getSize());
            printStackFrame();

            super.visitVarInsn(opcode, index);
        }
        else if (opcode == FSTORE) {
            Type t = Type.FLOAT_TYPE;
            // local variables
            dup(t);
            pushByte(index);
            dup_x(t);
            pop();
            addLocal(t);

            // operand stack
            popStack(t.getSize());
            printStackFrame();

            super.visitVarInsn(opcode, index);
        }
        else if (opcode == DSTORE) {
            Type t = Type.DOUBLE_TYPE;
            // local variables
            dup(t);
            pushByte(index);
            dup_x(t);
            pop();
            addLocal(t);

            // operand stack
            popStack(t.getSize());
            printStackFrame();

            super.visitVarInsn(opcode, index);
        }
        else if (opcode == ASTORE) {
            Type t = OBJECT_TYPE;
            // local variables
            dup(t);
            pushByte(index);
            dup_x(t);
            pop();
            addLocal(t);

            // operand stack
            popStack(t.getSize());
            printStackFrame();

            super.visitVarInsn(opcode, index);
        }
        else {
            super.visitVarInsn(opcode, index);
            super.visitLdcInsn("not supported");
            printValueOnStack("(Ljava/lang/Object;)V");
        }
    }

    /**
     * NOP, ACONST_NULL,
     * ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
     * LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1,
     * IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
     * IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
     * POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP,
     * IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM,
     * INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
     */
    @Override
    public void visitInsn(int opcode) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        super.visitInsn(opcode);

        switch (opcode) {
            case ACONST_NULL:
                pushStackValue("NULL");
                printStackFrame();
                break;
            case ICONST_M1:
                pushStackValue("-1");
                printStackFrame();
                break;
            case ICONST_0:
            case FCONST_0:
                pushStackValue("0");
                printStackFrame();
                break;
            case LCONST_0:
            case DCONST_0:
                pushStackValue("0");
                pushStackValue("TOP");
                printStackFrame();
                break;
            case ICONST_1:
            case FCONST_1:
                pushStackValue("1");
                printStackFrame();
                break;
            case LCONST_1:
            case DCONST_1:
                pushStackValue("1");
                pushStackValue("TOP");
                printStackFrame();
                break;
            case ICONST_2:
            case FCONST_2:
                pushStackValue("2");
                printStackFrame();
                break;
            case ICONST_3:
                pushStackValue("3");
                printStackFrame();
                break;
            case ICONST_4:
                pushStackValue("4");
                printStackFrame();
                break;
            case ICONST_5:
                pushStackValue("5");
                printStackFrame();
                break;
            case IALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
                popAndDupAndPushStack(2, Type.INT_TYPE);
                printStackFrame();
                break;
            case LALOAD:
                popAndDupAndPushStack(2, Type.LONG_TYPE);
                printStackFrame();
                break;
            case FALOAD:
                popAndDupAndPushStack(2, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case DALOAD:
                popAndDupAndPushStack(2, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case AALOAD:
                popAndDupAndPushStack(2, OBJECT_TYPE);
                printStackFrame();
                break;
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                popStack(3);
                printStackFrame();
                break;
            case POP:
                popStack(1);
                printStackFrame();
                break;
            case POP2:
                popStack(2);
                printStackFrame();
                break;
            case DUP:
                dupStack();
                printStackFrame();
                break;
            case DUP2:
                dup2Stack();
                printStackFrame();
                break;
            case DUP_X1:
                dupx1Stack();
                printStackFrame();
                break;
            case DUP_X2:
                dupx2Stack();
                printStackFrame();
                break;
            case DUP2_X1:
                dup2x1Stack();
                printStackFrame();
                break;
            case DUP2_X2:
                dup2x2Stack();
                printStackFrame();
                break;
            case SWAP:
                swapStack();
                printStackFrame();
                break;
            case IADD:
            case ISUB:
            case IMUL:
            case IDIV:
            case IREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IAND:
            case IOR:
            case IXOR:
                popAndDupAndPushStack(2, Type.INT_TYPE);
                printStackFrame();
                break;
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
                popAndDupAndPushStack(2, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case LADD:
            case LSUB:
            case LMUL:
            case LDIV:
            case LREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
                popAndDupAndPushStack(4, Type.LONG_TYPE);
                printStackFrame();
                break;
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                popAndDupAndPushStack(4, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case INEG:
                popAndDupAndPushStack(1, Type.INT_TYPE);
                printStackFrame();
                break;
            case FNEG:
                popAndDupAndPushStack(1, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case LNEG:
                popAndDupAndPushStack(2, Type.LONG_TYPE);
                printStackFrame();
                break;
            case DNEG:
                popAndDupAndPushStack(2, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case I2L:
                popAndDupAndPushStack(1, Type.LONG_TYPE);
                printStackFrame();
                break;
            case I2F:
                popAndDupAndPushStack(1, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case I2D:
                popAndDupAndPushStack(1, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case L2I:
                popAndDupAndPushStack(2, Type.INT_TYPE);
                printStackFrame();
                break;
            case L2F:
                popAndDupAndPushStack(2, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case L2D:
                popAndDupAndPushStack(2, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case F2I:
                popAndDupAndPushStack(1, Type.INT_TYPE);
                printStackFrame();
                break;
            case F2L:
                popAndDupAndPushStack(1, Type.LONG_TYPE);
                printStackFrame();
                break;
            case F2D:
                popAndDupAndPushStack(1, Type.DOUBLE_TYPE);
                printStackFrame();
                break;
            case D2I:
                popAndDupAndPushStack(2, Type.INT_TYPE);
                printStackFrame();
                break;
            case D2L:
                popAndDupAndPushStack(2, Type.LONG_TYPE);
                printStackFrame();
                break;
            case D2F:
                popAndDupAndPushStack(2, Type.FLOAT_TYPE);
                printStackFrame();
                break;
            case I2B:
            case I2C:
            case I2S:
                popAndDupAndPushStack(1, Type.INT_TYPE);
                printStackFrame();
                break;
            case LCMP:
                popAndDupAndPushStack(4, Type.INT_TYPE);
                printStackFrame();
                break;
            case FCMPL:
            case FCMPG:
                popAndDupAndPushStack(2, Type.INT_TYPE);
                printStackFrame();
                break;
            case DCMPL:
            case DCMPG:
                popAndDupAndPushStack(4, Type.INT_TYPE);
                printStackFrame();
                break;
            case ARRAYLENGTH:
                popAndDupAndPushStack(1, Type.INT_TYPE);
                printStackFrame();
                break;
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
                // do nothing
                break;
            default:
                break;
        }
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        String opcode_arg = String.format("%s %s", var, increment);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "iinc", opcode_arg);
        printMessage(instruction);

        super.visitIincInsn(var, increment);

        super.visitVarInsn(ILOAD, var);
        pushStack(Type.INT_TYPE);
        printStackFrame();
    }

    /**
     * BIPUSH, SIPUSH or NEWARRAY.
     */
    @Override
    public void visitIntInsn(int opcode, int operand) {
        String opcode_arg = String.valueOf(operand);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitIntInsn(opcode, operand);

        switch (opcode) {
            case BIPUSH:
            case SIPUSH:
                dupAndPushStack(Type.INT_TYPE);
                printStackFrame();
                break;
            case NEWARRAY:
                dupAndPushStack(OBJECT_TYPE);
                printStackFrame();
                break;
        }
    }

    /**
     * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE,
     * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     * IF_ACMPEQ, IF_ACMPNE,
     * GOTO, JSR,
     * IFNULL or IFNONNULL.
     */
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        switch (opcode) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IFNULL:
            case IFNONNULL:
                popStack(1);
                printStackFrame();
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
                popStack(2);
                printStackFrame();
                break;
            case JSR:
                pushStackValue("returnAddress");
                printStackFrame();
                break;
        }

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "LDC", value);
        printMessage(instruction);

        super.visitLdcInsn(value);

        dupAndPushStack(OBJECT_TYPE);
        printStackFrame();
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "tableswitch", "");
        printMessage(instruction);

        popStack(1);
        printStackFrame();

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "lookupswitch", "");
        printMessage(instruction);

        popStack(1);
        printStackFrame();

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        String opcode_arg = descriptor + ":" + numDimensions;
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "multianewarray", opcode_arg);
        printMessage(instruction);

        super.visitMultiANewArrayInsn(descriptor, numDimensions);

        popStack(numDimensions);
        dupAndPushStack(OBJECT_TYPE);
        printStackFrame();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        Type t = Type.getType(descriptor);
        int size = t.getSize();
        switch (opcode) {
            case PUTFIELD:
                popStack(size + 1);
                printStackFrame();
                break;
            case PUTSTATIC:
                popStack(size);
                printStackFrame();
                break;
        }

        super.visitFieldInsn(opcode, owner, name, descriptor);
        if (opcode == GETFIELD || opcode == GETSTATIC) {
            dupAndPrintValueOnStack(t);
        }

        switch (opcode) {
            case GETFIELD:
                popStack(1);
                dupAndPushStack(t);
                printStackFrame();
                break;
            case GETSTATIC:
                dupAndPushStack(t);
                printStackFrame();
                break;
        }
    }

    /**
     * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        Type methodType = Type.getMethodType(descriptor);
        Type[] argumentTypes = methodType.getArgumentTypes();
        Type returnType = methodType.getReturnType();
        if (!returnType.equals(Type.VOID_TYPE)) {
            dupAndPrintValueOnStack(returnType);
        }

        int argSize = 0;
        for (Type t : argumentTypes) {
            argSize += t.getSize();
        }

        switch (opcode) {
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                popStack(argSize + 1);
                dupAndPushStack(returnType);
                printStackFrame();
                break;
            case INVOKESTATIC:
                popStack(argSize);
                dupAndPushStack(returnType);
                printStackFrame();
                break;
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        String opcode_arg = String.format("%s:%s", name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "invokedynamic", opcode_arg);
        printMessage(instruction);

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);

        Type methodType = Type.getMethodType(descriptor);
        Type[] argumentTypes = methodType.getArgumentTypes();
        Type returnType = methodType.getReturnType();
        dupAndPrintValueOnStack(returnType);

        int argSize = 0;
        for (Type t : argumentTypes) {
            argSize += t.getSize();
        }

        popStack(argSize);
        dupAndPushStack(returnType);
        printStackFrame();
    }

    /**
     * NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), type);
        printMessage(instruction);

        super.visitTypeInsn(opcode, type);

        String className;
        int index = type.lastIndexOf("/");
        if (index > 0) {
            className = type.substring(index + 1);
        }
        else {
            className = type;
        }
        switch (opcode) {
            case NEW:
                pushStackValue(className);
                printStackFrame();
                break;
            case ANEWARRAY:
                popStack(1);
                pushStackValue(className);
                printStackFrame();
                break;
            case CHECKCAST:
                popStack(1);
                pushStackValue(className);
                printStackFrame();
                break;
            case INSTANCEOF:
                popAndDupAndPushStack(1, Type.INT_TYPE);
                printStackFrame();
                break;
        }
    }
}
