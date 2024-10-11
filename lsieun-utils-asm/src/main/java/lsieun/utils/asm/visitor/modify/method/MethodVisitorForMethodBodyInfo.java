package lsieun.utils.asm.visitor.modify.method;


import lsieun.utils.asm.consumer.StdMethodConsumer;
import lsieun.utils.asm.cst.MyAsmConst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Objects;
import java.util.Set;

public class MethodVisitorForMethodBodyInfo extends MethodVisitor implements Opcodes {

    private final int version;
    private final String owner;
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;
    private final Set<MethodBodyInfoType> options;

    public MethodVisitorForMethodBodyInfo(MethodVisitor methodVisitor,
                                          int version,
                                          String owner, int methodAccess, String methodName, String methodDesc,
                                          Set<MethodBodyInfoType> options) {
        super(MyAsmConst.ASM_API_VERSION, methodVisitor);

        Objects.requireNonNull(options);

        this.version = version;
        this.owner = owner;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.options = options;

    }

    @Override
    public void visitCode() {
        if (mv != null) {
            if (options.contains(MethodBodyInfoType.ENTER)) {
                StdMethodConsumer.ENTER.accept(mv, version, owner, methodAccess, methodName, methodDesc);
            }

            if (options.contains(MethodBodyInfoType.PARAMETER_VALUES)) {
                StdMethodConsumer.PARAMETER_VALUES.accept(mv, version, owner, methodAccess, methodName, methodDesc);
            }

            if (options.contains(MethodBodyInfoType.THREAD_INFO)) {
                StdMethodConsumer.THREAD_INFO.accept(mv, version, owner, methodAccess, methodName, methodDesc);
            }

            if (options.contains(MethodBodyInfoType.CLASSLOADER)) {
                StdMethodConsumer.CLASSLOADER.accept(mv, version, owner, methodAccess, methodName, methodDesc);
            }

            if (options.contains(MethodBodyInfoType.STACK_TRACE)) {
                StdMethodConsumer.STACK_TRACE.accept(mv, version, owner, methodAccess, methodName, methodDesc);

            }
        }

        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if (mv != null) {
            if ((opcode == Opcodes.ATHROW) || (opcode == Opcodes.RETURN) || (opcode >= Opcodes.IRETURN && opcode <= Opcodes.ARETURN)) {
                if (options.contains(MethodBodyInfoType.RETURN_VALUE)) {
                    if (opcode == Opcodes.ATHROW) {
                        StdMethodConsumer.THROWN.accept(mv, version, owner, methodAccess, methodName, methodDesc);
                    }
                    else if (opcode == Opcodes.RETURN) {
                        StdMethodConsumer.RETURN_VOID.accept(mv, version, owner, methodAccess, methodName, methodDesc);
                    }
                    else {
                        StdMethodConsumer.RETURN_VALUE.accept(mv, version, owner, methodAccess, methodName, methodDesc);
                    }
                }

                if (options.contains(MethodBodyInfoType.EXIT)) {
                    StdMethodConsumer.EXIT.accept(mv, version, owner, methodAccess, methodName, methodDesc);
                }
            }
        }

        super.visitInsn(opcode);
    }

}
