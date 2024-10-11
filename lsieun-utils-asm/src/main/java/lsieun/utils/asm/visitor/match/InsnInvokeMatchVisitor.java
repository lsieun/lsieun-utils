package lsieun.utils.asm.visitor.match;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import org.objectweb.asm.MethodVisitor;

public class InsnInvokeMatchVisitor extends MatchFlagVisitor {
    private final MethodInfoMatch methodInfoMatch;
    private final InsnInvokeMatch insnInvokeMatch;

    public InsnInvokeMatchVisitor(MethodInfoMatch methodInfoMatch, InsnInvokeMatch insnInvokeMatch) {
        this.methodInfoMatch = methodInfoMatch;
        this.insnInvokeMatch = insnInvokeMatch;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        boolean flag = methodInfoMatch.test(version, currentOwner, access, name, descriptor, signature, exceptions);
        if (flag) {
            return new InsnInvokeMatchAdapter();
        }
        return null;
    }

    private class InsnInvokeMatchAdapter extends MethodVisitor {

        protected InsnInvokeMatchAdapter() {
            super(MyAsmConst.ASM_API_VERSION);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            boolean flag = insnInvokeMatch.test(opcode, owner, name, descriptor);
            matchFlag |= flag;
        }
    }
}
