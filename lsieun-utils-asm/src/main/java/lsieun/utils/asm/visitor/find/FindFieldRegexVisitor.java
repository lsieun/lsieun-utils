package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.core.ASMStringUtils;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;
import org.objectweb.asm.FieldVisitor;

public class FindFieldRegexVisitor extends ClassRegexVisitor {
    private String owner;

    public FindFieldRegexVisitor(String[] includes, String[] excludes) {
        super(null, includes, excludes);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.owner = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String name_and_desc = ASMStringUtils.getClassMemberInfo(name, descriptor);
        boolean flag = isAppropriate(name_and_desc);
        if (flag) {
            MatchItem item = MatchItem.ofField(owner, name, descriptor);
            addResult(item);
        }

        return null;
    }
}
