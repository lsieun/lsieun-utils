package lsieun.utils.asm.match.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchItem {
    public final MatchType type;

    public final String owner;
    public final String name;
    public final String descriptor;

    public final List<MatchItem> children = new ArrayList<>();

    private MatchItem(MatchType type, String owner, String name, String descriptor) {
        this.type = type;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }

    public static MatchItem ofType(String internalName) {
        return of(MatchType.CLASS, internalName, "", "");
    }

    public static MatchItem ofField(String internalName, String fieldName, String fieldDesc) {
        return of(MatchType.FIELD, internalName, fieldName, fieldDesc);
    }

    public static MatchItem ofMethod(String internalName, String methodName, String methodDesc) {
        return of(MatchType.METHOD, internalName, methodName, methodDesc);
    }

    public static MatchItem ofInsn(String internalName, String methodName, String methodDesc) {
        return of(MatchType.INSN, internalName, methodName, methodDesc);
    }

    public static MatchItem of(MatchType type, String internalName, String name, String descriptor) {
        return new MatchItem(type, internalName, name, descriptor);
    }

    public static MatchItem parse(String line) {
        if (line == null) {
            return null;
        }
        String[] array1 = line.split("\\s+");

        MatchType type = MatchType.valueOf(array1[0]);
        String internalName = array1[1];
        String name = "";
        String descriptor = "";
        if (type != MatchType.CLASS) {
            String[] array2 = array1[2].split(":");
            name = array2[0];
            descriptor = array2[1];
        }

        return of(type, internalName, name, descriptor);
    }

    @Override
    public boolean equals(Object o) {
        // ref
        if (this == o) {
            return true;
        }

        // clazz
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        // fields
        MatchItem that = (MatchItem) o;
        return type == that.type &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(name, that.name) &&
                Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, owner, name, descriptor);
    }

    @Override
    public String toString() {
        switch (type) {
            case CLASS: {
                return String.format("%s %s", type, owner);
            }
            case FIELD:
            case METHOD:
            case INSN:
            default: {
                return String.format("%s %s %s:%s", type, owner, name, descriptor);
            }
        }
    }
}
