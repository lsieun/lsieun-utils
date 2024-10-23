package lsieun.core.sam;

import lsieun.core.processor.bytes.ByteArrayProcessor;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AddByteArrayProcessor<T> {
    T withByteArrayProcessor(@NotNull ByteArrayProcessor processor);
}
