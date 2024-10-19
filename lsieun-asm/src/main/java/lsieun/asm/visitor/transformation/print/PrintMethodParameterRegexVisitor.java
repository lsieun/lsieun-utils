package lsieun.asm.visitor.transformation.print;

import lsieun.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.core.ASMStringUtils.getClassMemberInfo;

public class PrintMethodParameterRegexVisitor extends ClassRegexVisitor {
    // 开始前，传入的参数
    private final boolean showMethodReturnValue;
    private final boolean showStackTrace;

    // 过程中，计录的数据
    private String owner;

    public PrintMethodParameterRegexVisitor(ClassVisitor cv, String[] includes, String[] excludes) {
        this(cv, includes, excludes, false, false);
    }

    public PrintMethodParameterRegexVisitor(ClassVisitor cv, String[] includes, String[] excludes,
                                            boolean showMethodReturnValue, boolean showStackTrace) {
        super(cv, includes, excludes);
        this.showMethodReturnValue = showMethodReturnValue;
        this.showStackTrace = showStackTrace;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // 先处理自己的逻辑
        this.owner = name;

        // 再处理别人的逻辑
        super.visit(version, access, name, signature, superName, interfaces);
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
            mv = new PrintMethodParameterRegexAdapter(mv, access, name, descriptor);
        }

        return mv;
    }

    private class PrintMethodParameterRegexAdapter extends PrintMethodAdapter {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;

        public PrintMethodParameterRegexAdapter(MethodVisitor methodVisitor, int methodAccess, String methodName, String methodDesc) {
            super(methodVisitor);
            this.methodAccess = methodAccess;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public void visitCode() {
            printMessage("=================================================================================");
            String line = String.format("Method Enter: %s.%s:%s", owner, methodName, methodDesc);
            printMessage(line);

            int slotIndex = (methodAccess & ACC_STATIC) != 0 ? 0 : 1;
            Type methodType = Type.getMethodType(methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            for (Type t : argumentTypes) {
                int size = t.getSize();
                int opcode = t.getOpcode(ILOAD);
                super.visitVarInsn(opcode, slotIndex);

                printValueOnStack(t);
                slotIndex += size;
            }

            if (showStackTrace) {
                printStackTrace();
            }

            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            if (showMethodReturnValue) {
                Type t = Type.getMethodType(methodDesc);
                Type returnType = t.getReturnType();
                if (opcode == ATHROW) {
                    String line = String.format("Method throws Exception: %s.%s:%s", owner, methodName, methodDesc);
                    printMessage(line);

                    super.visitLdcInsn("abnormal return");
                    printValueOnStack("(Ljava/lang/Object;)V");
                    printMessage("=================================================================================");
                }
                else if (opcode == RETURN) {
                    String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
                    printMessage(line);

                    super.visitLdcInsn("return void");
                    printValueOnStack("(Ljava/lang/Object;)V");
                    printMessage("=================================================================================");
                }
                else if (opcode >= IRETURN && opcode <= ARETURN) {
                    String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
                    printMessage(line);

                    dupAndPrintValueOnStack(returnType);
                    printMessage("=================================================================================");
                }
                else {
                    // other opcodes
                }

            }

            super.visitInsn(opcode);
        }
    }
}
