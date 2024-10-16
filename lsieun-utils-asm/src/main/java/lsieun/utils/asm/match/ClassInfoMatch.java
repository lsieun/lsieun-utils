package lsieun.utils.asm.match;

import lsieun.utils.annotation.type.asm.AsmMatchGeneration;

@AsmMatchGeneration
public interface ClassInfoMatch {
    boolean test(int version, int access, String name, String signature, String superName, String[] interfaces);

//    default ClassInfoMatch negate() {
//        return ((version, access, name, signature, superName, interfaces) ->
//                !test(version, access, name, signature, superName, interfaces));
//    }

    enum All implements ClassInfoMatch {
        INSTANCE;

        @Override
        public boolean test(int version, int access, String name, String signature, String superName, String[] interfaces) {
            return true;
        }
    }

    enum None implements ClassInfoMatch {
        INSTANCE;

        @Override
        public boolean test(int version, int access, String name, String signature, String superName, String[] interfaces) {
            return false;
        }
    }
}
