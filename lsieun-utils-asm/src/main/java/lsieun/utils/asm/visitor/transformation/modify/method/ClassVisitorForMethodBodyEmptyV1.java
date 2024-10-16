package lsieun.utils.asm.visitor.transformation.modify.method;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.MethodInfoMatch;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class ClassVisitorForMethodBodyEmptyV1 extends ClassVisitor {
    private final MethodInfoMatch match;
    private int version;
    private String owner;

    public ClassVisitorForMethodBodyEmptyV1(ClassVisitor classVisitor, MethodInfoMatch match) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.match = match;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.owner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv == null) {
            return null;
        }

        // （1）如果是abstract方法或native方法，不处理
        if ((access & Opcodes.ACC_ABSTRACT) != 0) return mv;
        if ((access & Opcodes.ACC_NATIVE) != 0) return mv;


        // （2）如果符合条件，则进行处理
        boolean flag = match.test(version, owner, access, name, descriptor, signature, exceptions);
        if (flag) {
            // (1) method argument types and return type
            Type t = Type.getType(descriptor);
            Type[] argumentTypes = t.getArgumentTypes();
            Type returnType = t.getReturnType();


            // (2) compute the size of local variable and operand stack
            boolean isStaticMethod = ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC);
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
                mv.visitInsn(returnType.getOpcode(Opcodes.ICONST_1));
                mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
            }
            else {
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
            }
            mv.visitMaxs(stackSize, localSize);
            mv.visitEnd();
            return null;
        }

        return mv;
    }
}
