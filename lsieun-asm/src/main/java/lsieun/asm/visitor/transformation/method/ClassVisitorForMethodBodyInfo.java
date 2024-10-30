package lsieun.asm.visitor.transformation.method;

import lsieun.asm.insn.method.AsmInsnUtilsForMethod;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.common.MethodMatchVisitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_DESC;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_NAME;

public class ClassVisitorForMethodBodyInfo extends MethodMatchVisitor {
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
        if (methodName.equals(PRINT_STACK_FRAME_METHOD_NAME) && methodDesc.equals(PRINT_STACK_FRAME_METHOD_DESC)) {
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
                    PRINT_STACK_FRAME_METHOD_NAME,
                    PRINT_STACK_FRAME_METHOD_DESC);
        }

        super.visitEnd();
    }
}
