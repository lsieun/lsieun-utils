package lsieun.utils.asm.match;


import lsieun.utils.match.text.TextMatch;

@FunctionalInterface
public interface MemberInfoMatch {
    boolean test(String currentOwner, int access, String name, String descriptor);

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

    static MemberInfoMatch byName(TextMatch textMatch) {
        return ((currentOwner, access, name, descriptor) -> textMatch.test(name));
    }


}
