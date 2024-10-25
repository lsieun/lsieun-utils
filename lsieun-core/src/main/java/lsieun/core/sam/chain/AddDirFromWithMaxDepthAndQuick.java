package lsieun.core.sam.chain;

import java.nio.file.Path;

@FunctionalInterface
public interface AddDirFromWithMaxDepthAndQuick<T> {
    T withFromDir(Path dirPath, int maxDepth, boolean quick);
}
