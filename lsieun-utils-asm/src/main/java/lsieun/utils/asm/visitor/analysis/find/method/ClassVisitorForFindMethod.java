package lsieun.utils.asm.visitor.analysis.find.method;

import lsieun.utils.asm.description.ByteCodeElementType;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
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
