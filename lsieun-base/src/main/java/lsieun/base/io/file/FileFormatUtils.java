package lsieun.base.io.file;

import lsieun.annotation.type.UtilityClass;

import java.net.URI;
import java.nio.file.Path;

@UtilityClass
@SuppressWarnings("UnnecessaryLocalVariable")
public class FileFormatUtils {
    public static String format(Path path, FileOperation op) {
        URI uri = path.toAbsolutePath().normalize().toUri();
        String msg = String.format("[%s] %s", op, uri);
        return msg;
    }

    public static String format(Path sourcePath, Path targetPath, FileOperation op) {
        String msg = String.format("[%s] %s --> %s", op, sourcePath, targetPath);
        return msg;
    }
}
