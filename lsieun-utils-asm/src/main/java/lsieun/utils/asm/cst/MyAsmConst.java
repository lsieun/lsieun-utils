package lsieun.utils.asm.cst;

import org.objectweb.asm.Opcodes;

public final class MyAsmConst {
    public static final String COLON = ":";
    public static final String PRINT_STACK_FRAME_METHOD_NAME = "printStackFrame";
    public static final String PRINT_STACK_FRAME_METHOD_DESC = "(Ljava/lang/StackWalker$StackFrame;)V";
    public static int ASM_API_VERSION;

    static {
        ASM_API_VERSION = Opcodes.ASM9;
    }
}
