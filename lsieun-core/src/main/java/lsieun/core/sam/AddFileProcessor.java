package lsieun.core.sam;

import lsieun.core.processor.vfs.FileProcessor;

@FunctionalInterface
public interface AddFileProcessor {
    void withFileProcessor(FileProcessor fileProcessor);
}
