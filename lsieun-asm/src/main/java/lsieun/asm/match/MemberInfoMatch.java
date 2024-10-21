package lsieun.asm.match;


import lsieun.core.match.LogicAssistant;
import lsieun.core.match.text.TextMatch;

import java.lang.invoke.MethodHandles;

@FunctionalInterface
public interface MemberInfoMatch {
    boolean test(String currentOwner, int access, String name, String descriptor);


    static MemberInfoMatch byName(TextMatch textMatch) {
        return ((currentOwner, access, name, descriptor) -> textMatch.test(name));
    }

    static LogicAssistant<MemberInfoMatch> logic() {
        return LogicAssistant.of(MethodHandles.lookup(), MemberInfoMatch.class);
    }

    enum Bool implements MemberInfoMatch {
        TRUE {
            @Override
            public boolean test(String currentOwner, int access, String name, String descriptor) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(String currentOwner, int access, String name, String descriptor) {
                return false;
            }
        };
    }

}
