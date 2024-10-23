package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.MethodInfoMatch;

@FunctionalInterface
public interface AddMethodMatch<T> {
    T withMethodMatch(MethodInfoMatch methodMatch);
}
