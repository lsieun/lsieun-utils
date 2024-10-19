package lsieun.asm.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static lsieun.asm.core.ASMStringUtils.getClassMemberInfo;

public class ReplaceMethodBodyRegexVisitor extends ClassRegexVisitor {
    private String owner;

    public ReplaceMethodBodyRegexVisitor(ClassVisitor cv, String[] includes, String[] excludes) {
        super(cv, includes, excludes);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv == null) {
            return null;
        }

        // （1）如果是abstract方法或native方法，不处理
        if ((access & ACC_ABSTRACT) != 0) return mv;
        if ((access & ACC_NATIVE) != 0) return mv;


        // （2）如果符合正则表达式，则进行处理
        String name_desc = getClassMemberInfo(name, descriptor);
        boolean flag = isAppropriate(name_desc);
        if (flag) {
            generateNewBody(mv, owner, access, name, descriptor, signature, exceptions);
            return null;
        }

        return mv;
    }

    protected void generateNewBody(MethodVisitor mv, String owner, int methodAccess, String methodName, String methodDesc, String signature, String[] exceptions) {
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
            mv.visitInsn(RETURN);
        }
        else if (returnType.getSort() >= Type.BOOLEAN && returnType.getSort() <= Type.DOUBLE) {
            mv.visitInsn(returnType.getOpcode(ICONST_1));
            mv.visitInsn(returnType.getOpcode(IRETURN));
        }
        else {
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
        }
        mv.visitMaxs(stackSize, localSize);
        mv.visitEnd();
    }
}
