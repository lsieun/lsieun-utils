package lsieun.asm.visitor.common;

import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static lsieun.asm.cst.MyAsmConst.ASM_API_VERSION;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.*;

/**
 * <pre>
 *                                                     ┌─── onVisitMethodEnter()
 *                                                     │
 * ClassVisitorForMethodMatch ───┼─── visitMethod() ───┼─── MethodMatch.test()
 *                                                     │
 *                                                     └─── getNewMethodVisitor()
 * </pre>
 */
public abstract class MethodMatchVisitor extends ClassVisitor implements Opcodes {
    private static final Logger logger = LoggerFactory.getLogger(MethodMatchVisitor.class);
    private final MethodInfoMatch methodMatch;
    protected int version;
    protected String currentOwner;

    protected MethodMatchVisitor(ClassVisitor classVisitor, MethodInfoMatch methodMatch) {
        super(ASM_API_VERSION, classVisitor);
        this.methodMatch = methodMatch;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));

        // 可以做一些辅助性的工作，例如『判断方法是否存在』
        onVisitMethodEnter(version, currentOwner, access, name, descriptor, signature, exceptions);

        // match: find, modify
        boolean flag = methodMatch.test(currentOwner, access, name, descriptor, signature, exceptions);

        if (name.equals(PRINT_STACK_FRAME_METHOD_NAME) && descriptor.equals(PRINT_STACK_FRAME_METHOD_DESC)) {
            if (flag) {
                logger.warn(() -> MatchFormat.format(
                        MatchState.SKIP, ByteCodeElementType.METHOD,
                        PRINT_STACK_FRAME_METHOD_NAME + ":" + PRINT_STACK_FRAME_METHOD_DESC
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

        // (3) <init> or <clinit>, do not process
        if (name.equals(INIT_METHOD_NAME) || name.equals(CLINIT_METHOD_NAME)) {
            logger.debug(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, name));
            return mv;
        }

        // (4) if flag is true, then process
        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            mv = newMethodVisitor(mv, access, name, descriptor, signature, exceptions);
        }

        return mv;
    }

    /**
     * 可以做一些辅助性的工作，例如『判断方法是否存在』
     */
    protected void onVisitMethodEnter(int version, String owner,
                                      int methodAccess, String methodName, String methodDesc,
                                      String signature, String[] exceptions) {
    }

    protected abstract MethodVisitor newMethodVisitor(MethodVisitor mv,
                                                      int methodAccess, String methodName, String methodDesc,
                                                      String signature, String[] exceptions);
}
