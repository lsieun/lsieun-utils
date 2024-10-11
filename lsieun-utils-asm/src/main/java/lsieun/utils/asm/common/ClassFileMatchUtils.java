package lsieun.utils.asm.common;

import lsieun.utils.asm.match.ClassInfoMatch;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.visitor.match.ClassInfoMatchVisitor;
import lsieun.utils.asm.visitor.match.InsnInvokeMatchVisitor;
import lsieun.utils.asm.visitor.match.MatchFlagVisitor;
import lsieun.utils.asm.visitor.match.MethodInfoMatchVisitor;
import org.objectweb.asm.ClassReader;

import java.util.function.Function;

public class ClassFileMatchUtils {
    public static boolean matchClassInfo(byte[] bytes, ClassInfoMatch match) {
        return matchClassInfo(bytes, match, ClassInfoMatchVisitor::new);
    }

    public static boolean matchClassInfo(byte[] bytes, ClassInfoMatch match,
                                         Function<ClassInfoMatch, MatchFlagVisitor> func) {
        MatchFlagVisitor cv = func.apply(match);
        return matchByteCodeElement(bytes, cv);
    }

    public static boolean matchMethodInfo(byte[] bytes, MethodInfoMatch methodMatch) {
        return matchByteCodeElement(bytes, new MethodInfoMatchVisitor(methodMatch));
    }

    public static boolean matchInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        return matchByteCodeElement(bytes, new InsnInvokeMatchVisitor(methodMatch, insnInvokeMatch));
    }

    public static boolean matchByteCodeElement(byte[] bytes, MatchFlagVisitor cv) {
        // (1) reader
        ClassReader cr = new ClassReader(bytes);

        // (2) reader + visitor
        int parsingOptions = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        // (3)
        return cv.match();
    }
}
