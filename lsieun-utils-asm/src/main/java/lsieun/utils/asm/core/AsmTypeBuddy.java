package lsieun.utils.asm.core;

import org.objectweb.asm.Type;

public class AsmTypeBuddy {
    public static Type toArray(Type t, int rank) {
        String arrayDescriptor = "[".repeat(rank) + t.getDescriptor();
        return Type.getType(arrayDescriptor);
    }
}
