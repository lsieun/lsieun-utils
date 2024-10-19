package lsieun.asm.visitor.transformation.modify.method;

import lsieun.asm.insn.AsmInsnUtilsForMethod;
import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.visitor.common.ClassVisitorForMethodMatch;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

public class ClassVisitorForMethodBodyInfo extends ClassVisitorForMethodMatch {
    private final Set<MethodBodyInfoType> options;
    private boolean isPrintStackFrameMethodPresent = false;

    public ClassVisitorForMethodBodyInfo(ClassVisitor classVisitor, MethodInfoMatch match, Set<MethodBodyInfoType> options) {
        super(classVisitor, match);
        this.options = options;
    }

    // region visitMethod
    @Override
    protected void onVisitMethodEnter(int version, String owner,
                                      int methodAccess, String methodName, String methodDesc,
                                      String signature, String[] exceptions) {
        if (methodName.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME) && methodDesc.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC)) {
            isPrintStackFrameMethodPresent = true;
        }
    }

    @Override
    protected MethodVisitor newMethodVisitor(MethodVisitor mv,
                                             int methodAccess, String methodName, String methodDesc,
                                             String signature, String[] exceptions) {
        return new MethodVisitorForMethodBodyInfo(mv, version, currentOwner, methodAccess, methodName, methodDesc, options);
    }
    // endregion

    @Override
    public void visitEnd() {
        if (options.contains(MethodBodyInfoType.STACK_TRACE) && version >= (44 + 9) && !isPrintStackFrameMethodPresent) {
            AsmInsnUtilsForMethod.addPrintStackFrame(cv,
                    MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME, MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC);
        }

        super.visitEnd();
    }
}
