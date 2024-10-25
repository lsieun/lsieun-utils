package lsieun.asm.sam.match;

import lsieun.core.match.LogicAssistant;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;

@FunctionalInterface
public interface MethodDescMatch {
    boolean test(String methodDesc);

    LogicAssistant<MethodDescMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), MethodDescMatch.class);

    static MethodDescMatch byReturnType(AsmTypeMatch asmTypeMatch) {
        return methodDesc -> {
            Type returnType = Type.getReturnType(methodDesc);
            return asmTypeMatch.test(returnType);
        };
    }
}
