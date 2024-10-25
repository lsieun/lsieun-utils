package lsieun.core.sam.chain;

import lsieun.core.sam.match.path.FilePathMatch;

@FunctionalInterface
public interface AddFilePathMatchWithVarArgs<T> {
    T withFilePathMatch(FilePathMatch... filePathMatches);
}
