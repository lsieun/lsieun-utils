package lsieun.base.io.file;

import jakarta.annotation.Nonnull;
import lsieun.annotation.type.UtilityClass;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static lsieun.base.io.file.FileOperation.READ;
import static lsieun.base.io.file.FileOperation.WRITE;

@UtilityClass
public class FileContentUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileContentUtils.class);

    private FileContentUtils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] readBytes(@Nonnull Path filepath) {
        if (!Files.exists(filepath)) {
            String msg = String.format("filepath does not exist: %s", filepath);
            throw new IllegalArgumentException(msg);
        }

        try {
            byte[] bytes = Files.readAllBytes(filepath);
            logger.info(() -> FileFormatUtils.format(filepath, READ));
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBytes(@Nonnull Path filepath, @Nonnull byte[] bytes) {
        try {
            prepareWrite(filepath);

            Files.write(filepath, bytes);
            logger.info(() -> FileFormatUtils.format(filepath, WRITE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readLines(@Nonnull Path path) {
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            String msg = String.format("path does not exist: %s", path);
            throw new IllegalArgumentException(msg);
        }

        try {
            List<String> lines = Files.readAllLines(path);
            logger.info(() -> FileFormatUtils.format(path, READ));
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLines(@Nonnull Path path, @Nonnull List<String> lines) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(lines);

        try {
            prepareWrite(path);

            Files.write(path, lines);
            logger.info(() -> FileFormatUtils.format(path, WRITE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareWrite(@Nonnull Path filepath) throws IOException {
        Path dir = filepath.getParent();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
