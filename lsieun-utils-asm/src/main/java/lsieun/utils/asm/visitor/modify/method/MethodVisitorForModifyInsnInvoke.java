package lsieun.utils.asm.visitor.modify.method;

import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.visitor.common.MethodVisitorForInsnInvokeMatch;
import org.objectweb.asm.MethodVisitor;

public class MethodVisitorForModifyInsnInvoke extends MethodVisitorForInsnInvokeMatch {
    private final InsnInvokeConsumer insnInvokeConsumer;

    protected MethodVisitorForModifyInsnInvoke(MethodVisitor methodVisitor,
                                               String currentType, String currentMethodName, String currentMethodDesc,
                                               InsnInvokeMatch insnInvokeMatch,
                                               InsnInvokeConsumer insnInvokeConsumer) {
        super(methodVisitor, currentType, currentMethodName, currentMethodDesc, insnInvokeMatch);
        this.insnInvokeConsumer = insnInvokeConsumer;
    }


    @Override
    protected void onMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        insnInvokeConsumer.accept(mv,
                currentType, currentMethodName, currentMethodDesc,
                opcode, owner, name, descriptor, isInterface);
    }
}
