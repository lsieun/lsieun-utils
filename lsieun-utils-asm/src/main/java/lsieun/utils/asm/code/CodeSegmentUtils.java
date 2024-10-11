package lsieun.utils.asm.code;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class CodeSegmentUtils {
    public static void printMessage(MethodVisitor mv, String message) {
        if (message != null) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(message);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

    public static void printParameters(MethodVisitor mv, int methodAccess, String methodDesc) {
        if (mv == null) {
            return;
        }

        int slotIndex = (methodAccess & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        for (Type t : argumentTypes) {
            printParameter(mv, slotIndex, t);

            int size = t.getSize();
            slotIndex += size;
        }
    }

    public static void printParameter(MethodVisitor mv, int slotIndex, Type t) {
        if (mv == null || t == null) {
            return;
        }

        // load variable
        int opcode = t.getOpcode(Opcodes.ILOAD);
        mv.visitVarInsn(opcode, slotIndex);

        // prefix
        String prefix = String.format("    slots[%02d]", slotIndex);

        // print
        printValueOnStack(mv, t, prefix);
    }

    public static void printParameterV1(MethodVisitor mv, int slotIndex, Type t) {
        if (mv == null || t == null) {
            return;
        }

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("    ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        if (slotIndex >= 0 && slotIndex <= 5) {
            mv.visitInsn(ICONST_0 + slotIndex);
        }
        else {
            mv.visitIntInsn(BIPUSH, slotIndex);
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(": ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        // load variable
        int opcode = t.getOpcode(Opcodes.ILOAD);
        mv.visitVarInsn(opcode, slotIndex);

        // Arrays.toString(String[])
        String paramDesc = t.getDescriptor();
        if (paramDesc.startsWith("[") && paramDesc.endsWith(";")) {
//        if ("[Ljava/lang/String;".equals(paramDesc)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        }

        // descriptor
        int sort = t.getSort();
        String appendDesc;
        if (sort == Type.SHORT) {
            appendDesc = "(I)Ljava/lang/StringBuilder;";
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            appendDesc = "(" + paramDesc + ")Ljava/lang/StringBuilder;";
        }
        else {
            appendDesc = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", appendDesc, false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printThreadInfo(MethodVisitor mv) {
        if (mv == null) {
            return;
        }

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Thread: %s@%s(%s)");
        mv.visitInsn(ICONST_3);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "isDaemon", "()Z", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printThreadInfoV1(MethodVisitor mv) {
        if (mv == null) {
            return;
        }

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("Thread Id: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("@");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("(");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "isDaemon", "()Z", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(")");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printClassLoader(MethodVisitor mv, String owner) {
        if (mv == null) {
            return;
        }

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("ClassLoader: %s");
        mv.visitInsn(ICONST_1);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitLdcInsn(Type.getObjectType(owner));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printClassLoaderV1(MethodVisitor mv, String owner) {
        if (mv == null) {
            return;
        }

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("ClassLoader: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(Type.getObjectType(owner));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printStackTrace(MethodVisitor mv, String msg) {
        if (mv == null || msg == null) {
            return;
        }

        mv.visitTypeInsn(NEW, "java/lang/Exception");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V", false);
    }

    public static void printStackTraceSinceJava9(MethodVisitor mv, String owner) {
        if (mv == null || owner == null) {
            return;
        }

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/StackWalker", "getInstance", "()Ljava/lang/StackWalker;", false);
        mv.visitInvokeDynamicInsn("accept", "()Ljava/util/function/Consumer;",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        "java/lang/invoke/LambdaMetafactory", "metafactory",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        false
                ),
                new Object[]{
                        Type.getType("(Ljava/lang/Object;)V"),
                        new Handle(Opcodes.H_INVOKESTATIC, owner, "printStackFrame", "(Ljava/lang/StackWalker$StackFrame;)V", false),
                        Type.getType("(Ljava/lang/StackWalker$StackFrame;)V")
                }
        );
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackWalker", "forEach", "(Ljava/util/function/Consumer;)V", false);
    }

    public static void printReturnValue(MethodVisitor mv, String owner, String methodName, String methodDesc) {
        if (mv == null) {
            return;
        }

        String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
        printMessage(mv, line);

        Type methodType = Type.getMethodType(methodDesc);
        Type returnType = methodType.getReturnType();
        printReturnValue(mv, returnType);
    }

    /**
     * @see CodeSegmentUtils#printValueOnStack(MethodVisitor, Type)
     */
    public static void printReturnValue(MethodVisitor mv, Type returnType) {
        if (mv == null || returnType == null) {
            return;
        }

        dupValueOnStack(mv, returnType);

        printValueOnStack(mv, returnType);
    }

    public static void dupValueOnStack(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? DUP : DUP2;
            mv.visitInsn(opcode);
        }
    }

    /**
     * 注意 printValueOnStack 和 printReturnValue 的区别
     *
     * @see CodeSegmentUtils#printReturnValue(MethodVisitor, Type)
     */
    public static void printValueOnStack(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        printValueOnStack(mv, t, "    ");
    }

    public static void printValueOnStack(MethodVisitor mv, Type t, String prefix) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort < Type.BOOLEAN || sort > Type.OBJECT) {
            return;
        }

        // convert to String
        // operand stack: obj
        convertValueOnStackToString(mv, t);
        // operand stack: str

        if ("".equals(prefix)) {
            // operand stack: str
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            // operand stack: str, System.out
            mv.visitInsn(SWAP);
            // operand stack: System.out, str
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            return;
        }

        // operand stack: str
        // StringBuilder builder = new StringBuilder();
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        // operand stack: str, builder

        // operand stack: str, builder
        mv.visitLdcInsn(prefix);
        // operand stack: str, builder, prefix
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        // operand stack: str, builder


        // swap
        // operand stack: str, builder
        mv.visitInsn(SWAP);
        // operand stack: builder, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        // operand stack: builder
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        // operand stack: str

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // operand stack: str, System.out

        // swap
        mv.visitInsn(SWAP);

        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printValueOnStackV1(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int size = t.getSize();
        if (size == 0) {
            // size = 0, t = VOID_TYPE
            return;
        }

        // operand stack: val
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // operand stack: val, System.out

        // swap
        if (size == 1) {
            mv.visitInsn(SWAP);
        }
        else if (size == 2) {
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
        }
        else {
            assert false : "should not be here";
        }
        // operand stack: System.out, val

        // StringBuilder builder = new StringBuilder();
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        // operand stack: System.out, val, builder

        // builder.append("    ");
        mv.visitLdcInsn("    ");
        // operand stack: System.out, val, builder, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        // operand stack: System.out, val, builder

        // swap
        if (size == 1) {
            mv.visitInsn(SWAP);
        }
        else {
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
        }
        // operand stack: System.out, builder, val

        // Arrays.toString(String[])
        String paramDesc = t.getDescriptor();
        if (paramDesc.startsWith("[") && paramDesc.endsWith(";")) {
//        if ("[Ljava/lang/String;".equals(paramDesc)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        }

        // builder.append(val);
        int sort = t.getSort();
        String descriptor;
        if (sort == Type.SHORT) {
            descriptor = "(I)Ljava/lang/StringBuilder;";
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            descriptor = "(" + t.getDescriptor() + ")Ljava/lang/StringBuilder;";
        }
        else {
            descriptor = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
        }

        // operand stack: System.out, builder, val
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        // operand stack: System.out, builder
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    /**
     * <pre>
     *     public static valueOf:(Ljava/lang/Object;)Ljava/lang/String;
     *     public static valueOf:()Ljava/lang/String;
     * </pre>
     */
    public static void convertValueOnStackToString(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort == Type.VOID) {
            return;
        }

        // String - do nothing, just return
        if (Type.getType(String.class).equals(t)) {
            return;
        }

        // primitive
        if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            convertPrimitiveValueOnStackToString(mv, t);
        }
        else if (sort == Type.ARRAY) {
            convertArrayValueOnStackToString(mv, t);
        }
        else {
            convertObjectValueOnStackToString(mv, t);
        }
    }

    /**
     * <pre>
     * valueOf:(Z)Ljava/lang/String;
     * valueOf:(C)Ljava/lang/String;
     * valueOf:(I)Ljava/lang/String;
     * valueOf:(J)Ljava/lang/String;
     * valueOf:(F)Ljava/lang/String;
     * valueOf:(D)Ljava/lang/String;
     * </pre>
     */
    public static void convertPrimitiveValueOnStackToString(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            String descriptor = (sort >= Type.BYTE && sort <= Type.INT) ?
                    Type.INT_TYPE.getDescriptor() :
                    t.getDescriptor();
            String methodDesc = String.format("(%s)Ljava/lang/String;", descriptor);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", methodDesc, false);
        }
    }

    public static void convertArrayValueOnStackToString(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort == Type.ARRAY) {
            int dimensions = t.getDimensions();
            Type elementType = t.getElementType();
            int elementTypeSort = elementType.getSort();
            if (dimensions == 1) {
                if (elementTypeSort == Type.CHAR) {
                    // String.valueOf(char[])
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                            "([C)Ljava/lang/String;", false);
                }
                else if (elementTypeSort >= Type.BOOLEAN && elementTypeSort <= Type.DOUBLE) {
                    String descriptor = t.getDescriptor();
                    String methodDesc = String.format("(%s)Ljava/lang/String;", descriptor);
                    mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", methodDesc, false);
                }
                else {
                    mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString",
                            "([Ljava/lang/Object;)Ljava/lang/String;", false);
                }
            }
            else {
                convertObjectValueOnStackToString(mv, t);
            }
        }
    }

    /**
     * <pre>
     * valueOf:(Ljava/lang/Object;)Ljava/lang/String;
     * </pre>
     */
    public static void convertObjectValueOnStackToString(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort == Type.VOID) {
            return;
        }

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(Ljava/lang/Object;)Ljava/lang/String;", false);
    }

    public static void pop(MethodVisitor mv, Type t) {
        if (mv == null || t == null) {
            return;
        }

        int sort = t.getSort();
        if (sort >= Type.BOOLEAN && sort <= Type.OBJECT) {
            int size = t.getSize();
            int opcode = size == 1 ? POP : POP2;
            mv.visitInsn(opcode);
        }
    }

    public static void popByMethodDesc(MethodVisitor mv, boolean isStatic, String methodDesc) {
        if (mv == null) {
            return;
        }

        // pop arg types
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        int argCount = argumentTypes.length;
        for (int i = argCount - 1; i >= 0; i--) {
            Type argType = argumentTypes[i];
            pop(mv, argType);
        }

        // pop this
        if (!isStatic) {
            pop(mv, Type.getType(Object.class));
        }
    }
}
