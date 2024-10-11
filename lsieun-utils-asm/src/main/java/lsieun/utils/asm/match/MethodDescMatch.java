package lsieun.utils.asm.match;

import org.objectweb.asm.Type;

@FunctionalInterface
public interface MethodDescMatch {
    static MethodDescMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return methodDesc -> {
            Type returnType = Type.getReturnType(methodDesc);
            return asmTypeMatch.test(returnType);
        };
    }

    boolean test(String methodDesc);

//    class And implements MethodDescMatch {
//        private final MethodDescMatch[] matches;
//
//        private And(MethodDescMatch... matches) {
//            this.matches = matches;
//        }
//
//        @Override
//        public boolean test(String methodDesc) {
//            for (MethodDescMatch match : matches) {
//                if (!match.test(methodDesc)) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        public static And of(MethodDescMatch... matches) {
//            return new And(matches);
//        }
//    }

//    class Or implements MethodDescMatch {
//        private final MethodDescMatch[] matches;
//
//        private Or(MethodDescMatch... matches) {
//            this.matches = matches;
//        }
//
//        @Override
//        public boolean test(String methodDesc) {
//            for (MethodDescMatch match : matches) {
//                if (match.test(methodDesc)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        public static Or of(MethodDescMatch... matches) {
//            return new Or(matches);
//        }
//    }

    enum Bool implements MethodDescMatch {
        TRUE {
            @Override
            public boolean test(String methodDesc) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(String methodDesc) {
                return false;
            }
        };
    }
}
