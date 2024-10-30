package lsieun.asm.insn.code;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;


import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForThread {
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
    public static void printAllThreadsInfo(@NotNull MethodVisitor mv, int methodAccess, @NotNull String methodDesc) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/Formatter", INIT_METHOD_NAME, "()V", false);
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

    public static void printCurrentThreadInfo(@NotNull MethodVisitor mv) {
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

    public static void printThreadInfoV1(@NotNull MethodVisitor mv) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", INIT_METHOD_NAME, "()V", false);
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
}
