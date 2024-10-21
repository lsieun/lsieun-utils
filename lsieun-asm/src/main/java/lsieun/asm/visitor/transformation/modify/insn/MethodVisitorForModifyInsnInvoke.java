package lsieun.asm.visitor.transformation.modify.insn;

import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.visitor.common.MethodVisitorForInsnInvokeMatch;
import org.objectweb.asm.MethodVisitor;

public class MethodVisitorForModifyInsnInvoke extends MethodVisitorForInsnInvokeMatch {
    private final InsnInvokeConsumer insnInvokeConsumer;

    protected MethodVisitorForModifyInsnInvoke(MethodVisitor methodVisitor,
                                               String currentType, String currentMethodName, String currentMethodDesc,
                                               InsnInvokeMatch insnInvokeMatch, boolean supportJump,
                                               InsnInvokeConsumer insnInvokeConsumer) {
        super(methodVisitor, currentType, currentMethodName, currentMethodDesc, insnInvokeMatch, supportJump);
        this.insnInvokeConsumer = insnInvokeConsumer;
    }


    @Override
    protected void onMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        insnInvokeConsumer.accept(mv,
                currentType, currentMethodName, currentMethodDesc,
                opcode, owner, name, descriptor, isInterface);
    }
}
