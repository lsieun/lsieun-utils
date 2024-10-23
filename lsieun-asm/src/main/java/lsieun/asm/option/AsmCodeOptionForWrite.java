package lsieun.asm.option;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;

import java.util.Arrays;
import java.util.List;

public enum AsmCodeOptionForWrite {
    NONE(0),
    COMPUTE_MAX(ClassWriter.COMPUTE_MAXS),
    COMPUTE_FRAME(ClassWriter.COMPUTE_FRAMES);

    public final int writerFlags;

    AsmCodeOptionForWrite(int writerFlags) {
        this.writerFlags = writerFlags;
    }

    public static AsmCodeOptionForWrite combine(@NotNull AsmCodeOptionForWrite... options) {
        return combine(Arrays.asList(options));
    }

    public static AsmCodeOptionForWrite combine(@NotNull List<AsmCodeOptionForWrite> optionList) {
        AsmCodeOptionForWrite result = NONE;
        for (AsmCodeOptionForWrite option : optionList) {
            if (option == null) {
                continue;
            }
            if (option.writerFlags > result.writerFlags) {
                result = option;
            }
        }
        return result;
    }
}