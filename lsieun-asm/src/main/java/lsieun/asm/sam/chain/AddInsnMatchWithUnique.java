package lsieun.asm.sam.chain;

import lsieun.asm.sam.match.InsnInvokeMatch;

@FunctionalInterface
public interface AddInsnMatchWithUnique<T> {
    T withInsnMatch(InsnInvokeMatch insnInvokeMatch, boolean deduplicate);
}
