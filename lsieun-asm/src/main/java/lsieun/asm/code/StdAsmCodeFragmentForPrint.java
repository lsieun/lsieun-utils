package lsieun.asm.code;

import lsieun.asm.core.AsmTypeBuddy;
import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.format.MethodInfoFormat;
import lsieun.asm.insn.AsmInsnUtilsForCodeFragment;
import lsieun.asm.insn.AsmInsnUtilsForOpcode;
import lsieun.asm.tag.AsmCodeTag;
import lsieun.base.thread.stacktrace.StackTraceFormat;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.EnumSet;
import java.util.Set;

public enum StdAsmCodeFragmentForPrint implements AsmCodeFragment {
    ENTER {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodEnter(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    },
    EXIT {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExit(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    },
    PARAMETER_VALUES {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            AsmInsnUtilsForCodeFragment.printParameters(mv, methodAccess, methodDesc);
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
            AsmInsnUtilsForCodeFragment.printThreadInfo(mv);
            AsmInsnUtilsForCodeFragment.printStackTraceSinceJava9(mv, currentType,
                    MyAsmConst.PRINT_STACK_FRAME_METHOD_NAME,
                    MyAsmConst.PRINT_STACK_FRAME_METHOD_DESC);
        }
    },
    EXIT_RETURN_SIMPLE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitReturn(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
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
            if (AsmTypeBuddy.isValidValue(returnType)) {
                AsmInsnUtilsForOpcode.dupValueOnStack(mv, returnType);
                AsmInsnUtilsForCodeFragment.printValueOnStack(mv, returnType, line + " - [ReturnValue] ");
            }
            else {
                AsmInsnUtilsForCodeFragment.printMessage(mv, line);
            }

        }
    },
    EXIT_THROWN_SIMPLE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitThrown(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    };

}
