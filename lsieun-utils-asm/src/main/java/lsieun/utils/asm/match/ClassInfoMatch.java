package lsieun.utils.asm.match;

import lsieun.utils.annotation.type.asm.AsmMatchGeneration;

import java.lang.invoke.MethodHandles;

@AsmMatchGeneration
@FunctionalInterface
public interface ClassInfoMatch {
    boolean test(int version, int access, String name, String signature, String superName, String[] interfaces);

    static LogicAssistant<ClassInfoMatch> logic() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return LogicAssistant.of(lookup, ClassInfoMatch.class);
    }

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

    enum Bool implements ClassInfoMatch {
        TRUE {
            @Override
            public boolean test(int version, int access, String name, String signature,
                                String superName, String[] interfaces) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(int version, int access, String name, String signature,
                                String superName, String[] interfaces) {
                return false;
            }
        };
    }
}
