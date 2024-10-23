package lsieun.core.sam;

import java.nio.file.Path;

@FunctionalInterface
public interface AddDirFromWithMaxDepth<T> {
    T withFromDir(Path dirPath, int maxDepth);
}
