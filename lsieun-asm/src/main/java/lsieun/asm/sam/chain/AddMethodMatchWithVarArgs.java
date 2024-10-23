package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.MethodInfoMatch;

@FunctionalInterface
public interface AddMethodMatchWithVarArgs<T> {
    T withMethodMatch(MethodInfoMatch... methodMatches);
}
