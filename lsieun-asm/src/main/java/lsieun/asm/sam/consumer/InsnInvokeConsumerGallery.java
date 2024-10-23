package lsieun.asm.sam.consumer;

public interface InsnInvokeConsumerGallery {
    static InsnInvokeConsumer printInvokeMethodInsn() {
        return InsnInvokeConsumer.ThreePhase.builder()
                .withPreInvokeConsumer(InsnInvokeConsumer.Print.PRINT_METHOD_INSN)
                .withOnInvokeConsumer(InsnInvokeConsumer.Default.INSTANCE)
                .withPostInvokeConsumer();
    }

    static InsnInvokeConsumer printInvokeMethodInsnAndReturn() {
        return InsnInvokeConsumer.ThreePhase.builder()
                .withPreInvokeConsumer(InsnInvokeConsumer.Print.PRINT_METHOD_INSN)
                .withOnInvokeConsumer(InsnInvokeConsumer.Default.INSTANCE)
                .withPostInvokeConsumer(InsnInvokeConsumer.Print.DUP_AND_PRINT_VALUE_ON_STACK);
    }
    static InsnInvokeConsumer printInvokeMethodInsnParamsAndReturn() {
        return InsnInvokeConsumer.ThreePhase.builder()
                .withPreInvokeConsumer(
                        InsnInvokeConsumer.Print.PRINT_METHOD_INSN,
                        InsnInvokeConsumer.Print.PRINT_METHOD_INSN_PARAM
                )
                .withOnInvokeConsumer(InsnInvokeConsumer.Default.INSTANCE)
                .withPostInvokeConsumer(InsnInvokeConsumer.Print.DUP_AND_PRINT_VALUE_ON_STACK);
    }
}
