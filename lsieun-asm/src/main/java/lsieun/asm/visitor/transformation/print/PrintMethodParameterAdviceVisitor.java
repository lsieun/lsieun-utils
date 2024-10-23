package lsieun.asm.visitor.transformation.print;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.visitor.ClassRegexVisitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static lsieun.asm.core.ASMStringUtils.getClassMemberInfo;

public class PrintMethodParameterAdviceVisitor extends ClassRegexVisitor {

    // 开始前，传入的参数
    private final boolean showMethodReturnValue;
    private final boolean showStackTrace;

    // 过程中，计录的数据
    private String owner;

    public PrintMethodParameterAdviceVisitor(ClassVisitor cv, String[] includes, String[] excludes) {
        this(cv, includes, excludes, true, true);
    }

    public PrintMethodParameterAdviceVisitor(ClassVisitor cv, String[] includes, String[] excludes,
                                             boolean showMethodReturnValue,
                                             boolean showStackTrace) {
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
            mv = new PrintInitMethodParameterAdviceAdapter(mv, access, name, descriptor);
        }

        return mv;
    }

    private class PrintInitMethodParameterAdviceAdapter extends AdviceAdapter {

        public PrintInitMethodParameterAdviceAdapter(MethodVisitor mv, int access, String name, String descriptor) {
            super(MyAsmConst.ASM_API_VERSION, mv, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            printMessage("=================================================================================");
            String line = String.format("Method Enter: %s.%s:%s", owner, getName(), methodDesc);
            printMessage(line);

            int slotIndex = (methodAccess & ACC_STATIC) != 0 ? 0 : 1;
            Type methodType = Type.getMethodType(methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            for (Type t : argumentTypes) {
                int sort = t.getSort();
                int size = t.getSize();
                int opcode = t.getOpcode(ILOAD);
                super.visitVarInsn(opcode, slotIndex);
                if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
                    String desc = t.getDescriptor();
                    printValueOnStack("(" + desc + ")V");
                }
                else {
                    printValueOnStack("(Ljava/lang/Object;)V");
                }
                slotIndex += size;
            }

            if (showStackTrace) {
                printStackTrace();
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (showMethodReturnValue) {
                String line = String.format("Method Return: %s.%s:%s", owner, getName(), methodDesc);
                printMessage(line);

                Type methodType = Type.getMethodType(this.methodDesc);
                Type returnType = methodType.getReturnType();
                if (opcode == ATHROW) {
                    super.visitLdcInsn("abnormal return");
                    printValueOnStack("(Ljava/lang/Object;)V");
                }
                else if (opcode == RETURN) {
                    super.visitLdcInsn("return void");
                    printValueOnStack("(Ljava/lang/Object;)V");
                }
                else if (opcode == ARETURN) {
                    dup();
                    printValueOnStack("(Ljava/lang/Object;)V");
                }
                else if (opcode == LRETURN || opcode == DRETURN) {
                    dup2();
                    String desc = returnType.getDescriptor();
                    printValueOnStack("(" + desc + ")V");
                }
                else {
                    dup();
                    String desc = returnType.getDescriptor();
                    printValueOnStack("(" + desc + ")V");
                }
                printMessage("=================================================================================");
            }
        }

        private void printMessage(String str) {
            super.visitLdcInsn(str);
            super.visitMethodInsn(INVOKESTATIC, "lsieun/asm/utils/PrintUtils", "printText", "(Ljava/lang/String;)V", false);
        }

        private void printValueOnStack(String descriptor) {
            super.visitMethodInsn(INVOKESTATIC, "lsieun/asm/utils/PrintUtils", "printValueOnStack", descriptor, false);
        }

        private void printStackTrace() {
            super.visitMethodInsn(INVOKESTATIC, "lsieun/asm/utils/PrintUtils", "printStackTrace", "()V", false);
        }
    }
}
