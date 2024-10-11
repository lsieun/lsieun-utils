package lsieun.utils.asm.visitor;

import lsieun.utils.asm.cst.MyAsmConst;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class TransformerVisitor extends ClassVisitor {
    private String containerName;

    public TransformerVisitor(ClassVisitor cv, String containerName) {
        super(MyAsmConst.ASM_API_VERSION, cv);
        this.containerName = containerName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null &&
                name.equals("transform") &&
                descriptor.equals("(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;Ljava/security/ProtectionDomain;[B)[B")) {
            mv = new TransformerAdapter(mv, access, name, descriptor);
        }
        return mv;
    }

    private class TransformerAdapter extends AdviceAdapter {

        public TransformerAdapter(MethodVisitor mv, int access, String name, String descriptor) {
            super(MyAsmConst.ASM_API_VERSION, mv, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            if (mv != null) {
                mv.visitTypeInsn(NEW, containerName);
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKESPECIAL, containerName, "<init>", "(Ljava/lang/String;[B)V", false);
                mv.visitMethodInsn(INVOKESTATIC, containerName, "start", "(L" + containerName + ";)V", false);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (mv != null) {
                if (opcode == ARETURN) {
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKESTATIC, containerName, "stop", "([BLjava/lang/String;)V", false);
                }
            }
        }
    }
}
