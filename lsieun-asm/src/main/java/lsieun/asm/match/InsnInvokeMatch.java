package lsieun.asm.match;

import lsieun.asm.description.MemberDesc;
import lsieun.core.match.text.TextMatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Objects;

@FunctionalInterface
@SuppressWarnings("UnnecessaryLocalVariable")
public interface InsnInvokeMatch extends InsnMatch {
    boolean test(int opcode, String owner, String name, String descriptor);

    static InsnInvokeMatch byMethodName(String methodName) {
        InsnInvokeMatch match = (opcode, owner, name, descriptor) -> Objects.equals(methodName, name);
        return match;
    }

    static InsnInvokeMatch byMethodName(TextMatch textMatch) {
        InsnInvokeMatch match = (opcode, owner, name, descriptor) -> textMatch.test(name);
        return match;
    }

    static InsnInvokeMatch byNameAndDesc(TextMatch textMatch) {
        InsnInvokeMatch match = (opcode, owner, name, descriptor) -> {
            String nameAndDesc = String.format("%s:%s", name, descriptor);
            return textMatch.test(nameAndDesc);
        };
        return match;
    }

    static InsnInvokeMatch byOwnerNameAndDesc(String targetOwner, String targetName, String targetDesc) {
        return ((opcode, owner, name, descriptor) ->
                owner.equals(targetOwner) && name.equals(targetName) && descriptor.equals(targetDesc));
    }

    static InsnInvokeMatch byOwnerNameAndDesc(MemberDesc memberDesc) {
        return ((opcode, owner, name, descriptor) ->
                owner.equals(memberDesc.owner()) &&
                        name.equals(memberDesc.name()) &&
                        descriptor.equals(memberDesc.desc()));
    }

    static InsnInvokeMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return (opcode, owner, name, descriptor) -> {
            Type returnType = Type.getReturnType(descriptor);
            return asmTypeMatch.test(returnType);
        };
    }

    enum All implements InsnInvokeMatch {
        INSTANCE;

        @Override
        public boolean test(int opcode, String owner, String name, String descriptor) {
            return true;
        }
    }

    enum None implements InsnInvokeMatch {
        INSTANCE;

        @Override
        public boolean test(int opcode, String owner, String name, String descriptor) {
            return false;
        }
    }

    enum Bool implements InsnInvokeMatch {
        TRUE {
            @Override
            public boolean test(int opcode, String owner, String name, String descriptor) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(int opcode, String owner, String name, String descriptor) {
                return false;
            }
        };
    }

    enum ByMethodInsn implements InsnInvokeMatch {
        SYSTEM_EXIT {
            @Override
            public boolean test(int opcode, String owner, String name, String descriptor) {
                return opcode == Opcodes.INVOKESTATIC &&
                        "java/lang/System".equals(owner) &&
                        "exit".equals(name) &&
                        "(I)V".equals(descriptor);
            }
        },
        METHOD_INVOKE {
            @Override
            public boolean test(int opcode, String owner, String name, String descriptor) {
                return opcode == Opcodes.INVOKEVIRTUAL &&
                        "java/lang/reflect/Method".equals(owner) &&
                        "invoke".equals(name) &&
                        "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;".equals(descriptor);
            }
        };
    }

    enum ByReturn implements InsnInvokeMatch {
        METHOD {
            @Override
            public boolean test(int opcode, String owner, String name, String descriptor) {
                Type t = Type.getMethodType(descriptor);
                Type returnType = t.getReturnType();
                return Type.getType(Method.class).equals(returnType);
            }
        },
        ;
    }
}
