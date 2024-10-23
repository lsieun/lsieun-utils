package lsieun.asm.visitor.transformation.method;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.insn.method.AsmInsnUtilsForMethod;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.consumer.AsmCodeFragment;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class ClassVisitorForAdvice extends ClassVisitor {
    private static final Logger logger = LoggerFactory.getLogger(ClassVisitorForAdvice.class);

    private final boolean supportStackTrace;
    private final MethodInfoMatch methodMatch;
    private final AsmCodeFragment methodEnterCodeSegment;
    private final AsmCodeFragment methodExitCodeSegmentForReturn;
    private final AsmCodeFragment methodExitCodeSegmentForThrown;

    private int version;
    private String currentOwner;
    private boolean isPrintStackFrameMethodPresent = false;

    private ClassVisitorForAdvice(ClassVisitor classVisitor, boolean supportStackTrace,
                                  MethodInfoMatch methodMatch,
                                  AsmCodeFragment methodEnterCodeSegment,
                                  AsmCodeFragment methodExitCodeSegmentForReturn,
                                  AsmCodeFragment methodExitCodeSegmentForThrown) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.supportStackTrace = supportStackTrace;
        this.methodMatch = methodMatch;
        this.methodEnterCodeSegment = methodEnterCodeSegment;
        this.methodExitCodeSegmentForReturn = methodExitCodeSegmentForReturn;
        this.methodExitCodeSegmentForThrown = methodExitCodeSegmentForThrown;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.debug(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));


        // match: find, modify
        boolean flag = methodMatch.test(currentOwner, access, name, descriptor, signature, exceptions);

        if (name.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME) && descriptor.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC)) {
            isPrintStackFrameMethodPresent = true;
            if (flag) {
                logger.warn(() -> MatchFormat.format(
                        MatchState.SKIP, ByteCodeElementType.METHOD,
                        MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME + ":" + MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC
                ));
                flag = false;
            }
        }

        // mv
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        // (1) mv is null
        if (mv == null) {
            logger.debug(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, "mv is null"));
            return mv;
        }

        // (2) abstract or native, do not process
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        boolean isNative = (access & Opcodes.ACC_NATIVE) != 0;
        if (isAbstract || isNative) {
            logger.debug(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, "native or abstract"));
            return mv;
        }

        // (3) if flag is true, then process
        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            mv = new MethodVisitorForAdvice(mv, access, name, descriptor, signature, exceptions);
        }
        else {
            logger.debug(() -> MatchFormat.format(MatchState.MISMATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
        }

        return mv;
    }

    @Override
    public void visitEnd() {
        if (!isPrintStackFrameMethodPresent && supportStackTrace) {
            AsmInsnUtilsForMethod.addPrintStackFrame(cv,
                    MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME, MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC);
        }

        super.visitEnd();
    }

    private class MethodVisitorForAdvice extends AdviceAdapter {
        private final String signature;
        private final String[] exceptions;

        protected MethodVisitorForAdvice(MethodVisitor methodVisitor,
                                         int access, String name, String descriptor,
                                         String signature, String[] exceptions) {
            super(MyAsmConst.ASM_API_VERSION, methodVisitor, access, name, descriptor);
            this.signature = signature;
            this.exceptions = exceptions;
        }

        @Override
        protected void onMethodEnter() {
            if (methodEnterCodeSegment != null) {
                methodEnterCodeSegment.implement(mv, currentOwner,
                        getAccess(), getName(), methodDesc,
                        signature, exceptions);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (opcode == ATHROW) {
                if (methodExitCodeSegmentForThrown != null) {
                    methodExitCodeSegmentForThrown.implement(mv, currentOwner,
                            getAccess(), getName(), methodDesc,
                            signature, exceptions);
                }
            }
            else {
                if (methodExitCodeSegmentForReturn != null) {
                    methodExitCodeSegmentForReturn.implement(mv, currentOwner,
                            getAccess(), getName(), methodDesc,
                            signature, exceptions);
                }
            }
        }
    }

    public static AddClassVisitor builder() {
        return cv -> supportStackTrace -> methodMatch ->
               methodEnterCodeSegment -> methodExitCodeSegmentForReturn -> methodExitCodeSegmentForThrown ->
               () -> new ClassVisitorForAdvice(
                       cv, supportStackTrace, methodMatch, methodEnterCodeSegment,
                       methodExitCodeSegmentForReturn, methodExitCodeSegmentForThrown
               );
    }

    public interface AddClassVisitor {
        AddStackTraceSupport withClassVisitor(ClassVisitor classVisitor);
    }

    public interface AddStackTraceSupport {
        AddMethodMatch withStackTrace(boolean supportStackTrace);
    }

    public interface AddMethodMatch {
        AddMethodEnter withMethodMatch(MethodInfoMatch methodMatch);
    }

    public interface AddMethodEnter {
        AddMethodExitForReturn methodEnter(AsmCodeFragment methodEnterCodeSegment);
    }

    public interface AddMethodExitForReturn {
        AddMethodExitForThrown withMethodReturn(AsmCodeFragment methodExitCodeSegment);
    }

    public interface AddMethodExitForThrown {
        StepByStep withMethodExitThrown(AsmCodeFragment methodExitCodeSegment);
    }

    public interface StepByStep {
        ClassVisitorForAdvice build();
    }
}
