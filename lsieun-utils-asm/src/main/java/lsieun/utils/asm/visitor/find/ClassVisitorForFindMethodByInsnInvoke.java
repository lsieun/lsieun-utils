package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.description.ByteCodeElementType;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import org.objectweb.asm.MethodVisitor;

public class ClassVisitorForFindMethodByInsnInvoke extends ClassVisitorForFind {
    public static final Logger logger = LoggerFactory.getLogger(ClassVisitorForFindMethodByInsnInvoke.class);

    private final MethodInfoMatch methodMatch;
    private final InsnInvokeMatch insnInvokeMatch;

    public ClassVisitorForFindMethodByInsnInvoke(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        this.methodMatch = methodMatch;
        this.insnInvokeMatch = insnInvokeMatch;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
        boolean flag = methodMatch.test(version, currentOwner, access, name, descriptor, signature, exceptions);
        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            return new MethodVisitorForFindInsnInvoke(name, descriptor);
        }
        else {
            return null;
        }
    }


    private class MethodVisitorForFindInsnInvoke extends MethodVisitor {
        protected final String currentMethodName;
        protected final String currentMethodDesc;

        public MethodVisitorForFindInsnInvoke(String currentMethodName, String currentMethodDesc) {
            super(MyAsmConst.ASM_API_VERSION);
            this.currentMethodName = currentMethodName;
            this.currentMethodDesc = currentMethodDesc;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.INSN, owner, name, descriptor));
            boolean flag = insnInvokeMatch.test(opcode, owner, name, descriptor);

            if (flag) {
                logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.INSN, owner, name, descriptor));
                MatchItem item = MatchItem.ofMethod(currentOwner, currentMethodName, currentMethodDesc);
                if (!resultList.contains(item)) {
                    resultList.add(item);
                }
            }
        }
    }
}
