package lsieun.utils.asm.core;

import org.objectweb.asm.Type;

public class AsmTypeBuddy {
    public static Type toArray(Type t, int dimension) {
        String arrayDescriptor = "[".repeat(dimension) + t.getDescriptor();
        return Type.getType(arrayDescriptor);
    }
}
