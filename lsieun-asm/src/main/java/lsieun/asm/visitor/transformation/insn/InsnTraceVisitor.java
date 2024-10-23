package lsieun.asm.visitor.transformation.insn;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InsnTraceVisitor extends ClassVisitor {
    private static final Logger logger = LoggerFactory.getLogger(InsnTraceVisitor.class);

    private final MethodInfoMatch methodMatch;
    protected String currentOwner;

    public InsnTraceVisitor(ClassVisitor classVisitor, MethodInfoMatch methodMatch) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.methodMatch = methodMatch;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));

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

        boolean flag = methodMatch.test(currentOwner, access, name, descriptor, signature, exceptions);
        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            mv = new InsnTraceAdaptor(mv, currentOwner, access, name, descriptor);
        }

        return mv;
    }
}