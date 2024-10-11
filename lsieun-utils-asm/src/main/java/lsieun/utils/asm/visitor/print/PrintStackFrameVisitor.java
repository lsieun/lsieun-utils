package lsieun.utils.asm.visitor.print;

import lsieun.utils.asm.cst.MyAsmConst;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class PrintStackFrameVisitor extends ClassVisitor {
    private final String methodName;
    private final String methodDesc;
    private String owner;

    public PrintStackFrameVisitor(ClassVisitor classVisitor, String methodName, String methodDesc) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = owner;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && name.equals(methodName) && descriptor.equals(methodDesc)) {
            mv = new PrintStackFrameAdapter(mv, owner, access, name, descriptor);
        }
        return mv;
    }
}
