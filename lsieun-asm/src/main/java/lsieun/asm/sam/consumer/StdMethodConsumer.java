package lsieun.asm.sam.consumer;

import lsieun.asm.insn.code.*;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_DESC;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_NAME;

public enum StdMethodConsumer implements MethodConsumer, Opcodes {
    ENTER {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("%s::%s:%s %s", owner, methodName, methodDesc, ">".repeat(81));
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    },
    EXIT {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("%s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(81));
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    },
    PARAMETER_VALUES {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForMethodParameter.printParameters(mv, methodAccess, methodDesc);
        }
    },
    RETURN_VOID {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
            String message = "    return void";
            AsmInsnUtilsForPrint.printMessage(mv, message);
        }
    },
    RETURN_VALUE {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForMethodReturn.printReturnValue(mv, owner, methodName, methodDesc);
        }
    },
    THROWN {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            String line = String.format("Method throws Exception: %s.%s:%s", owner, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
            String message = "    abnormal return";
            AsmInsnUtilsForPrint.printMessage(mv, message);
        }
    },
    CLASSLOADER {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForClassLoader.printClassLoader(mv, owner);
        }
    },
    STACK_TRACE {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForPrint.printMessage(mv, "Stack Trace:");
            if (version < (44 + 9)) {
                String msg = String.format("METHOD %s %s:%s",
                        owner,
                        methodName,
                        methodDesc);
                AsmInsnUtilsForStackTrace.printStackTrace(mv, msg);
            }
            else {
                AsmInsnUtilsForStackTrace.printStackTraceSinceJava9(mv, owner,
                        PRINT_STACK_FRAME_METHOD_NAME,
                        PRINT_STACK_FRAME_METHOD_DESC);
            }
        }
    },
    THREAD_INFO {
        @Override
        public void accept(MethodVisitor mv, int version, String owner, int methodAccess, String methodName, String methodDesc) {
            AsmInsnUtilsForThread.printCurrentThreadInfo(mv);
        }
    };
}
