package lsieun.asm.visitor.analysis.find.method;

import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.match.format.MatchFormat;
import lsieun.asm.match.format.MatchState;
import lsieun.asm.match.result.MatchItem;
import lsieun.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
import org.objectweb.asm.MethodVisitor;

public class ClassVisitorForFindMethod extends ClassVisitorForFind {
    private static final Logger logger = LoggerFactory.getLogger(ClassVisitorForFindMethod.class);

    private final MethodInfoMatch methodMatch;

    public ClassVisitorForFindMethod(MethodInfoMatch methodMatch) {
        this.methodMatch = methodMatch;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.METHOD, currentOwner, name, descriptor));

        boolean flag = methodMatch.test(version, currentOwner, access, name, descriptor, signature, exceptions);

        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.METHOD, currentOwner, name, descriptor));
            MatchItem item = MatchItem.ofMethod(currentOwner, name, descriptor);
            if (!resultList.contains(item)) {
                resultList.add(item);
            }
        }
        return null;
    }
}
