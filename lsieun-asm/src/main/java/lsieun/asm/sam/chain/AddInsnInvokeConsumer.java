package lsieun.asm.sam.chain;

import lsieun.asm.sam.consumer.InsnInvokeConsumer;

@FunctionalInterface
public interface AddInsnInvokeConsumer<T> {
    T withInsnInvokeConsumer(InsnInvokeConsumer insnInvokeConsumer);
}
