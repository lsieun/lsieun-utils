package lsieun.asm.visitor.transformation.insn;

import lsieun.asm.sam.consumer.InsnInvokeConsumer;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.visitor.common.InsnInvokeMatchAdapter;

import org.objectweb.asm.MethodVisitor;

public class ModifyInsnInvokeAdaptor extends InsnInvokeMatchAdapter {
    private final InsnInvokeConsumer insnInvokeConsumer;

    protected ModifyInsnInvokeAdaptor(MethodVisitor methodVisitor,
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
