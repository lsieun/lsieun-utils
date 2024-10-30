package lsieun.asm.visitor.transformation.method;

import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.common.MethodMatchVisitor;

import org.objectweb.asm.*;

import static lsieun.asm.insn.opcode.OpcodeForConst.push;

public class ClassVisitorForMethodBodyEmpty extends MethodMatchVisitor {
    private final Object returnValue;

    public ClassVisitorForMethodBodyEmpty(ClassVisitor classVisitor, MethodInfoMatch match) {
        this(classVisitor, match, null);
    }
    public ClassVisitorForMethodBodyEmpty(ClassVisitor classVisitor, MethodInfoMatch match, Object returnValue) {
        super(classVisitor, match);
        this.returnValue = returnValue;
    }

    @Override
    protected MethodVisitor newMethodVisitor(MethodVisitor mv,
                                             int methodAccess, String methodName, String methodDesc,
                                             String signature, String[] exceptions) {
        // (1) method argument types and return type
        Type t = Type.getType(methodDesc);
        Type[] argumentTypes = t.getArgumentTypes();
        Type returnType = t.getReturnType();


        // (2) compute the size of local variable and operand stack
        boolean isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC);
        int localSize = isStaticMethod ? 0 : 1;
        for (Type argType : argumentTypes) {
            localSize += argType.getSize();
        }
        int stackSize = returnType.getSize();


        // (3) method body
        mv.visitCode();
        if (returnType.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
        }
        else if (returnType.getSort() >= Type.BOOLEAN && returnType.getSort() <= Type.DOUBLE) {
            if (returnValue == null) {
                mv.visitInsn(returnType.getOpcode(Opcodes.ICONST_0));
            }
            else if (returnValue instanceof Boolean) {
                push(mv, (boolean) returnValue);
            }
            else if (returnValue instanceof Integer) {
                push(mv, (int) returnValue);
            }
            else if (returnValue instanceof Long) {
                push(mv, (long) returnValue);
            }
            else if (returnValue instanceof Float) {
                push(mv, (float) returnValue);
            }
            else if (returnValue instanceof Double) {
                push(mv, (double) returnValue);
            }
            else {
                mv.visitInsn(returnType.getOpcode(Opcodes.ICONST_0));
            }
            mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
        }
        else {
            if (returnValue == null) {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            else if (returnValue instanceof String) {
                push(mv, (String) returnValue);
            }
            else if(returnValue instanceof Type) {
                push(mv, (Type)returnValue);
            }
            else if (returnValue instanceof Handle) {
                push(mv, (Handle) returnValue);
            }
            else if (returnValue instanceof ConstantDynamic) {
                push(mv, (ConstantDynamic) returnValue);
            }
            else {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            mv.visitInsn(Opcodes.ARETURN);
        }
        mv.visitMaxs(stackSize, localSize);
        mv.visitEnd();
        return null;
    }
}
