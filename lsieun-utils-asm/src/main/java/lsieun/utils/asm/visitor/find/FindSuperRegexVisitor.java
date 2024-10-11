package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;

public class FindSuperRegexVisitor extends ClassRegexVisitor {

    public FindSuperRegexVisitor(String[] includes) {
        super(null, includes, null);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        boolean flag = isAppropriate(superName);
        if (flag) {
            MatchItem item = MatchItem.ofType(name);
            addResult(item);
        }
    }
}
