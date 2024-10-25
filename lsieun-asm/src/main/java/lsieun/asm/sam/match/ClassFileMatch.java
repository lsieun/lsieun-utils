package lsieun.asm.sam.match;

import lsieun.asm.common.analysis.ClassFileMatchUtils;
import lsieun.core.match.LogicAssistant;

import java.lang.invoke.MethodHandles;

/**
 * @see ClassFileMatchUtils
 */
@FunctionalInterface
public interface ClassFileMatch {
    boolean test(byte[] bytes);

    LogicAssistant<ClassFileMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), ClassFileMatch.class);

    static ClassFileMatch byClassInfo(ClassInfoMatch match) {
        return bytes -> ClassFileMatchUtils.matchClassInfo(bytes, match);
    }

    static ClassFileMatch byMethodInfo(MethodInfoMatch match) {
        return bytes -> ClassFileMatchUtils.matchMethodInfo(bytes, match);
    }

    static ClassFileMatch byInsnInvoke(InsnInvokeMatch insnInvokeMatch) {
        return byInsnInvoke(MethodInfoMatch.LOGIC.alwaysTrue(), insnInvokeMatch);
    }

    static ClassFileMatch byInsnInvoke(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        return bytes -> ClassFileMatchUtils.matchInsnInvoke(bytes, methodMatch, insnInvokeMatch);
    }
}
