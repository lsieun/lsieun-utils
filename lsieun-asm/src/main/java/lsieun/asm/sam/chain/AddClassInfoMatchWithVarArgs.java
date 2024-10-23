package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.ClassInfoMatch;

@FunctionalInterface
public interface AddClassInfoMatchWithVarArgs<T> {
    T withClassMatch(ClassInfoMatch... classMatches);
}
