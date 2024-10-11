package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.MethodVisitor;

import static lsieun.utils.asm.core.ASMStringUtils.getClassMemberInfo;

public class FindMethodRegexVisitor extends ClassRegexVisitor {
    private String owner;

    public FindMethodRegexVisitor(String[] includes, String[] excludes) {
        super(null, includes, excludes);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String name_and_desc = getClassMemberInfo(name, descriptor);
        boolean flag = isAppropriate(name_and_desc);
        if (flag) {
            MatchItem item = MatchItem.ofMethod(owner, name, descriptor);
            addResult(item);
        }

        return null;
    }
}
