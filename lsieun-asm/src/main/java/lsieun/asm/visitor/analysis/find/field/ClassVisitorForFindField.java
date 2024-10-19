package lsieun.asm.visitor.analysis.find.field;

import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.match.FieldInfoMatch;
import lsieun.asm.match.format.MatchFormat;
import lsieun.asm.match.format.MatchState;
import lsieun.asm.match.result.MatchItem;
import lsieun.asm.visitor.analysis.find.ClassVisitorForFind;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;
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
