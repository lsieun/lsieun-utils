package lsieun.asm.visitor.transformation.modify.insn;

import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.visitor.common.ClassVisitorForMethodMatch;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassVisitorForModifyInsnInvoke extends ClassVisitorForMethodMatch {
    private final InsnInvokeMatch insnInvokeMatch;
    private final InsnInvokeConsumer insnInvokeConsumer;
    private final boolean supportJump;

    public ClassVisitorForModifyInsnInvoke(ClassVisitor classVisitor,
                                           MethodInfoMatch methodMatch,
                                           InsnInvokeMatch insnInvokeMatch,
                                           InsnInvokeConsumer insnInvokeConsumer,
                                           boolean supportJump) {
        super(classVisitor, methodMatch);
        this.insnInvokeMatch = insnInvokeMatch;
        this.insnInvokeConsumer = insnInvokeConsumer;
        this.supportJump = supportJump;
    }

    @Override
    protected MethodVisitor newMethodVisitor(MethodVisitor mv,
                                             int methodAccess, String methodName, String methodDesc,
                                             String signature, String[] exceptions) {
        return new MethodVisitorForModifyInsnInvoke(mv,
                currentOwner, methodName, methodDesc,
                insnInvokeMatch, supportJump, insnInvokeConsumer);
    }
}
