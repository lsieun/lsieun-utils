package lsieun.asm.visitor.analysis.match;

import lsieun.asm.sam.match.MethodInfoMatch;

import org.objectweb.asm.MethodVisitor;

public class MethodInfoMatchVisitor extends MatchFlagVisitor {
    private final MethodInfoMatch match;

    public MethodInfoMatchVisitor(final MethodInfoMatch match) {
        this.match = match;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        boolean flag = match.test(currentOwner, access, name, descriptor, signature, exceptions);
        matchFlag |= flag;
        return null;
    }
}
