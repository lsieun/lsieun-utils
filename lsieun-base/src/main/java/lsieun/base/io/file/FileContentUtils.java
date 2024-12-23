package lsieun.base.io.file;

import lsieun.annotation.type.UtilityClass;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;


@UtilityClass
public class FileContentUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileContentUtils.class);

    private FileContentUtils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] readBytes(@NotNull Path filepath) {
        if (!Files.exists(filepath)) {
            String msg = String.format("filepath does not exist: %s", filepath);
            throw new IllegalArgumentException(msg);
        }

        try {
            byte[] bytes = Files.readAllBytes(filepath);
            logger.info(() -> FileFormatUtils.format(filepath, FileOperation.READ));
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBytes(@NotNull Path filepath, byte @NotNull [] bytes) {
        try {
            prepareWrite(filepath);

            Files.write(filepath, bytes);
            logger.info(() -> FileFormatUtils.format(filepath, FileOperation.WRITE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readLines(@NotNull Path path) {
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            String msg = String.format("path does not exist: %s", path);
            throw new IllegalArgumentException(msg);
        }

        try {
            List<String> lines = Files.readAllLines(path);
            logger.info(() -> FileFormatUtils.format(path, FileOperation.READ));
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLines(@NotNull Path path, @NotNull List<String> lines) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(lines);

        try {
            prepareWrite(path);

            Files.write(path, lines);
            logger.info(() -> FileFormatUtils.format(path, FileOperation.WRITE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareWrite(@NotNull Path filepath) throws IOException {
        Path dir = filepath.getParent();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
