package lsieun.asm.visitor.common;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.insn.AsmInsnUtilsForCodeFragment;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.format.MatchFormat;
import lsieun.asm.match.format.MatchState;
import lsieun.asm.utils.OpcodeConst;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class MethodVisitorForInsnInvokeMatch extends MethodVisitor {
    private static final Logger logger = LoggerFactory.getLogger(MethodVisitorForInsnInvokeMatch.class);

    protected final String currentType;
    protected final String currentMethodName;
    protected final String currentMethodDesc;
    private final InsnInvokeMatch insnInvokeMatch;
    private final boolean supportJump;

    protected MethodVisitorForInsnInvokeMatch(MethodVisitor methodVisitor,
                                              String currentType, String currentMethodName, String currentMethodDesc,
                                              InsnInvokeMatch insnInvokeMatch, boolean supportJump) {
        super(MyAsmConst.ASM_API_VERSION, methodVisitor);
        this.currentType = currentType;
        this.currentMethodName = currentMethodName;
        this.currentMethodDesc = currentMethodDesc;
        this.insnInvokeMatch = insnInvokeMatch;
        this.supportJump = supportJump;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.INSN, owner, name, descriptor));
        boolean flag = insnInvokeMatch.test(opcode, owner, name, descriptor);
        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.INSN, owner, name, descriptor));
            onMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
        else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (mv != null && supportJump) {
            AsmInsnUtilsForCodeFragment.printMessage(mv, "Jump --->" + OpcodeConst.getOpcodeName(opcode));
        }
        super.visitJumpInsn(opcode, label);
        if (mv != null && supportJump) {
            AsmInsnUtilsForCodeFragment.printMessage(mv, "Jump ---> XXX - " + OpcodeConst.getOpcodeName(opcode));
        }
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if (mv != null && supportJump) {
            AsmInsnUtilsForCodeFragment.printMessage(mv, "Jump <---");
        }
    }

    protected abstract void onMethodInsn(int opcode, String owner,
                                         String name, String descriptor, boolean isInterface);
}
