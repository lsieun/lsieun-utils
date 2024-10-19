package lsieun.asm.common.analysis;

import lsieun.asm.match.MemberInfoMatch;
import lsieun.asm.visitor.analysis.list.ClassVisitorForListMember;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.util.function.Supplier;

/**
 * @see StdAnalysis
 */
public class ClassFileAnalysisUtils {
    public static void listMembers(byte[] bytes, MemberInfoMatch memberMatch) {
        ClassReader cr = new ClassReader(bytes);
        ClassVisitor cv = new ClassVisitorForListMember(memberMatch);
        cr.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    public static void analysis(byte[] bytes, Supplier<ClassVisitor> supplier) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassVisitor
        ClassVisitor cv = supplier.get();

        //（3）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);
    }
}
