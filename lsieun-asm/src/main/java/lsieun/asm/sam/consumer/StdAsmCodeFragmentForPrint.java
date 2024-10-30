package lsieun.asm.sam.consumer;

import lsieun.asm.core.AsmTypeUtils;
import lsieun.asm.format.MethodInfoFormat;
import lsieun.asm.insn.code.AsmInsnUtilsForMethodParameter;
import lsieun.asm.insn.code.AsmInsnUtilsForPrint;
import lsieun.asm.insn.code.AsmInsnUtilsForStackTrace;
import lsieun.asm.insn.code.AsmInsnUtilsForThread;
import lsieun.asm.tag.AsmCodeTag;
import lsieun.base.thread.stacktrace.StackTraceFormat;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.EnumSet;
import java.util.Set;

import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_DESC;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.PRINT_STACK_FRAME_METHOD_NAME;
import static lsieun.asm.insn.opcode.OpcodeForStack.dupValueOnStack;

/**
 * @see AsmCodeFragment
 */
public enum StdAsmCodeFragmentForPrint implements AsmCodeFragment {
    ENTER {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodEnter(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    },
    EXIT {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExit(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    },
    PARAMETER_VALUES {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            AsmInsnUtilsForMethodParameter.printParameters(mv, methodAccess, methodDesc);
        }
    },
    STACK_TRACE {
        @Override
        public Set<AsmCodeTag> tags() {
            return EnumSet.of(AsmCodeTag.STACK_TRACE);
        }

        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String title = StackTraceFormat.formatTitle(currentType, methodName, methodDesc);
//            AsmInsnUtilsForCodeSegment.printMessage(mv, title);
            AsmInsnUtilsForThread.printCurrentThreadInfo(mv);
            AsmInsnUtilsForStackTrace.printStackTraceSinceJava9(mv, currentType,
                    PRINT_STACK_FRAME_METHOD_NAME,
                    PRINT_STACK_FRAME_METHOD_DESC);
        }
    },
    EXIT_RETURN_SIMPLE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitReturn(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    },
    EXIT_RETURN_VALUE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitReturn(currentType, methodAccess, methodName, methodDesc);
            Type methodType = Type.getMethodType(methodDesc);
            Type returnType = methodType.getReturnType();
            if (AsmTypeUtils.hasValidValue(returnType)) {
                dupValueOnStack(mv, returnType);
                AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, returnType, line + " - [ReturnValue] ");
            }
            else {
                AsmInsnUtilsForPrint.printMessage(mv, line);
            }

        }
    },
    EXIT_THROWN_SIMPLE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitThrown(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForPrint.printMessage(mv, line);
        }
    };

}
