package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.FieldInfoMatch;

@FunctionalInterface
public interface AddFieldMatch<T> {
    T withFieldMatch(FieldInfoMatch fieldMatch);
}
