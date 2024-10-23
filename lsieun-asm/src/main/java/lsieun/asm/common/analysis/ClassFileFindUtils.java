package lsieun.asm.common.analysis;

import lsieun.asm.match.MatchItem;
import lsieun.asm.sam.match.FieldInfoMatch;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.analysis.find.field.ClassVisitorForFindField;
import lsieun.asm.visitor.analysis.find.insn.ClassVisitorForFindInsnByInvokeXxx;
import lsieun.asm.visitor.analysis.find.method.ClassVisitorForFindMethod;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.ClassReader;

import java.lang.reflect.Modifier;
import java.util.List;

public class ClassFileFindUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassFileFindUtils.class);

    public static List<MatchItem> findAbstractMethod(byte[] bytes) {
        MethodInfoMatch methodMatch = MethodInfoMatch.byModifier(Modifier::isAbstract);
        return findMethod(bytes, methodMatch);
    }

    public static List<MatchItem> findField(byte[] bytes, FieldInfoMatch fieldMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindField cv = new ClassVisitorForFindField(fieldMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    public static List<MatchItem> findMethod(byte[] bytes, MethodInfoMatch methodMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindMethod cv = new ClassVisitorForFindMethod(methodMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    public static List<MatchItem> findMethodByInsnInvoke(byte[] bytes, MethodInfoMatch methodMatch, InsnInvokeMatch insnInvokeMatch) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindInsnByInvokeXxx cv = new ClassVisitorForFindInsnByInvokeXxx(methodMatch, insnInvokeMatch);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }

    public static List<MatchItem> findInsnByInvokeXxx(byte[] bytes,
                                                      MethodInfoMatch methodMatch,
                                                      InsnInvokeMatch insnInvokeMatch,
                                                      boolean deduplicate) {
        //（1）构建 ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建 ClassVisitor
        ClassVisitorForFindInsnByInvokeXxx cv = new ClassVisitorForFindInsnByInvokeXxx(
                methodMatch, insnInvokeMatch, deduplicate);

        //（3）结合 ClassReader 和 ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（4）返回结果
        return cv.getResultList();
    }
}
