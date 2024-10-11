package lsieun.utils.asm.visitor.match;

import lsieun.utils.asm.cst.MyAsmConst;
import org.objectweb.asm.ClassVisitor;

public class MatchFlagVisitor extends ClassVisitor implements MatchFlag {
    protected boolean matchFlag = false;

    protected int version;
    protected String currentOwner;

    protected MatchFlagVisitor() {
        super(MyAsmConst.ASM_API_VERSION);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.currentOwner = name;
    }

    @Override
    public boolean match() {
        return matchFlag;
    }
}
