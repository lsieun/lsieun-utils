package lsieun.utils.asm.insn;

import org.objectweb.asm.*;

public class InstructionAdapter {

    private final MethodVisitor mv;

    public InstructionAdapter(final MethodVisitor methodVisitor) {
        this.mv = methodVisitor;
    }

    // -----------------------------------------------------------------------------------------------

    /**
     * Generates a nop instruction.
     */
    public void nop() {
        mv.visitInsn(Opcodes.NOP);
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the constant to be pushed on the stack. This parameter must be an {@link Integer},
     *              a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link Type} of
     *              OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is 49, a
     *              {@link Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle constants,
     *              for classes whose version is 51 or a {@link ConstantDynamic} for a constant dynamic for
     *              classes whose version is 55.
     */
    public void aconst(final Object value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param intValue the constant to be pushed on the stack.
     */
    public void iconst(final int intValue) {
        if (intValue >= -1 && intValue <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + intValue);
        }
        else if (intValue >= Byte.MIN_VALUE && intValue <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, intValue);
        }
        else if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, intValue);
        }
        else {
            mv.visitLdcInsn(intValue);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param longValue the constant to be pushed on the stack.
     */
    public void lconst(final long longValue) {
        if (longValue == 0L || longValue == 1L) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) longValue);
        }
        else {
            mv.visitLdcInsn(longValue);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param floatValue the constant to be pushed on the stack.
     */
    public void fconst(final float floatValue) {
        int bits = Float.floatToIntBits(floatValue);
        if (bits == 0L || bits == 0x3F800000 || bits == 0x40000000) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) floatValue);
        }
        else {
            mv.visitLdcInsn(floatValue);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param doubleValue the constant to be pushed on the stack.
     */
    public void dconst(final double doubleValue) {
        long bits = Double.doubleToLongBits(doubleValue);
        if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) doubleValue);
        }
        else {
            mv.visitLdcInsn(doubleValue);
        }
    }

    /**
     * Generates the instruction to push the given type on the stack.
     *
     * @param type the type to be pushed on the stack.
     */
    public void tconst(final Type type) {
        mv.visitLdcInsn(type);
    }

    /**
     * Generates the instruction to push the given handle on the stack.
     *
     * @param handle the handle to be pushed on the stack.
     */
    public void hconst(final Handle handle) {
        mv.visitLdcInsn(handle);
    }

    /**
     * Generates the instruction to push the given constant dynamic on the stack.
     *
     * @param constantDynamic the constant dynamic to be pushed on the stack.
     */
    public void cconst(final ConstantDynamic constantDynamic) {
        mv.visitLdcInsn(constantDynamic);
    }

    public void load(final int varIndex, final Type type) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), varIndex);
    }

    public void aload(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IALOAD));
    }

    public void store(final int varIndex, final Type type) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), varIndex);
    }

    public void astore(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IASTORE));
    }

    public void pop() {
        mv.visitInsn(Opcodes.POP);
    }

    public void pop2() {
        mv.visitInsn(Opcodes.POP2);
    }

    public void dup() {
        mv.visitInsn(Opcodes.DUP);
    }

    public void dup2() {
        mv.visitInsn(Opcodes.DUP2);
    }

    public void dupX1() {
        mv.visitInsn(Opcodes.DUP_X1);
    }

    public void dupX2() {
        mv.visitInsn(Opcodes.DUP_X2);
    }

    public void dup2X1() {
        mv.visitInsn(Opcodes.DUP2_X1);
    }

    public void dup2X2() {
        mv.visitInsn(Opcodes.DUP2_X2);
    }

    public void swap() {
        mv.visitInsn(Opcodes.SWAP);
    }

    public void add(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IADD));
    }

    public void sub(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.ISUB));
    }

    public void mul(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IMUL));
    }

    public void div(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IDIV));
    }

    public void rem(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IREM));
    }

    public void neg(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.INEG));
    }

    public void shl(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.ISHL));
    }

    public void shr(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.ISHR));
    }

    public void ushr(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IUSHR));
    }

    public void and(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IAND));
    }

    public void or(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IOR));
    }

    public void xor(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IXOR));
    }

    public void iinc(final int varIndex, final int increment) {
        mv.visitIincInsn(varIndex, increment);
    }

    /**
     * Generates the instruction to cast from the first given type to the other.
     *
     * @param from a Type.
     * @param to   a Type.
     */
    public void cast(final Type from, final Type to) {
        AsmInsnUtilsForOpcode.castPrimitive(mv, from, to);
    }


    public void lcmp() {
        mv.visitInsn(Opcodes.LCMP);
    }

    public void cmpl(final Type type) {
        mv.visitInsn(type == Type.FLOAT_TYPE ? Opcodes.FCMPL : Opcodes.DCMPL);
    }

    public void cmpg(final Type type) {
        mv.visitInsn(type == Type.FLOAT_TYPE ? Opcodes.FCMPG : Opcodes.DCMPG);
    }

    public void ifeq(final Label label) {
        mv.visitJumpInsn(Opcodes.IFEQ, label);
    }

    public void ifne(final Label label) {
        mv.visitJumpInsn(Opcodes.IFNE, label);
    }

    public void iflt(final Label label) {
        mv.visitJumpInsn(Opcodes.IFLT, label);
    }

    public void ifge(final Label label) {
        mv.visitJumpInsn(Opcodes.IFGE, label);
    }

    public void ifgt(final Label label) {
        mv.visitJumpInsn(Opcodes.IFGT, label);
    }

    public void ifle(final Label label) {
        mv.visitJumpInsn(Opcodes.IFLE, label);
    }

    public void ificmpeq(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, label);
    }

    public void ificmpne(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPNE, label);
    }

    public void ificmplt(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPLT, label);
    }

    public void ificmpge(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, label);
    }

    public void ificmpgt(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPGT, label);
    }

    public void ificmple(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPLE, label);
    }

    public void ifacmpeq(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, label);
    }

    public void ifacmpne(final Label label) {
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, label);
    }

    public void goTo(final Label label) {
        mv.visitJumpInsn(Opcodes.GOTO, label);
    }

    public void jsr(final Label label) {
        mv.visitJumpInsn(Opcodes.JSR, label);
    }

    public void ret(final int varIndex) {
        mv.visitVarInsn(Opcodes.RET, varIndex);
    }

    public void tableswitch(final int min, final int max, final Label dflt, final Label... labels) {
        mv.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void lookupswitch(final Label dflt, final int[] keys, final Label[] labels) {
        mv.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void areturn(final Type type) {
        mv.visitInsn(type.getOpcode(Opcodes.IRETURN));
    }

    public void getstatic(final String owner, final String name, final String descriptor) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, descriptor);
    }

    public void putstatic(final String owner, final String name, final String descriptor) {
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, name, descriptor);
    }

    public void getfield(final String owner, final String name, final String descriptor) {
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, descriptor);
    }

    public void putfield(final String owner, final String name, final String descriptor) {
        mv.visitFieldInsn(Opcodes.PUTFIELD, owner, name, descriptor);
    }

    /**
     * Generates the instruction to call the given virtual method.
     *
     * @param owner       the internal name of the method's owner class (see {@link
     *                    Type#getInternalName()}).
     * @param name        the method's name.
     * @param descriptor  the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    public void invokevirtual(final String owner, final String name, final String descriptor, final boolean isInterface) {
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, descriptor, isInterface);
    }

    /**
     * Generates the instruction to call the given special method.
     *
     * @param owner       the internal name of the method's owner class (see {@link
     *                    Type#getInternalName()}).
     * @param name        the method's name.
     * @param descriptor  the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    public void invokespecial(final String owner, final String name, final String descriptor, final boolean isInterface) {
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, descriptor, isInterface);
    }


    /**
     * Generates the instruction to call the given static method.
     *
     * @param owner       the internal name of the method's owner class (see {@link
     *                    Type#getInternalName()}).
     * @param name        the method's name.
     * @param descriptor  the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    public void invokestatic(final String owner, final String name, final String descriptor, final boolean isInterface) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, descriptor, isInterface);
    }

    /**
     * Generates the instruction to call the given interface method.
     *
     * @param owner      the internal name of the method's owner class (see {@link
     *                   Type#getInternalName()}).
     * @param name       the method's name.
     * @param descriptor the method's descriptor (see {@link Type}).
     */
    public void invokeinterface(final String owner, final String name, final String descriptor) {
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, name, descriptor, true);
    }

    /**
     * Generates the instruction to call the given dynamic method.
     *
     * @param name                     the method's name.
     * @param descriptor               the method's descriptor (see {@link Type}).
     * @param bootstrapMethodHandle    the bootstrap method.
     * @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
     *                                 an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
     *                                 Type}, {@link Handle} or {@link ConstantDynamic} value. This method is allowed to modify
     *                                 the content of the array so a caller should expect that this array may change.
     */
    public void invokedynamic(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object[] bootstrapMethodArguments) {
        mv.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    public void anew(final Type type) {
        mv.visitTypeInsn(Opcodes.NEW, type.getInternalName());
    }

    /**
     * Generates the instruction to create and push on the stack an array of the given type.
     *
     * @param type an array Type.
     */
    public void newarray(final Type type) {
        AsmInsnUtilsForOpcode.newArray(mv, type);
    }

    public void arraylength() {
        mv.visitInsn(Opcodes.ARRAYLENGTH);
    }

    public void athrow() {
        mv.visitInsn(Opcodes.ATHROW);
    }

    public void checkcast(final Type type) {
        mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }

    public void instanceOf(final Type type) {
        mv.visitTypeInsn(Opcodes.INSTANCEOF, type.getInternalName());
    }

    public void monitorenter() {
        mv.visitInsn(Opcodes.MONITORENTER);
    }

    public void monitorexit() {
        mv.visitInsn(Opcodes.MONITOREXIT);
    }

    public void multianewarray(final String descriptor, final int numDimensions) {
        mv.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    public void ifnull(final Label label) {
        mv.visitJumpInsn(Opcodes.IFNULL, label);
    }

    public void ifnonnull(final Label label) {
        mv.visitJumpInsn(Opcodes.IFNONNULL, label);
    }

    public void mark(final Label label) {
        mv.visitLabel(label);
    }
}