package lsieun.core.sam;

import lsieun.core.match.path.FilePathMatch;

@FunctionalInterface
public interface AddFilePathMatch<T> {
    T withFilePathMatch(int maxDepth, FilePathMatch filePathMatch);
}
