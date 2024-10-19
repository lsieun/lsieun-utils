package lsieun.asm.consumer;

import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.insn.AsmInsnUtilsForCodeFragment;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public enum StdMethodConsumer implements MethodConsumer, Opcodes {
    ENTER {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("%s::%s:%s %s", owner, methodName, methodDesc, ">".repeat(81));
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    },
    EXIT {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("%s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(81));
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    },
    PARAMETER_VALUES {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForCodeFragment.printParameters(mv, methodAccess, methodDesc);
        }
    },
    RETURN_VOID {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
            String message = "    return void";
            AsmInsnUtilsForCodeFragment.printMessage(mv, message);
        }
    },
    RETURN_VALUE {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForCodeFragment.printReturnValue(mv, owner, methodName, methodDesc);
        }
    },
    THROWN {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("Method throws Exception: %s.%s:%s", owner, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
            String message = "    abnormal return";
            AsmInsnUtilsForCodeFragment.printMessage(mv, message);
        }
    },
    CLASSLOADER {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForCodeFragment.printClassLoader(mv, owner);
        }
    },
    STACK_TRACE {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForCodeFragment.printMessage(mv, "Stack Trace:");
            if (version < (44 + 9)) {
                String msg = String.format("METHOD %s %s:%s",
                        owner,
                        methodName,
                        methodDesc);
                AsmInsnUtilsForCodeFragment.printStackTrace(mv, msg);
            }
            else {
                AsmInsnUtilsForCodeFragment.printStackTraceSinceJava9(mv, owner,
                        MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME,
                        MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC);
            }
        }
    },
    THREAD_INFO {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForCodeFragment.printThreadInfo(mv);
        }
    };
}
