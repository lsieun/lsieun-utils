package lsieun.asm.core;

import org.objectweb.asm.Type;

public class AsmTypeUtils {
    public static boolean hasValidValue(Type type) {
        int sort = type.getSort();
        return (sort >= Type.BOOLEAN) && (sort <= Type.OBJECT);
    }

    public static boolean hasInvalidValue(Type type) {
        return !hasValidValue(type);
    }

    public static boolean isPrimitive(Type type) {
        int sort = type.getSort();
        return (sort >= Type.BOOLEAN) && (sort <= Type.DOUBLE);
    }

    public static boolean isNotPrimitive(Type type) {
        return !isPrimitive(type);
    }

    public static boolean isArray(Type type) {
        int sort = type.getSort();
        return sort == Type.ARRAY;
    }

    public static boolean isNotArray(Type type) {
        return !isArray(type);
    }

    public static boolean isObject(Type type) {
        int sort = type.getSort();
        return sort == Type.OBJECT;
    }

    public static boolean isNotObject(Type type) {
        return !isObject(type);
    }

    public static void requiresValidValue(Type type) {
        if (hasInvalidValue(type)) {
            throw new IllegalArgumentException("Invalid type " + type);
        }
    }

    public static void requiresPrimitiveValue(Type type) {
        if (isNotPrimitive(type)) {
            throw new IllegalArgumentException("Not Primitive Type: " + type);
        }
    }

    public static void requiresArrayValue(Type type) {
        if (isNotArray(type)) {
            throw new IllegalArgumentException("Not Array Type: " + type);
        }
    }

    public static void requiresObjectValue(Type type) {
        if (isNotObject(type)) {
            throw new IllegalArgumentException("Not Object Type: " + type);
        }
    }

    public static Type toArray(Type t, int rank) {
        String arrayDescriptor = "[".repeat(rank) + t.getDescriptor();
        return Type.getType(arrayDescriptor);
    }
}
