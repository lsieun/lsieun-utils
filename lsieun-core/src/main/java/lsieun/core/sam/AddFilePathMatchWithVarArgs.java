package lsieun.core.sam;

import lsieun.core.match.path.FilePathMatch;

@FunctionalInterface
public interface AddFilePathMatchWithVarArgs<T> {
    T withFilePathMatch(FilePathMatch... filePathMatches);
}
