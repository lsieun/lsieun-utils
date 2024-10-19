package lsieun.asm.core;

import org.objectweb.asm.Type;

public class AsmTypeBuddy {
    public static boolean isValidValue(Type type) {
        int sort = type.getSort();
        return (sort >= Type.BOOLEAN) && (sort <= Type.OBJECT);
    }

    public static boolean isPrimitive(Type type) {
        int sort = type.getSort();
        return (sort >= Type.BOOLEAN) && (sort <= Type.DOUBLE);
    }

    public static boolean isArray(Type type) {
        int sort = type.getSort();
        return sort == Type.ARRAY;
    }

    public static boolean isObject(Type type) {
        int sort = type.getSort();
        return sort == Type.OBJECT;
    }

    public static void requiresValidValue(Type type) {
        if (!isValidValue(type)) {
            throw new IllegalArgumentException("Invalid type " + type);
        }
    }

    public static void requiresPrimitiveValue(Type type) {
        if (!isPrimitive(type)) {
            throw new IllegalArgumentException("Not Primitive Type: " + type);
        }
    }

    public static void requiresArrayValue(Type type) {
        if (!isArray(type)) {
            throw new IllegalArgumentException("Not Array Type: " + type);
        }
    }

    public static void requiresObjectValue(Type type) {
        if (!isObject(type)) {
            throw new IllegalArgumentException("Not Object Type: " + type);
        }
    }

    public static Type toArray(Type t, int rank) {
        String arrayDescriptor = "[".repeat(rank) + t.getDescriptor();
        return Type.getType(arrayDescriptor);
    }
}
