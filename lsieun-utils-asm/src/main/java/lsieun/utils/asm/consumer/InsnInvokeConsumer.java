package lsieun.utils.asm.consumer;

import lsieun.utils.annotation.mind.logic.ThinkStep;
import lsieun.utils.asm.common.transformation.ClassFileModifyUtils;
import lsieun.utils.asm.cst.MyAsmConst;
import lsieun.utils.asm.insn.AsmInsnUtilsForCodeFragment;
import lsieun.utils.asm.insn.AsmInsnUtilsForOpcode;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.utils.OpcodeConst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            AsmInsnUtilsForOpcode.push(mv, val);
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
                    if (opcode == Opcodes.INVOKESPECIAL && MyAsmConst.CONSTRUCTOR_INTERNAL_NAME.equals(name)) {
                        mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                    else {
                        AsmInsnUtilsForOpcode.popByMethodDesc(mv, opcode == Opcodes.INVOKESTATIC, descriptor);
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
                            currentType, currentMethodName, currentMethodDesc,
                            OpcodeConst.getOpcodeName(opcode), owner, name, descriptor);
                    AsmInsnUtilsForCodeFragment.printMessage(mv, msg);
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
                    boolean isInstanceInit = MyAsmConst.CONSTRUCTOR_INTERNAL_NAME.equals(name);
                    @ThinkStep("(3) 排除这两种情况")
                    boolean hasReceiverType = !(isStatic || isInstanceInit);
                    AsmInsnUtilsForCodeFragment.printInvokeMethodInsnParams(mv, hasReceiverType, descriptor);
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
                    AsmInsnUtilsForOpcode.dupValueOnStack(mv, returnType);
                    String msg = String.format("%s::%s:%s <--- %s %s::%s:%s [RETURN] ",
                            currentType, currentMethodName, currentMethodDesc,
                            OpcodeConst.getOpcodeName(opcode), owner, name, descriptor);
                    AsmInsnUtilsForCodeFragment.printValueOnStack(mv, returnType, msg);
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
                    AsmInsnUtilsForCodeFragment.printStackTrace(mv, msg);
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
