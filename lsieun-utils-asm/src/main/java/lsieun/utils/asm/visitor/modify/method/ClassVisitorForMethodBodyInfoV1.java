package lsieun.utils.asm.visitor.modify.method;


import lsieun.utils.asm.code.CodeMethodUtils;
import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.match.MethodInfoMatch;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class ClassVisitorForMethodBodyInfoV1 extends ClassVisitor implements Opcodes {
    private final MethodInfoMatch match;
    private final Set<MethodBodyInfoType> options;

    private int version;
    private String owner;
    private boolean isPrintStackFrameMethodPresent = false;


    public ClassVisitorForMethodBodyInfoV1(ClassVisitor classVisitor, MethodInfoMatch match, Set<MethodBodyInfoType> options) {
        super(MyAsmConst.ASM_API_VERSION, classVisitor);
        this.match = match;
        this.options = options;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.owner = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME) && descriptor.equals(MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC)) {
            isPrintStackFrameMethodPresent = true;
        }

        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        // (1) mv is null
        if (mv == null) return mv;

        // (2) 如果是 abstract 方法或 native 方法，不处理
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        boolean isNative = (access & Opcodes.ACC_NATIVE) != 0;
        if (isAbstract || isNative) return mv;

        // (3) 如果是 <init> 方法或 <clinit> 方法，不处理
        if (name.equals("<init>") || name.equals("<clinit>")) return mv;

        // (4) 如果符合条件，则进行处理
        boolean flag = match.test(version, owner, access, name, descriptor, signature, exceptions);
        if (flag) {
            String line = String.format("---> %s::%s:%s", owner, name, descriptor);
            System.out.println(line);
            mv = new MethodVisitorForMethodBodyInfo(mv, version, owner, access, name, descriptor, options);
        }

        return mv;
    }

    @Override
    public void visitEnd() {
        if (options.contains(MethodBodyInfoType.STACK_TRACE) && version >= (44 + 9) && !isPrintStackFrameMethodPresent) {
            CodeMethodUtils.addPrintStackFrame(cv);
        }

        super.visitEnd();
    }
}
