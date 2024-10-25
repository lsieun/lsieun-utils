package lsieun.core.sam.chain;

import lsieun.core.sam.match.path.FilePathMatch;

@FunctionalInterface
public interface AddFilePathMatch<T> {
    T withFilePathMatch(int maxDepth, FilePathMatch filePathMatch);
}
