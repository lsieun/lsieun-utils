package lsieun.asm.visitor.analysis.check;

import lsieun.asm.cst.MyAsmConst;
import org.objectweb.asm.ClassVisitor;

public class ClassVisitorForCheckExistense extends ClassVisitor {
    protected boolean existsFlag;

    protected ClassVisitorForCheckExistense() {
        super(MyAsmConst.ASM_API_VERSION);
    }

    public boolean exists() {
        return existsFlag;
    }
}
