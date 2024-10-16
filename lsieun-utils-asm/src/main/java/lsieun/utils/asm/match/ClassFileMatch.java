package lsieun.utils.asm.match;

import lsieun.utils.asm.common.analysis.ClassFileMatchUtils;

/**
 * @see ClassFileMatchUtils
 */
@FunctionalInterface
public interface ClassFileMatch {
    boolean test(byte[] bytes);

    static ClassFileMatch byClassInfo(ClassInfoMatch match) {
        return bytes -> ClassFileMatchUtils.matchClassInfo(bytes, match);
    }

    static ClassFileMatch byMethodInfo(MethodInfoMatch match) {
        return bytes -> ClassFileMatchUtils.matchMethodInfo(bytes, match);
    }

    static ClassFileMatch byInsnInvoke(InsnInvokeMatch insnInvokeMatch) {
        return byInsnInvoke(MethodInfoMatch.Bool.TRUE, insnInvokeMatch);
    }

    static ClassFileMatch byInsnInvoke(MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        return bytes -> ClassFileMatchUtils.matchInsnInvoke(bytes, methodMatch, insnInvokeMatch);
    }
}
