package lsieun.asm.insn;

import jakarta.annotation.Nonnull;
import lsieun.asm.core.AsmTypeBuddy;
import lsieun.asm.cst.MyAsmConst;
import org.objectweb.asm.*;

import java.lang.reflect.Modifier;

import static lsieun.asm.cst.MyAsmConst.OBJECT_TYPE;
import static lsieun.asm.insn.AsmInsnUtilsForOpcode.*;
import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForCodeFragment {
    public static void printMessage(@Nonnull MethodVisitor mv, String message) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(message == null ? "null" : message);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printParameters(@Nonnull MethodVisitor mv, int methodAccess, @Nonnull String methodDesc) {
        int slotIndex = (methodAccess & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        for (Type t : argumentTypes) {
            printParameter(mv, slotIndex, t);

            int size = t.getSize();
            slotIndex += size;
        }
    }

    public static void printParameter(@Nonnull MethodVisitor mv, int slotIndex, @Nonnull Type t) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
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

    /**
     * <code>
     * <pre>
     * String format = &quot;%-9s    %-32s    %-15s    %-8s    %-8s%n&quot;;
     *
     * Formatter fm = new Formatter();
     * fm.format(format, &quot;Id&quot;, &quot;Name&quot;, &quot;State&quot;, &quot;Priority&quot;, &quot;isDaemon&quot;);
     * Set&lt;Thread&gt; threads = Thread.getAllStackTraces().keySet();
     * for (Thread t : threads) {
     *     fm.format(format,
     *             t.getId(),
     *             t.getName(), t.getState(), t.getPriority(), t.isDaemon());
     * }
     * fm.format(format,
     *         Thread.currentThread().getId(),
     *         Thread.currentThread().getName(),
     *         Thread.currentThread().getState(),
     *         Thread.currentThread().getPriority(),
     *         Thread.currentThread().isDaemon()
     * );
     *
     * System.out.println(fm.toString());
     * </pre>
     * </code>
     */
    public static void printAllThreadsInfo(@Nonnull MethodVisitor mv, int methodAccess, @Nonnull String methodDesc) {
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        int freeSlotIndex = Modifier.isStatic(methodAccess) ? 0 : 1;
        for (Type argumentType : argumentTypes) {
            freeSlotIndex += argumentType.getSize();
        }

        int slotIndexForFormatter = freeSlotIndex++;
        int slotIndexForSet = freeSlotIndex++;
        int slotIndexForIterator = freeSlotIndex++;
        int slotIndexForThread = freeSlotIndex++;

        // Formatter fm = new Formatter();
        mv.visitTypeInsn(NEW, "java/util/Formatter");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/Formatter", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
        mv.visitVarInsn(ASTORE, slotIndexForFormatter);


        // fm.format(format, "Id", "Name", "State", "Priority", "isDaemon");
        mv.visitVarInsn(ALOAD, slotIndexForFormatter);
        mv.visitLdcInsn("%-9s    %-32s    %-15s    %-8s    %-8s%n");
        mv.visitInsn(ICONST_5);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitLdcInsn("Id");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitLdcInsn("Name");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitLdcInsn("State");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitLdcInsn("Priority");
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitLdcInsn("isDaemon");
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Formatter", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/Formatter;", false);
        mv.visitInsn(POP);

        // Set<Thread> threads = Thread.getAllStackTraces().keySet();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "getAllStackTraces", "()Ljava/util/Map;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;", true);
        mv.visitVarInsn(ASTORE, slotIndexForSet);

        // Iterator it = threads.iterator();
        mv.visitVarInsn(ALOAD, slotIndexForSet);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitVarInsn(ASTORE, slotIndexForIterator);


        Label forContinueLabel = new Label();
        Label forStopLabel = new Label();

        // it.hasNext()
        mv.visitLabel(forContinueLabel);
        mv.visitVarInsn(ALOAD, slotIndexForIterator);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

        // Thread t = it.next();
        mv.visitJumpInsn(IFEQ, forStopLabel);
        mv.visitVarInsn(ALOAD, slotIndexForIterator);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Thread");
        mv.visitVarInsn(ASTORE, slotIndexForThread);

        // fm.format(format, t.getId(), t.getName(), t.getState(), t.getPriority(), t.isDaemon());
        mv.visitVarInsn(ALOAD, slotIndexForFormatter);
        mv.visitLdcInsn("%-9s    %-32s    %-15s    %-8s    %-8s%n");
        mv.visitInsn(ICONST_5);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, slotIndexForThread);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ALOAD, slotIndexForThread);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ALOAD, slotIndexForThread);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getState", "()Ljava/lang/Thread$State;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitVarInsn(ALOAD, slotIndexForThread);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getPriority", "()I", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitVarInsn(ALOAD, slotIndexForThread);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "isDaemon", "()Z", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Formatter", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/Formatter;", false);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, forContinueLabel);

        // fm.format(format,
        //                Thread.currentThread().getId(),
        //                Thread.currentThread().getName(),
        //                Thread.currentThread().getState(),
        //                Thread.currentThread().getPriority(),
        //                Thread.currentThread().isDaemon()
        //        );
        mv.visitLabel(forStopLabel);
        mv.visitVarInsn(ALOAD, slotIndexForFormatter);
        mv.visitLdcInsn("%-9s    %-32s    %-15s    %-8s    %-8s%n");
        mv.visitInsn(ICONST_5);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getState", "()Ljava/lang/Thread$State;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getPriority", "()I", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mv.visitInsn(AASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "isDaemon", "()Z", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Formatter", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/Formatter;", false);
        mv.visitInsn(POP);

        // System.out.println(fm.toString());
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, slotIndexForFormatter);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Formatter", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printThreadInfo(@Nonnull MethodVisitor mv) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
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

    public static void printClassLoader(@Nonnull MethodVisitor mv, @Nonnull String owner) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
        mv.visitLdcInsn("ClassLoader: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(Type.getObjectType(owner));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printStackTrace(@Nonnull MethodVisitor mv, @Nonnull String msg) {
        mv.visitTypeInsn(NEW, "java/lang/Exception");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "(Ljava/lang/String;)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V", false);
    }

    public static void printStackTraceSinceJava9(@Nonnull MethodVisitor mv, @Nonnull String owner,
                                                 @Nonnull String methodName, @Nonnull String methodDesc) {

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
                        new Handle(Opcodes.H_INVOKESTATIC, owner, methodName, methodDesc, false),
                        Type.getType(methodDesc)
                }
        );
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackWalker", "forEach", "(Ljava/util/function/Consumer;)V", false);
    }

    public static void printReturnValue(@Nonnull MethodVisitor mv, @Nonnull String owner,
                                        @Nonnull String methodName, @Nonnull String methodDesc) {
        String line = String.format("Method Return: %s.%s:%s", owner, methodName, methodDesc);
        printMessage(mv, line);

        Type methodType = Type.getMethodType(methodDesc);
        Type returnType = methodType.getReturnType();
        printReturnValue(mv, returnType);
    }

    /**
     * @see AsmInsnUtilsForCodeFragment#printValueOnStack(MethodVisitor, Type)
     */
    public static void printReturnValue(@Nonnull MethodVisitor mv, @Nonnull Type returnType) {
        dupValueOnStack(mv, returnType);

        printValueOnStack(mv, returnType);
    }


    /**
     * 注意 printValueOnStack 和 printReturnValue 的区别
     *
     * @see AsmInsnUtilsForCodeFragment#printReturnValue(MethodVisitor, Type)
     */
    public static void printValueOnStack(@Nonnull MethodVisitor mv, @Nonnull Type t) {
        printValueOnStack(mv, t, "    ");
    }

    public static void printValueOnStack(@Nonnull MethodVisitor mv, @Nonnull Type t, @Nonnull String prefix) {
        if (!AsmTypeBuddy.isValidValue(t)) {
            return;
        }

        // convert to String
        // operand stack: obj
        AsmInsnUtilsForValue.convertValueOnStackToString(mv, t);
        // operand stack: str

        if (prefix.isEmpty()) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
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
        String descriptor = AsmInsnUtilsForDesc.getDescForStringBuilderAppend(t);

        // operand stack: System.out, builder, val
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        // operand stack: System.out, builder
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }



    public static void printInvokeMethodInsnParams(@Nonnull MethodVisitor mv, boolean hasReceiverType,
                                                   @Nonnull String methodDesc) {
        // operand stack: [...]
        arrayFromStackByMethodDesc(mv, methodDesc);
        // operand stack: [..., array]

        if (hasReceiverType) {
            // operand stack: [..., receiver, array]
            swap(mv);
            // operand stack: [..., array, receiver]
            dupX1(mv);
            // operand stack: [..., receiver, array, receiver]
            AsmInsnUtilsForValue.convertValueOnStackToTypeName(mv);
            // operand stack: [..., receiver, array, str]
            printValueOnStack(mv, Type.getType(String.class), "    [receiver] ");
            // operand stack: [..., receiver, array]
        }

        // operand stack: [x, array]
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        int count = argumentTypes.length;
        for (int i = 0; i < count; i++) {
            Type argumentType = argumentTypes[i];

            // operand stack: [x, array]
            dup(mv);
            // operand stack: [x, array, array]
            push(mv, i);
            // operand stack: [x, array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [x, array, obj]
            String prefix = String.format("    args[%d] - ", i);
            printValueOnStack(mv, OBJECT_TYPE, prefix);
            // operand stack: [x, array]
        }

        for (int i = 0; i < count; i++) {
            Type argumentType = argumentTypes[i];

            // operand stack: [x, array]
            dup(mv);
            // operand stack: [x, array, array]
            push(mv, i);
            // operand stack: [x, array, array, index]
            arrayLoad(mv, OBJECT_TYPE);
            // operand stack: [x, array, obj]
            unbox(mv, argumentType);
            // operand stack: [x, array, arg]
            swapLeftAndRight(mv, OBJECT_TYPE, argumentType);
            // operand stack: [x, arg, array]
        }

        // pop array
        // operand stack: [..., array]
        pop(mv);
        // operand stack: [...]
    }


}
