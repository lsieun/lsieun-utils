package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.MethodVisitor;

import static lsieun.utils.asm.core.ASMStringUtils.getClassMemberInfo;

public class FindInvokeInstructionRegexVisitor extends ClassRegexVisitor {
    public FindInvokeInstructionRegexVisitor(String[] includes, String[] excludes, String[] invokeRegex) {
        super(null, includes, excludes);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String name_desc = getClassMemberInfo(name, descriptor);
        boolean flag = isAppropriate(name_desc);
        if (flag) {
            return new FindInvokeInstructionRegexAdapter();
        }
        return null;
    }

    private class FindInvokeInstructionRegexAdapter extends MethodVisitor {

        public FindInvokeInstructionRegexAdapter() {
            super(MyAsmConst.ASM_API_VERSION, null);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            MatchItem item = MatchItem.ofMethod(owner, name, descriptor);
            addResult(item);
        }
    }
}
