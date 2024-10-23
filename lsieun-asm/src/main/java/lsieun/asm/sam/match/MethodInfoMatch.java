package lsieun.asm.sam.match;

import lsieun.asm.description.MemberDesc;
import lsieun.core.match.LogicAssistant;
import lsieun.core.match.text.TextMatch;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

import static lsieun.asm.cst.MyAsmConst.*;

@FunctionalInterface
public interface MethodInfoMatch {
    boolean test(String owner,
                 int methodAccess, String methodName, String methodDesc,
                 String signature, String[] exceptions);


    LogicAssistant<MethodInfoMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), MethodInfoMatch.class);

    static LogicAssistant<MethodInfoMatch> logic() {
        return LOGIC;
    }

    static MethodInfoMatch byModifier(Predicate<Integer> predicate) {
        return ((owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                predicate.test(methodAccess));
    }

    static MethodInfoMatch byMethodName(String name) {
        return byMethodName(TextMatch.equals(name));
    }

    static MethodInfoMatch byMethodName(TextMatch textMatch) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                textMatch.test(methodName);
    }

    static MethodInfoMatch byMethodNameAndDesc(String name, String desc) {
        return byMethodNameAndDesc(TextMatch.equals(name), TextMatch.equals(desc));
    }

    static MethodInfoMatch byMethodNameAndDesc(TextMatch nameMatch, TextMatch descMatch) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                nameMatch.test(methodName) && descMatch.test(methodDesc);
    }

    static MethodInfoMatch byMethodNameAndDesc(TextMatch textMatch) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            String nameAndDesc = String.format("%s:%s", methodName, methodDesc);
            return textMatch.test(nameAndDesc);
        };
    }

    static MethodInfoMatch byOwnerNameAndDesc(String internalClassName, String name, String desc) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                owner.equals(internalClassName) && methodName.equals(name) && methodDesc.equals(desc);
    }

    static MethodInfoMatch byOwnerNameAndDesc(MemberDesc memberDesc) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                owner.equals(memberDesc.owner()) &&
                        methodName.equals(memberDesc.name()) &&
                        methodDesc.equals(memberDesc.desc());
    }

    static MethodInfoMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return (owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            Type returnType = Type.getReturnType(methodDesc);
            return asmTypeMatch.test(returnType);
        };
    }

    static MethodInfoMatch of(boolean flag) {
        return flag ? Bool.TRUE : Bool.FALSE;
    }

    static MethodInfoMatch skipCommon() {
        return skip(
                Common.INIT,
                Common.CLASS_INIT,
                Common.HASH_CODE,
                Common.EQUALS,
                Common.CLONE,
                Common.TO_STRING
        );
    }

    static MethodInfoMatch skip(MethodInfoMatch... matches) {
        LogicAssistant<MethodInfoMatch> logic = logic();
        MethodInfoMatch m = logic.or(false, matches);
        return logic.not(m);
    }

    enum Bool implements MethodInfoMatch {
        TRUE {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return false;
            }
        };
    }

    enum Common implements MethodInfoMatch {
        INIT {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return CONSTRUCTOR_INTERNAL_NAME.equals(methodName);
            }
        },
        CLASS_INIT {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return TYPE_INITIALIZER_INTERNAL_NAME.equals(methodName);
            }
        },
        HASH_CODE {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return HASH_CODE_METHOD_NAME.equals(methodName)
                        && HASH_CODE_METHOD_DESC.equals(methodDesc);
            }
        },
        EQUALS {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return EQUALS_METHOD_NAME.equals(methodName)
                        && EQUALS_METHOD_DESC.equals(methodDesc);
            }
        },
        CLONE {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return CLONE_METHOD_NAME.equals(methodName)
                        && CLONE_METHOD_DESC.equals(methodDesc);
            }
        },
        TO_STRING {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return TO_STRING_METHOD_NAME.equals(methodName)
                        && TO_STRING_METHOD_DESC.equals(methodDesc);
            }
        },
        ;
    }

    enum Skip implements MethodInfoMatch {
        INIT_AND_CLINIT {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return !(CONSTRUCTOR_INTERNAL_NAME.equals(methodName)
                        || TYPE_INITIALIZER_INTERNAL_NAME.equals(methodName));
            }
        },
        TO_STRING {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return !(TO_STRING_METHOD_NAME.equals(methodName) && TO_STRING_METHOD_DESC.equals(methodDesc));
            }
        },
        HASH_AND_EQUALS {
            @Override
            public boolean test(String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return !(
                        (HASH_CODE_METHOD_NAME.equals(methodName) && HASH_CODE_METHOD_DESC.equals(methodDesc))
                                || (EQUALS_METHOD_NAME.equals(methodName) && EQUALS_METHOD_DESC.equals(methodDesc))
                );
            }
        };
    }
}
