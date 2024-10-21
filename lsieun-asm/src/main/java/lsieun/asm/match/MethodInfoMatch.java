package lsieun.asm.match;

import lsieun.asm.description.MemberDesc;
import lsieun.core.match.LogicAssistant;
import lsieun.core.match.text.TextMatch;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

@FunctionalInterface
public interface MethodInfoMatch {
    boolean test(int version, String owner,
                 int methodAccess, String methodName, String methodDesc,
                 String signature, String[] exceptions);

    static LogicAssistant<MethodInfoMatch> logic() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return LogicAssistant.of(lookup, MethodInfoMatch.class);
    }

    static MethodInfoMatch byModifier(Predicate<Integer> predicate) {
        return ((version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                predicate.test(methodAccess));
    }

    static MethodInfoMatch byMethodName(String name) {
        return byMethodName(TextMatch.equals(name));
    }

    static MethodInfoMatch byMethodName(TextMatch textMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                textMatch.test(methodName);
    }

    static MethodInfoMatch byMethodNameAndDesc(String name, String desc) {
        return byMethodNameAndDesc(TextMatch.equals(name), TextMatch.equals(desc));
    }

    static MethodInfoMatch byMethodNameAndDesc(TextMatch nameMatch, TextMatch descMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                nameMatch.test(methodName) && descMatch.test(methodDesc);
    }

    static MethodInfoMatch byMethodNameAndDesc(TextMatch textMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            String nameAndDesc = String.format("%s:%s", methodName, methodDesc);
            return textMatch.test(nameAndDesc);
        };
    }

    static MethodInfoMatch byOwnerNameAndDesc(String internalClassName, String name, String desc) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                owner.equals(internalClassName) && methodName.equals(name) && methodDesc.equals(desc);
    }

    static MethodInfoMatch byOwnerNameAndDesc(MemberDesc memberDesc) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                owner.equals(memberDesc.owner()) &&
                        methodName.equals(memberDesc.name()) &&
                        methodDesc.equals(memberDesc.desc());
    }

    static MethodInfoMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            Type returnType = Type.getReturnType(methodDesc);
            return asmTypeMatch.test(returnType);
        };
    }

    static MethodInfoMatch of(boolean flag) {
        return flag ? logic().toTrue() : logic().toFalse();
    }

    enum All implements MethodInfoMatch {
        INSTANCE;

        @Override
        public boolean test(int version, String owner,
                            int methodAccess, String methodName, String methodDesc,
                            String signature, String[] exceptions) {
            return true;
        }
    }

    enum None implements MethodInfoMatch {
        INSTANCE;

        @Override
        public boolean test(int version, String owner,
                            int methodAccess, String methodName, String methodDesc,
                            String signature, String[] exceptions) {
            return false;
        }
    }

    enum Bool implements MethodInfoMatch {
        TRUE {
            @Override
            public boolean test(int version, String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(int version, String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return false;
            }
        };
    }

    enum Skip implements MethodInfoMatch {
        INIT_METHOD {
            @Override
            public boolean test(int version, String owner,
                                int methodAccess, String methodName, String methodDesc,
                                String signature, String[] exceptions) {
                return !("<init>".equals(methodName) || "<clinit>".equals(methodName));
            }
        };
    }
}
