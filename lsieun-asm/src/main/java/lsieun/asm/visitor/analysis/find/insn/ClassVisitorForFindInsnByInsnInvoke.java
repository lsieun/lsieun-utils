package lsieun.asm.visitor.analysis.find.insn;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.match.format.MatchFormat;
import lsieun.asm.match.format.MatchState;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.match.result.MatchItem;
import lsieun.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import org.objectweb.asm.MethodVisitor;

public class ClassVisitorForFindInsnByInsnInvoke extends ClassVisitorForFind {
    public static final Logger logger = LoggerFactory.getLogger(ClassVisitorForFindInsnByInsnInvoke.class);

    private final MethodInfoMatch methodMatch;
    private final InsnInvokeMatch insnInvokeMatch;

    public ClassVisitorForFindInsnByInsnInvoke(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        this.methodMatch = methodMatch;
        this.insnInvokeMatch = insnInvokeMatch;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));

        boolean flag = methodMatch.test(version, currentOwner, access, name, descriptor, signature, exceptions);

        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            return new MethodVisitorForFindInsnInvoke();
        }
        else {
            return null;
        }
    }


    private class MethodVisitorForFindInsnInvoke extends MethodVisitor {

        public MethodVisitorForFindInsnInvoke() {
            super(MyAsmConst.ASM_API_VERSION);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.INSN, owner, name, descriptor));
            boolean flag = insnInvokeMatch.test(opcode, owner, name, descriptor);

            if (flag) {
                logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.INSN, owner, name, descriptor));
                MatchItem item = MatchItem.ofMethod(owner, name, descriptor);
                resultList.add(item);
            }
        }
    }
}
