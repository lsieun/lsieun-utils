package lsieun.asm.common.analysis;

import lsieun.asm.visitor.analysis.list.ClassVisitorForListMember;
import org.objectweb.asm.ClassVisitor;

import java.util.function.Supplier;

/**
 * @see ClassFileAnalysisUtils
 */
public enum StdAnalysis implements Supplier<ClassVisitor> {
    LIST_MEMBERS {
        @Override
        public ClassVisitor get() {
            return new ClassVisitorForListMember();
        }
    };

}
