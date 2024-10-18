package lsieun.utils.asm.visitor.analysis.match;

import lsieun.utils.asm.match.ClassInfoMatch;

public class ClassInfoMatchVisitor extends MatchFlagVisitor {
    private final ClassInfoMatch match;

    public ClassInfoMatchVisitor(ClassInfoMatch match) {
        this.match = match;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        matchFlag = match.test(version, access, name, signature, superName, interfaces);
    }
}
