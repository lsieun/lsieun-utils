package lsieun.utils.asm.match;

import lsieun.utils.match.text.TextMatch;
import org.objectweb.asm.Type;

@FunctionalInterface
public interface MethodInfoMatch {
    boolean test(int version, String owner,
                 int methodAccess, String methodName, String methodDesc,
                 String signature, String[] exceptions);

    static MethodInfoMatch byMethodName(String name) {
        return byMethodName(TextMatch.equals(name));
    }

    static MethodInfoMatch byMethodName(TextMatch textMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                textMatch.test(methodName);
    }

    static MethodInfoMatch byMethodNameAndDesc(String name, String desc) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                methodName.equals(name) && methodDesc.equals(desc);
    }

    static MethodInfoMatch byMethodNameAndDesc(TextMatch textMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            String nameAndDesc = String.format("%s:%s", methodName, methodDesc);
            return textMatch.test(nameAndDesc);
        };
    }

    static MethodInfoMatch byOwnerMethodNameAndDesc(String internalClassName, String name, String desc) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
                owner.equals(internalClassName) && methodName.equals(name) && methodDesc.equals(desc);
    }

    static MethodInfoMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return (version, owner, methodAccess, methodName, methodDesc, signature, exceptions) ->
        {
            Type returnType = Type.getReturnType(methodDesc);
            return asmTypeMatch.test(returnType);
        };
    }



    enum AllMethods implements MethodInfoMatch {
        INSTANCE;

        @Override
        public boolean test(int version, String owner,
                            int methodAccess, String methodName, String methodDesc,
                            String signature, String[] exceptions) {
            return true;
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
}
