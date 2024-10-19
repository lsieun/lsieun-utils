package lsieun.asm.visitor.analysis.match;

import lsieun.asm.match.MemberInfoMatch;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class MemberInfoMatchVisitor extends MatchFlagVisitor {
    private final MemberInfoMatch match;

    public MemberInfoMatchVisitor(MemberInfoMatch match) {
        this.match = match;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        boolean flag = match.test(currentOwner, access, name, descriptor);
        matchFlag |= flag;
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        boolean flag = match.test(currentOwner, access, name, descriptor);
        matchFlag |= flag;
        return null;
    }
}
