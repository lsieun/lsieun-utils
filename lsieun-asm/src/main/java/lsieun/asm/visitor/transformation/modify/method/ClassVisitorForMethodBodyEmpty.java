package lsieun.asm.visitor.transformation.modify.method;

import lsieun.asm.insn.AsmInsnUtilsForOpcode;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.visitor.common.ClassVisitorForMethodMatch;
import org.objectweb.asm.*;

public class ClassVisitorForMethodBodyEmpty extends ClassVisitorForMethodMatch {
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
                AsmInsnUtilsForOpcode.push(mv, (boolean) returnValue);
            }
            else if (returnValue instanceof Integer) {
                AsmInsnUtilsForOpcode.push(mv, (int) returnValue);
            }
            else if (returnValue instanceof Long) {
                AsmInsnUtilsForOpcode.push(mv, (long) returnValue);
            }
            else if (returnValue instanceof Float) {
                AsmInsnUtilsForOpcode.push(mv, (float) returnValue);
            }
            else if (returnValue instanceof Double) {
                AsmInsnUtilsForOpcode.push(mv, (double) returnValue);
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
                AsmInsnUtilsForOpcode.push(mv, (String) returnValue);
            }
            else if(returnValue instanceof Type) {
                AsmInsnUtilsForOpcode.push(mv, (Type)returnValue);
            }
            else if (returnValue instanceof Handle) {
                AsmInsnUtilsForOpcode.push(mv, (Handle) returnValue);
            }
            else if (returnValue instanceof ConstantDynamic) {
                AsmInsnUtilsForOpcode.push(mv, (ConstantDynamic) returnValue);
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
