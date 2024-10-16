package lsieun.utils.asm.match.format;

import lsieun.utils.asm.description.ByteCodeElementType;

public class MatchFormat {
    public static String format(int index, MatchState state, String msg) {
        return String.format("|%03d| [%s] %s", index, state, msg);
    }

    public static String format(MatchState state, String msg) {
        return String.format("[%s] %s", state, msg);
    }

    public static String format(MatchState state, ByteCodeElementType elementType, String msg) {
        return String.format("[%s %s] %s", state, elementType, msg);
    }

    public static String format(MatchState state, ByteCodeElementType elementType,
                                String owner, String name, String desc) {
        return String.format("[%s %s] %s::%s:%s", state, elementType, owner, name, desc);
    }
}
