package lsieun.asm.common.analysis;

import lsieun.asm.sam.match.ClassInfoMatch;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.analysis.match.ClassInfoMatchVisitor;
import lsieun.asm.visitor.analysis.match.InsnInvokeMatchVisitor;
import lsieun.asm.visitor.analysis.match.MatchFlagVisitor;
import lsieun.asm.visitor.analysis.match.MethodInfoMatchVisitor;

import org.objectweb.asm.ClassReader;

import java.util.function.Function;

public class ClassFileMatchUtils {
    public static boolean matchClassInfo(byte[] bytes, ClassInfoMatch match) {
        return matchClassInfo(bytes, match, ClassInfoMatchVisitor::new);
    }

    public static boolean matchClassInfo(byte[] bytes, ClassInfoMatch match,
                                         Function<ClassInfoMatch, MatchFlagVisitor> func) {
        MatchFlagVisitor cv = func.apply(match);
        return matchByteCodeElement(bytes, cv, true);
    }

    public static boolean matchMethodInfo(byte[] bytes, MethodInfoMatch methodMatch) {
        return matchByteCodeElement(bytes, new MethodInfoMatchVisitor(methodMatch), true);
    }

    public static boolean matchInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        return matchByteCodeElement(bytes, new InsnInvokeMatchVisitor(methodMatch, insnInvokeMatch), false);
    }

    public static boolean matchByteCodeElement(byte[] bytes, MatchFlagVisitor cv, boolean skipCode) {
        // (1) reader
        ClassReader cr = new ClassReader(bytes);

        // (2) reader + visitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        if (skipCode) {
            parsingOptions |= ClassReader.SKIP_CODE;
        }
        cr.accept(cv, parsingOptions);

        // (3)
        return cv.match();
    }
}
