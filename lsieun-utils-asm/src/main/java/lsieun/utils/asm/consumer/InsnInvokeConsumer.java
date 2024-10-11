package lsieun.utils.asm.consumer;

import lsieun.utils.asm.code.CodeSegmentUtils;
import lsieun.utils.asm.common.ClassFileModifyUtils;
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
 * @see ClassFileModifyUtils#patchInsnInvoke(byte[], MethodInfoMatch, InsnInvokeMatch, InsnInvokeConsumer)
 */
public interface InsnInvokeConsumer {
    void accept(MethodVisitor mv,
                String currentType, String currentMethodName, String currentMethodDesc,
                int opcode, String owner, String name, String descriptor, boolean isInterface);

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
                    if (opcode == Opcodes.INVOKESPECIAL && "<init>".equals(name)) {
                        mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                    else {
                        CodeSegmentUtils.popByMethodDesc(mv, opcode == Opcodes.INVOKESTATIC, descriptor);
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
                    CodeSegmentUtils.printMessage(mv, msg);
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
                    CodeSegmentUtils.dupValueOnStack(mv, returnType);
                    String msg = String.format("%s::%s:%s ---> %s %s::%s:%s [RETURN] ",
                            currentType, currentMethodName, currentMethodDesc,
                            OpcodeConst.getOpcodeName(opcode), owner, name, descriptor);
                    CodeSegmentUtils.printValueOnStack(mv, returnType, msg);
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
                    CodeSegmentUtils.printStackTrace(mv, msg);
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
        public void accept(MethodVisitor mv, String currentType, String currentMethodName, String currentMethodDesc, int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (mv == null) {
                return;
            }

            for (InsnInvokeConsumer consumer : preInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc, opcode, owner, name, descriptor, isInterface);
            }

            for (InsnInvokeConsumer consumer : onInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc, opcode, owner, name, descriptor, isInterface);
            }

            for (InsnInvokeConsumer consumer : postInvokeConsumers) {
                consumer.accept(mv, currentType, currentMethodName, currentMethodDesc, opcode, owner, name, descriptor, isInterface);
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
