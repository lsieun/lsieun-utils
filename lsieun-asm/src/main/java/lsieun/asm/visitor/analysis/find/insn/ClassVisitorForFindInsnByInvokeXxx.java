package lsieun.asm.visitor.analysis.find.insn;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.match.MatchItem;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class ClassVisitorForFindInsnByInvokeXxx extends ClassVisitorForFind {
    public static final Logger logger = LoggerFactory.getLogger(ClassVisitorForFindInsnByInvokeXxx.class);

    private final MethodInfoMatch methodMatch;
    private final InsnInvokeMatch insnInvokeMatch;
    private final boolean deduplicate;

    public ClassVisitorForFindInsnByInvokeXxx(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        this(methodMatch, insnInvokeMatch, true);
    }

    public ClassVisitorForFindInsnByInvokeXxx(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch,
                                              boolean deduplicate) {
        this.methodMatch = methodMatch;
        this.insnInvokeMatch = insnInvokeMatch;
        this.deduplicate = deduplicate;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));

        boolean flag = methodMatch.test(currentOwner, access, name, descriptor, signature, exceptions);

        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            return new MethodVisitorForFindInsnInvoke(name, descriptor);
        }
        else {
            return null;
        }
    }


    private class MethodVisitorForFindInsnInvoke extends MethodVisitor {
        private final String currentMethodName;
        private final String currentMethodDesc;

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

                // (1) 添加 method
                MatchItem methodMatchItem = MatchItem.ofMethod(currentOwner, currentMethodName, currentMethodDesc);
                if (!resultList.contains(methodMatchItem)) {
                    resultList.add(methodMatchItem);
                }
                else {
                    int index = resultList.indexOf(methodMatchItem);
                    methodMatchItem = resultList.get(index);
                }

                // (2) 添加 instruction
                List<MatchItem> children = methodMatchItem.children;
                MatchItem item = MatchItem.ofInsn(owner, name, descriptor);
                if (deduplicate) {
                    if (!children.contains(item)) {
                        children.add(item);
                    }
                }
                else {
                    children.add(item);
                }
            }
        }
    }
}
