package lsieun.asm.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static lsieun.asm.core.ASMStringUtils.getClassMemberInfo;

public class ProxyMethodRegexVisitor extends ClassRegexVisitor {
    private String owner;

    public ProxyMethodRegexVisitor(ClassVisitor cv, String[] includes, String[] excludes) {
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
            proxyEnter(mv, owner, access, name, descriptor);
            proxyOrigin(mv, owner, access, name, descriptor);
            proxyExit(mv, owner, access, name, descriptor);

            String newName = getNewName(name);
            return super.visitMethod(access, newName, descriptor, signature, exceptions);
        }

        return mv;
    }

    protected void proxyEnter(MethodVisitor mv, String owner, int methodAccess, String methodName, String methodDesc) {
        mv.visitCode();
    }

    private void proxyOrigin(MethodVisitor mv, String owner, int methodAccess, String methodName, String methodDesc) {
        Type methodType = Type.getType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        boolean isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) != 0);
        boolean isPrivateMethod = ((methodAccess & Opcodes.ACC_PRIVATE) != 0);

        if (!isStaticMethod) {
            mv.visitVarInsn(ALOAD, 0);
        }

        int localIndex = isStaticMethod ? 0 : 1;
        for (Type t : argumentTypes) {
            int opcode = t.getOpcode(ILOAD);
            mv.visitVarInsn(opcode, localIndex);

            localIndex += t.getSize();
        }

        String newMethodName = getNewName(methodName);
        if (isStaticMethod) {
            mv.visitMethodInsn(INVOKESTATIC, owner, newMethodName, methodDesc, false);
        }
        else if (isPrivateMethod) {
            mv.visitMethodInsn(INVOKESPECIAL, owner, newMethodName, methodDesc, false);
        }
        else {
            mv.visitMethodInsn(INVOKEVIRTUAL, owner, newMethodName, methodDesc, false);
        }

    }

    protected void proxyExit(MethodVisitor mv, String owner, int methodAccess, String methodName, String methodDesc) {
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

        // (3) return, visitMaxs and visitEnd
        int opcode = returnType.getOpcode(IRETURN);
        mv.visitInsn(opcode);
        mv.visitMaxs(stackSize, localSize);
        mv.visitEnd();
    }

    protected String getNewName(String name) {
        return String.format("origin_%s", name);
    }
}
