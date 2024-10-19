package lsieun.asm.visitor.transformation.print;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.utils.PrintUtils;
import lsieun.asm.utils.StackFrameUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class PrintMethodAdapter extends MethodVisitor implements Opcodes {
    public static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
    private static final String PRINT_UTILS = PrintUtils.class.getName().replace('.', '/');
    private static final String STACK_FRAME_UTILS = StackFrameUtils.class.getName().replace('.', '/');

    public PrintMethodAdapter(MethodVisitor methodVisitor) {
        super(MyAsmConst.ASM_API_VERSION, methodVisitor);
    }

    // region Common
    protected void dup(Type t) {
        int size = t.getSize();
        if (size == 1) {
            super.visitInsn(DUP);
        }
        else if (size == 2) {
            super.visitInsn(DUP2);
        }
        else {
            // do nothing
        }
    }

    protected void dup_x(Type t) {
        int size = t.getSize();
        if (size == 1) {
            super.visitInsn(DUP_X1);
        }
        else if (size == 2) {
            super.visitInsn(DUP_X2);
        }
        else {
            // do nothing
        }
    }

    protected void pop() {
        super.visitInsn(POP);
    }

    protected void pushByte(int value) {
        super.visitIntInsn(BIPUSH, value);
    }
    // endregion

    // region PrintUtils
    protected void printMessage(String str) {
        super.visitLdcInsn(str);
        super.visitMethodInsn(INVOKESTATIC, PRINT_UTILS, "printText", "(Ljava/lang/String;)V", false);
    }

    protected void printValueOnStack(String descriptor) {
        super.visitMethodInsn(INVOKESTATIC, PRINT_UTILS, "printValueOnStack", descriptor, false);
    }

    protected void printValueOnStack(Type t) {
        int sort = t.getSort();
        if (sort == 0) {
            super.visitLdcInsn("void");
            printValueOnStack("(Ljava/lang/Object;)V");
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            String desc = t.getDescriptor();
            printValueOnStack("(" + desc + ")V");
        }
        else {
            printValueOnStack("(Ljava/lang/Object;)V");
        }
    }

    protected void dupAndPrintValueOnStack(Type t) {
        dup(t);
        printValueOnStack(t);
    }

    protected void printStackTrace() {
        super.visitMethodInsn(INVOKESTATIC, PRINT_UTILS, "printStackTrace", "()V", false);
    }
    // endregion

    // region StackFrameUtils
    protected void addLocal(Type t) {
        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            String desc = String.format("(I%s)V", t.getDescriptor());
            addLocal(desc);
        }
        else {
            addLocal("(ILjava/lang/Object;)V");
        }
    }

    protected void addLocal(String desc) {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "addLocal", desc, false);
    }

    protected void pushStack(Type t) {
        int sort = t.getSort();
        if (sort == Type.VOID) {
            // do nothing
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            String desc = String.format("(%s)V", t.getDescriptor());
            pushStack(desc);
        }
        else {
            pushStack("(Ljava/lang/Object;)V");
        }
    }

    protected void pushStack(String desc) {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "pushStack", desc, false);
    }

    protected void pushStackValue(String value) {
        super.visitLdcInsn(value);
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "pushStack", "(Ljava/lang/Object;)V", false);
    }

    protected void dupStack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dup", "()V", false);
    }

    protected void dup2Stack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dup2", "()V", false);
    }

    protected void dupx1Stack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dupx1", "()V", false);
    }

    protected void dupx2Stack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dupx2", "()V", false);
    }

    protected void dup2x1Stack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dup2x1", "()V", false);
    }

    protected void dup2x2Stack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "dup2x2", "()V", false);
    }

    protected void swapStack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "swap", "()V", false);
    }

    protected void popStack() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "pop", "(I)V", false);
    }

    protected void popStack(int num) {
        super.visitIntInsn(BIPUSH, num);
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "pop", "(I)V", false);
    }

    protected void popAndDupAndPushStack(int popSize, Type t) {
        popStack(popSize);
        dupAndPushStack(t);
    }

    protected void dupAndPushStack(Type t) {
        dup(t);
        pushStack(t);
    }

    protected void printStackFrame() {
        super.visitMethodInsn(INVOKESTATIC, STACK_FRAME_UTILS, "printStackFrame", "()V", false);
    }
    // endregion
}
