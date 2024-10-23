package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.InsnInvokeMatch;

@FunctionalInterface
public interface AddInsnMatch<T> {
    T withInsnMatch(InsnInvokeMatch insnInvokeMatch);
}
