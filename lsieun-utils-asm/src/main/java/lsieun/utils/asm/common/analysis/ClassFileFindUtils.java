package lsieun.utils.asm.common.analysis;

import lsieun.utils.asm.match.FieldInfoMatch;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MemberInfoMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.analysis.find.field.ClassVisitorForFindField;
import lsieun.utils.asm.visitor.analysis.find.insn.ClassVisitorForFindInsnByInvokeXxx;
import lsieun.utils.asm.visitor.analysis.find.method.ClassVisitorForFindMethod;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
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
