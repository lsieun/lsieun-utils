package lsieun.asm.format;

import lsieun.asm.description.ByteCodeElementType;
import lsieun.asm.match.MatchState;

import java.net.URI;
import java.nio.file.Path;

public class MatchFormat {
    public static String format(int index, MatchState state, String msg) {
        return String.format("|%03d| [%s] %s", index, state, msg);
    }

    public static String format(int index, MatchState state, Path path) {
        URI uri = path.toAbsolutePath().normalize().toUri();
        return String.format("|%03d| [%s] %s", index, state, uri);
    }

    public static String format(MatchState state, String msg) {
        return String.format("[%s] %s", state, msg);
    }

    public static String format(MatchState state, Path path) {
        URI uri = path.toAbsolutePath().normalize().toUri();
        return String.format("[%s] %s", state, uri);
    }

    public static String format(MatchState state, Path path, String msg) {
        URI uri = path.toAbsolutePath().normalize().toUri();
        return String.format("[%s] %s - %s", state, uri, msg);
    }

    public static String format(MatchState state, ByteCodeElementType elementType, String msg) {
        return String.format("[%s %s] %s", state, elementType, msg);
    }

    public static String format(MatchState state, ByteCodeElementType elementType,
                                String owner, String name, String desc) {
        return String.format("[%s %s] %s::%s:%s", state, elementType, owner, name, desc);
    }
}
