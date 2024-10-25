package lsieun.core.sam.chain;

import lsieun.core.processor.bytes.ByteArrayProcessor;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AddByteArrayProcessor<T> {
    T withByteArrayProcessor(@NotNull ByteArrayProcessor processor);
}
