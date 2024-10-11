package lsieun.utils.core.io.file;

import lsieun.utils.annotation.type.UtilityClass;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static lsieun.utils.core.io.file.FileOperation.READ;
import static lsieun.utils.core.io.file.FileOperation.WRITE;

@UtilityClass
public class FileContentUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileContentUtils.class);

    private FileContentUtils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] readBytes(Path path) throws IOException {
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            String msg = String.format("path does not exist: %s", path);
            throw new IllegalArgumentException(msg);
        }
        byte[] bytes = Files.readAllBytes(path);
        logger.info(() -> FileFormatUtils.format(path, READ));
        return bytes;
    }

    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        Objects.requireNonNull(path);
        Path dir = path.getParent();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Files.write(path, bytes);
        logger.info(() -> FileFormatUtils.format(path, WRITE));
    }

    public static List<String> readLines(Path path) throws IOException {
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            String msg = String.format("path does not exist: %s", path);
            throw new IllegalArgumentException(msg);
        }
        List<String> lines = Files.readAllLines(path);
        logger.info(() -> FileFormatUtils.format(path, READ));
        return lines;
    }

    public static void writeLines(Path path, List<String> lines) throws IOException {
        Objects.requireNonNull(path);
        Path dir = path.getParent();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Files.write(path, lines);
        logger.info(() -> FileFormatUtils.format(path, WRITE));
    }
}
