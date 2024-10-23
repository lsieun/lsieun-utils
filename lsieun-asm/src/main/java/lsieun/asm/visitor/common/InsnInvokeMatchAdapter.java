package lsieun.asm.visitor.common;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.cst.OpcodeConst;
import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.format.MatchFormat;
import lsieun.asm.insn.code.AsmInsnUtilsForPrint;
import lsieun.asm.match.MatchState;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class InsnInvokeMatchAdapter extends MethodVisitor {
    private static final Logger logger = LoggerFactory.getLogger(InsnInvokeMatchAdapter.class);

    protected final String currentType;
    protected final String currentMethodName;
    protected final String currentMethodDesc;
    private final InsnInvokeMatch insnInvokeMatch;
    private final boolean supportJump;

    protected InsnInvokeMatchAdapter(MethodVisitor methodVisitor,
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
            AsmInsnUtilsForPrint.printMessage(mv, "Jump --->" + OpcodeConst.getOpcodeName(opcode));
        }
        super.visitJumpInsn(opcode, label);
        if (mv != null && supportJump) {
            AsmInsnUtilsForPrint.printMessage(mv, "Jump ---> XXX - " + OpcodeConst.getOpcodeName(opcode));
        }
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if (mv != null && supportJump) {
            AsmInsnUtilsForPrint.printMessage(mv, "Jump <---");
        }
    }

    protected abstract void onMethodInsn(int opcode, String owner,
                                         String name, String descriptor, boolean isInterface);
}
