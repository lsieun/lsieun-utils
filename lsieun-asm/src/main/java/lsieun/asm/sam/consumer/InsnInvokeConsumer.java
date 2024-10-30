package lsieun.asm.sam.consumer;

import lsieun.annotation.mind.logic.ThinkStep;
import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.core.AsmTypeUtils;
import lsieun.asm.cst.OpcodeConst;
import lsieun.asm.insn.code.AsmInsnUtilsForMethodInvoke;
import lsieun.asm.insn.code.AsmInsnUtilsForPrint;
import lsieun.asm.insn.code.AsmInsnUtilsForStackTrace;
import lsieun.asm.insn.opcode.OpcodeForConst;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static lsieun.asm.insn.opcode.OpcodeForStack.dupValueOnStack;
import static lsieun.asm.insn.opcode.OpcodeForStack.popByMethodDesc;

/**
 * <code>
 * <pre>
 * Path jarPath = Path.of(&quot;...&quot;);
 * String entry = AsmTypeNameUtils.toJarEntryName(&quot;com.abc.Xyz&quot;);
 * String methodName = &quot;test&quot;;
 * String methodDesc = &quot;()V&quot;;
 * MethodInfoMatch methodMatch = MethodInfoMatch.byMethodNameAndDesc(methodName, methodDesc);
 * InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.All.INSTANCE;
 * InsnInvokeConsumer insnInvokeConsumer = InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn();
 *
 * Function&lt;byte[], byte[]&gt; func = bytes -&gt; ClassFileModifyUtils.patchInsnInvoke(
 *     bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer);
 * ByteArrayThreePhase.forZip(jarPath, entry, func);
 * </pre>
 * </code>
 *
 * @see ClassFileModifyUtils#modifyInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch, InsnInvokeConsumer)
 * @see InsnInvokeConsumerGallery
 */
public interface InsnInvokeConsumer {
    void accept(MethodVisitor mv,
                String currentType, String currentMethodName, String currentMethodDesc,
                int opcode, String owner, String name, String descriptor, boolean isInterface);

    static InsnInvokeConsumer push(int val) {
        return ((mv, currentType, currentMethodName, currentMethodDesc,
                 opcode, owner, name, descriptor, isInterface) ->
        {
            OpcodeForConst.push(mv, val);
        });
    }

    enum NoOp implements InsnInvokeConsumer {
        INSTANCE;

        @Override
        public void accept(MethodVisitor mv,
                           String currentType, String currentMethodName, String currentMethodDesc,
                           int opcode, String owner, String name, String descriptor, boolean isInterface) {
        }
    }

    enum Default implements InsnInvokeConsumer {
        INSTANCE;

        @Override
        public void accept(MethodVisitor mv,
                           String currentType, String currentMethodName, String currentMethodDesc,
                           int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (mv != null) {
                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }
    }

    enum Common implements InsnInvokeConsumer {
        POP_FROM_STACK {
            @Override
            public void accept(MethodVisitor mv,
                               String currentType, String currentMethodName, String currentMethodDesc,
                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (mv != null) {
                    if (opcode == Opcodes.INVOKESPECIAL && INIT_METHOD_NAME.equals(name)) {
                        mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                    else {
                        popByMethodDesc(mv, opcode == Opcodes.INVOKESTATIC, descriptor);
                    }
                }
            }
        },

        ;
    }

