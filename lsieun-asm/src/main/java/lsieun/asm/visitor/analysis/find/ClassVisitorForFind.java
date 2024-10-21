package lsieun.asm.visitor.analysis.find;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.match.result.MatchItem;
import lsieun.asm.match.result.MatchResult;
import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassVisitorForFind extends ClassVisitor implements MatchResult {
    protected final List<MatchItem> resultList = new ArrayList<>();
    protected int version;
    protected String currentOwner;

    protected ClassVisitorForFind() {
        super(MyAsmConst.ASM_API_VERSION);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public List<MatchItem> getResultList() {
        return resultList;
    }
}
