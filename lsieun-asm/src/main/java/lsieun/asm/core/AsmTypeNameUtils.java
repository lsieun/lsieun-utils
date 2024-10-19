package lsieun.asm.core;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;

public class AsmTypeNameUtils {
    public static String toClassName(String text) {
        Type t = parse(text);
        return toClassName(t);
    }

    public static String toClassName(Type type) {
        return type.getClassName();
    }

    public static String toInternalName(String text) {
        Type t = parse(text);
        return toInternalName(t);
    }

    public static String toInternalName(Type type) {
        return type.getInternalName();
    }

    public static String toDescriptor(String text) {
        Type t = parse(text);
        return toDescriptor(t);
    }

    public static String toDescriptor(Type type) {
        return type.getDescriptor();
    }

    public static String toJarEntryName(String text) {
        Type t = parse(text);
        return toJarEntryName(t);
    }

    public static String toJarEntryName(Type type) {
        String internalName = type.getInternalName();
        return String.format("%s.class", internalName);
    }

    public static String toSimpleName(String text) {
        Type t = parse(text);
        return toSimpleName(t);
    }

    public static String toSimpleName(Type type) {
        String className = type.getClassName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static Type parse(String text) {
        text = text.replace('.', '/');
        if (text.length() == 1) {
            Field[] fields = Type.class.getFields();
            for (Field field : fields) {
                if (field.getName().endsWith("_TYPE")) {
                    try {
                        Type predefinedType = (Type) field.get(null);
                        if (predefinedType.getInternalName().equals(text)) {
                            return predefinedType;
                        }
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }

        Type t;
        if (text.contains("(") && text.contains(")")) {
            Type methodType = Type.getMethodType(text);
            t = methodType.getReturnType();
        }
        else if (text.startsWith("L") && text.endsWith(";")) {
            t = Type.getType(text);
        }
        else {
            t = Type.getObjectType(text);
        }
        return t;
    }
}
