package lsieun.utils.asm.visitor.find;

import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.ClassRegexVisitor;

public class FindInterfaceRegexVisitor extends ClassRegexVisitor {
    public FindInterfaceRegexVisitor(String[] includes, String[] excludes) {
        super(null, includes, excludes);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            return;
        }
        for (String itf : interfaces) {
            boolean flag = isAppropriate(itf);
            if (flag) {
                MatchItem item = MatchItem.ofType(name);
                addResult(item);
            }
        }
    }
}
