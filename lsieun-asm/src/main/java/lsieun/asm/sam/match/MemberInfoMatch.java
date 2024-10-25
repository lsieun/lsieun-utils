package lsieun.asm.sam.match;


import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.text.TextMatch;

import java.lang.invoke.MethodHandles;

@FunctionalInterface
public interface MemberInfoMatch {
    boolean test(String currentOwner, int access, String name, String descriptor);

    LogicAssistant<MemberInfoMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), MemberInfoMatch.class);

    static MemberInfoMatch byName(TextMatch textMatch) {
        return ((currentOwner, access, name, descriptor) -> textMatch.test(name));
    }
}
