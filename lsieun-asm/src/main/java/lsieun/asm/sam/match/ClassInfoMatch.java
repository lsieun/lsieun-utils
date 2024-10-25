package lsieun.asm.sam.match;

import lsieun.annotation.type.asm.AsmMatchGeneration;
import lsieun.core.match.LogicAssistant;

import java.lang.invoke.MethodHandles;

@AsmMatchGeneration
@FunctionalInterface
public interface ClassInfoMatch {
    boolean test(int version, int access, String name, String signature, String superName, String[] interfaces);

    LogicAssistant<ClassInfoMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), ClassInfoMatch.class);
}
