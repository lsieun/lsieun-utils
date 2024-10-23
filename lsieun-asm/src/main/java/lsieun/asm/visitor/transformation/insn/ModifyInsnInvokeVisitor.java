package lsieun.asm.visitor.transformation.insn;

import lsieun.asm.sam.consumer.InsnInvokeConsumer;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.common.MethodMatchVisitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ModifyInsnInvokeVisitor extends MethodMatchVisitor {
    private final InsnInvokeMatch insnInvokeMatch;
    private final InsnInvokeConsumer insnInvokeConsumer;
    private final boolean supportJump;

    public ModifyInsnInvokeVisitor(ClassVisitor classVisitor,
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
        return new ModifyInsnInvokeAdaptor(mv,
                currentOwner, methodName, methodDesc,
                insnInvokeMatch, supportJump, insnInvokeConsumer);
    }
}
