package lsieun.utils.asm.code;

import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.format.MethodInfoFormat;
import lsieun.utils.asm.insn.AsmInsnUtilsForCodeFragment;
import lsieun.utils.core.thread.stacktrace.StackTraceFormat;
import org.objectweb.asm.MethodVisitor;

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
    EXIT_THROWN_SIMPLE {
        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            String line = MethodInfoFormat.methodExitThrown(currentType, methodAccess, methodName, methodDesc);
            AsmInsnUtilsForCodeFragment.printMessage(mv, line);
        }
    };

    @Override
    public AsmCodeOptionForWrite getWriteType() {
        return AsmCodeOptionForWrite.COMPUTE_MAX;
    }
}
