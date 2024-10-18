package lsieun.utils.asm.visitor.analysis.find.field;

import lsieun.utils.asm.description.ByteCodeElementType;
import lsieun.utils.asm.match.FieldInfoMatch;
import lsieun.utils.asm.match.format.MatchFormat;
import lsieun.utils.asm.match.format.MatchState;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;
import org.objectweb.asm.FieldVisitor;

public class ClassVisitorForFindField extends ClassVisitorForFind {
    private static final Logger logger = LoggerFactory.getLogger(ClassVisitorForFindField.class);

    private final FieldInfoMatch fieldMatch;

    public ClassVisitorForFindField(FieldInfoMatch fieldMatch) {
        this.fieldMatch = fieldMatch;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        logger.trace(() -> MatchFormat.format(MatchState.MATCHING, ByteCodeElementType.FIELD, currentOwner, name, descriptor));

        boolean flag = fieldMatch.test(currentOwner, access, name, descriptor, value);

        if (flag) {
            logger.debug(() -> MatchFormat.format(MatchState.MATCHED, ByteCodeElementType.FIELD, currentOwner, name, descriptor));
            MatchItem item = MatchItem.ofField(currentOwner, name, descriptor);
            if (!resultList.contains(item)) {
                resultList.add(item);
            }
        }
        return null;
    }
}