    enum Print implements InsnInvokeConsumer {
        PRINT_METHOD_INSN {
            @Override
            public void accept(MethodVisitor mv,
                               String currentType, String currentMethodName, String currentMethodDesc,
                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (mv != null) {
                    String msg = String.format("%s::%s:%s ---> %s %s::%s:%s",
                            AsmTypeNameUtils.toClassName(currentType), currentMethodName, currentMethodDesc,
                            OpcodeConst.getOpcodeName(opcode), AsmTypeNameUtils.toClassName(owner), name, descriptor);
                    AsmInsnUtilsForPrint.printMessage(mv, msg);
                }
            }
        },
        PRINT_METHOD_INSN_PARAM {
            @Override
            public void accept(MethodVisitor mv,
                               String currentType, String currentMethodName, String currentMethodDesc,
                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (mv != null) {
                    @ThinkStep("(1) 如果是 invokestatck 指令，说明是 static 方法，不存在 this")
                    boolean isStatic = opcode == Opcodes.INVOKESTATIC;
                    @ThinkStep("(2) 如果方法名称是 <init>，说明是 constructor 方法，此时的 receiver type 是 UNINITIALIZED_THIS")
                    boolean isInstanceInit = INIT_METHOD_NAME.equals(name);
                    @ThinkStep("(3) 排除这两种情况")
                    boolean hasReceiverType = !(isStatic || isInstanceInit);
                    AsmInsnUtilsForMethodInvoke.printInvokeMethodInsnParams(mv, hasReceiverType, descriptor, 4);
                }
            }
        },
        DUP_AND_PRINT_VALUE_ON_STACK {
            @Override
            public void accept(MethodVisitor mv,
                               String currentType, String currentMethodName, String currentMethodDesc,
                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (mv != null) {
                    // print return value
                    Type methodType = Type.getMethodType(descriptor);
                    Type returnType = methodType.getReturnType();

                    String msg = String.format("%s::%s:%s <--- %s %s::%s:%s [RETURN] ",
                            AsmTypeNameUtils.toClassName(currentType), currentMethodName, currentMethodDesc,
                            OpcodeConst.getOpcodeName(opcode), AsmTypeNameUtils.toClassName(owner), name, descriptor);
                    if (AsmTypeUtils.hasValidValue(returnType)) {
                        dupValueOnStack(mv, returnType);
                        AsmInsnUtilsForPrint.printValueOnStackWithPrefix(mv, returnType, msg);
                    }
                    else {
                        AsmInsnUtilsForPrint.printMessage(mv, msg + "void");
                    }
                }
            }
        },
        PRINT_STACK_TRACE {
            @Override
            public void accept(MethodVisitor mv,
                               String currentType, String currentMethodName, String currentMethodDesc,
                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (mv != null) {
                    String msg = String.format("METHOD %s %s:%s",
                            currentType,
                            currentMethodName,
                            currentMethodDesc);
                    AsmInsnUtilsForStackTrace.printStackTrace(mv, msg);
                }

            }
        },
//        PRINT_STACK_TRACE_SINCE9 {
//            @Override
//            public void accept(MethodVisitor mv,
//                               String currentType, String currentMethodName, String currentMethodDesc,
//                               int opcode, String owner, String name, String descriptor, boolean isInterface) {
//                if (mv != null) {
//                    String msg = String.format("METHOD %s %s:%s",
//                            currentType,
//                            currentMethodName,
//                            currentMethodDesc);
//                    // 存在问题：需要先添加 printStackFrame 静态方法
//                    CodeSegmentUtils.printStackTraceSinceJava9(mv, currentType);
//                }
//            }
//        },
        ;
    }

    class ThreePhase implements InsnInvokeConsumer {
        private final List<InsnInvokeConsumer> preInvokeConsumers = new ArrayList<>();
        private final List<InsnInvokeConsumer> onInvokeConsumers = new ArrayList<>();
        private final List<InsnInvokeConsumer> postInvokeConsumers = new ArrayList<>();

        public static AddPreInvokeConsumer builder() {
            return preInvokeConsumerArray ->
                    onInvokeConsumerArray ->
                            postInvokeConsumerArray ->
                            {
                                ThreePhase instance = new ThreePhase();
                                instance.preInvokeConsumers.addAll(Arrays.asList(preInvokeConsumerArray));
                                instance.onInvokeConsumers.addAll(Arrays.asList(onInvokeConsumerArray));
                                instance.postInvokeConsumers.addAll(Arrays.asList(postInvokeConsumerArray));
                                return instance;
                            };
        }

        @Override
        public void accept(MethodVisitor mv,
                           String currentType, String currentMethodName, String currentMethodDesc,
                           int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (mv == null) {
                return;
            }

            for (InsnInvokeConsumer consumer : preInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc,
                        opcode, owner, name, descriptor, isInterface);
            }

            for (InsnInvokeConsumer consumer : onInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc,
                        opcode, owner, name, descriptor, isInterface);
            }

            for (InsnInvokeConsumer consumer : postInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc,
                        opcode, owner, name, descriptor, isInterface);
            }
        }

        @FunctionalInterface
        public interface AddPreInvokeConsumer {
            AddOnInvokeConsumer withPreInvokeConsumer(InsnInvokeConsumer... preInvokeConsumerArray);
        }

        @FunctionalInterface
        public interface AddOnInvokeConsumer {
            AddPostInvokeConsumer withOnInvokeConsumer(InsnInvokeConsumer... onInvokeConsumerArray);
        }

        @FunctionalInterface
        public interface AddPostInvokeConsumer {
            ThreePhase withPostInvokeConsumer(InsnInvokeConsumer... postInvokeConsumerArray);
        }
    }
}
