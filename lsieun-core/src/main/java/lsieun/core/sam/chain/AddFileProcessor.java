package lsieun.core.sam.chain;

import lsieun.core.processor.vfs.FileProcessor;

@FunctionalInterface
public interface AddFileProcessor {
    void withFileProcessor(FileProcessor fileProcessor);
}
