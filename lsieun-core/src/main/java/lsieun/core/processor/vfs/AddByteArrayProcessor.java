package lsieun.core.processor.vfs;

import lsieun.core.processor.bytes.ByteArrayProcessor;

@FunctionalInterface
public interface AddByteArrayProcessor {
    void withByteArrayProcessor(ByteArrayProcessor processor);
}
