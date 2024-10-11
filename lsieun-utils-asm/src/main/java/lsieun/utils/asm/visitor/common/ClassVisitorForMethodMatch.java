package lsieun.utils.asm.visitor.common;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.description.ByteCodeElementType;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <pre>
 *                                                     ┌─── onVisitMethodEnter()
 *                                                     │
 * ClassVisitorForMethodMatch ───┼─── visitMethod() ───┼─── MethodMatch.test()
 *                                                     │
 *                                                     └─── getNewMethodVisitor()
 * </pre>
 */
public abstract class ClassVisitorForMethodMatch extends ClassVisitor implements Opcodes {
    private static final Logger logger = LoggerFactory.getLogger(ClassVisitorForMethodMatch.class);
    private final MethodInfoMatch methodMatch;
    protected int version;
    protected String currentOwner;

    protected ClassVisitorForMethodMatch(ClassVisitor classVisitor, MethodInfoMatch methodMatch) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
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
        boolean flag = methodMatch.test(version, currentOwner, access, name, descriptor, signature, exceptions);

        // mv
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        // (1) mv is null
        if (mv == null) {
            logger.trace(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, "mv is null"));
            return mv;
        }

        // (2) abstract or native, do not process
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        boolean isNative = (access & Opcodes.ACC_NATIVE) != 0;
        if (isAbstract || isNative) {
            logger.trace(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, "native or abstract"));
            return mv;
        }

        // (3) <init> or <clinit>, do not process
        if (name.equals("<init>") || name.equals("<clinit>")) {
            logger.trace(() -> MatchFormat.format(MatchState.SKIP, ByteCodeElementType.METHOD, "<init> or <clinit>"));
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
