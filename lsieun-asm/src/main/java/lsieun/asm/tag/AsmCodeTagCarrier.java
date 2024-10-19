package lsieun.asm.tag;

import java.util.EnumSet;
import java.util.Set;

public interface AsmCodeTagCarrier {
    default Set<AsmCodeTag> tags() {
        return EnumSet.noneOf(AsmCodeTag.class);
    }

    interface ForMax extends AsmCodeTagCarrier {
        @Override
        default Set<AsmCodeTag> tags() {
            return EnumSet.of(AsmCodeTag.COMPUTE_MAX);
        }
    }

    interface ForFrame extends ForMax {

        @Override
        default Set<AsmCodeTag> tags() {
            return EnumSet.of(AsmCodeTag.COMPUTE_FRAMES);
        }
    }
}
