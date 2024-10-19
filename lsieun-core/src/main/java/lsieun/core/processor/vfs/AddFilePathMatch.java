package lsieun.core.processor.vfs;

import lsieun.core.match.path.FilePathMatch;

public interface AddFilePathMatch<T> {
    T withFilePathMatch(int maxDepth, FilePathMatch filePathMatch);
}
