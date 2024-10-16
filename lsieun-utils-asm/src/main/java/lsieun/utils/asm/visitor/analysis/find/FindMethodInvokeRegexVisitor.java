package lsieun.utils.asm.visitor.analysis.find;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.MethodVisitor;

import static lsieun.utils.asm.core.ASMStringUtils.getClassMemberInfo;

public class FindMethodInvokeRegexVisitor extends ClassRegexVisitor {
    public FindMethodInvokeRegexVisitor(String[] includes, String[] excludes) {
        super(null, includes, excludes);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String name_desc = getClassMemberInfo(name, descriptor);
        boolean flag = isAppropriate(name_desc);
        if (flag) {
            return new FindMethodInvokeRegexAdapter();
        }
        return null;
    }

    private class FindMethodInvokeRegexAdapter extends MethodVisitor {
        public FindMethodInvokeRegexAdapter() {
            super(MyAsmConst.ASM_API_VERSION, null);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            MatchItem item = MatchItem.ofMethod(owner, name, descriptor);
            addResult(item);
        }
    }
}
