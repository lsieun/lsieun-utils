package lsieun.utils.asm.visitor.transformation.add;

import lsieun.utils.asm.cst.MyAsmConst;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ClassVisitorForAddInnerClass extends ClassVisitor implements Opcodes {
    private final String[] innerNames;
    private final boolean[] innerClassFlags;
    private final boolean[] nestMemberFlags;

    private String currentOwner;

    public ClassVisitorForAddInnerClass(ClassVisitor classVisitor, String[] innerNames) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.innerNames = innerNames;
        this.innerClassFlags = new boolean[innerNames.length];
        this.nestMemberFlags = new boolean[innerNames.length];
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentOwner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitNestMember(String nestMember) {
        if (nestMember != null) {
            int length = innerNames.length;
            for (int i = 0; i < length; i++) {
                String innerName = innerNames[i];
                String name = getInnerClassInternalName(innerName);
                if (nestMember.equals(name)) {
                    nestMemberFlags[i] = true;
                }
            }
        }

        super.visitNestMember(nestMember);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (innerName != null) {
            int length = innerNames.length;
            for (int i = 0; i < length; i++) {
                if (innerName.equals(innerNames[i])) {
                    innerClassFlags[i] = true;
                }
            }
        }

        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitEnd() {
        int length = innerNames.length;
        for (int i = 0; i < length; i++) {
            String innerName = innerNames[i];
            String innerClassInternalName = getInnerClassInternalName(innerName);

            if (!nestMemberFlags[i]) {
                cv.visitNestMember(innerClassInternalName);
            }

            if (!innerClassFlags[i]) {
                cv.visitInnerClass(innerClassInternalName, currentOwner, innerName,
                        ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);
            }
        }

        super.visitEnd();
    }

    private String getInnerClassInternalName(String innerName) {
        return String.format("%s$%s", currentOwner, innerName);
    }
}
