package lsieun.asm.sam.match;

import lsieun.core.match.LogicAssistant;

import java.lang.invoke.MethodHandles;

@FunctionalInterface
public interface NameAndDescMatch {
    boolean test(String name, String desc);

    LogicAssistant<NameAndDescMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), NameAndDescMatch.class);
}
