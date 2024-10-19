package lsieun.base.archive.zip;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ZipNioUtils {
    public static void consume(Path zipPath, Consumer<FileSystem> consumer) throws IOException {
        if (zipPath == null) {
            throw new IllegalArgumentException("zipPath is null");
        }
        if (!Files.exists(zipPath)) {
            throw new IllegalArgumentException("zipPath " + zipPath + " does not exist");
        }

        // (1) uri
        URI zipUri = URI.create("jar:" + zipPath.toUri());

        // (2) env
        Map<String, String> env = new HashMap<>(1);
        env.put("create", "false");

        // (3) use
        try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
            consumer.accept(zipFs);
        }
    }

    public static <T> T process(Path zipPath, boolean create, Function<FileSystem, T> func) throws IOException {
        if (zipPath == null) {
            throw new IllegalArgumentException("zipPath is null");
        }
        if (!Files.exists(zipPath) && !create) {
            throw new IllegalArgumentException("zipPath " + zipPath + " does not exist");
        }

        // (1) uri
        URI zipUri = URI.create("jar:" + zipPath.toUri());

        // (2) env
        Map<String, String> env = new HashMap<>(1);
        env.put("create", String.valueOf(create));

        // (3) use
        try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
            return func.apply(zipFs);
        }
    }

}
