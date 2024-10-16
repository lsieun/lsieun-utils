package lsieun.utils.asm.visitor.analysis.find;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.MethodVisitor;

import static lsieun.utils.asm.core.ASMStringUtils.getClassMemberInfo;

public class FindMethodRefRegexVisitor extends ClassRegexVisitor {
    public final String refClassName;

    private String currentClassName;

    public FindMethodRefRegexVisitor(String refClassName, String[] includes, String[] excludes) {
        super(null, includes, excludes);
        this.refClassName = refClassName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.currentClassName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ((access & ACC_ABSTRACT) != 0) return null;
        if ((access & ACC_NATIVE) != 0) return null;
        return new FindMethodRefRegexAdapter(name, descriptor);
    }

    private class FindMethodRefRegexAdapter extends MethodVisitor {
        private final String currentMethodName;
        private final String currentMethodDesc;

        public FindMethodRefRegexAdapter(String currentMethodName, String currentMethodDesc) {
            super(MyAsmConst.ASM_API_VERSION, null);
            this.currentMethodName = currentMethodName;
            this.currentMethodDesc = currentMethodDesc;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String name_desc = getClassMemberInfo(name, descriptor);
            if (owner.equals(refClassName) && isAppropriate(name_desc)) {
                MatchItem item = MatchItem.ofMethod(currentClassName, currentMethodName, currentMethodDesc);
                addResult(item);
            }
        }
    }
}
