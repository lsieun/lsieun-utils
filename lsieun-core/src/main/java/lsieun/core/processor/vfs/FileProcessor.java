package lsieun.core.processor.vfs;

import jakarta.annotation.Nonnull;
import lsieun.base.io.file.FileContentUtils;
import lsieun.core.processor.bytes.ByteArrayProcessor;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface FileProcessor extends Consumer<Path> {
    static FileProcessor of(@Nonnull ByteArrayProcessor processor) {
        return path -> processFileAsByteArray(path, processor);
    }

    static FileProcessor of(@Nonnull FileSystemProcessor fileSystemProcessor) {
        return path -> processFileAsZipFileSystem(path, fileSystemProcessor);
    }

    private static void processFileAsZipFileSystem(@Nonnull Path zipPath,
                                                   @Nonnull FileSystemProcessor fileSystemProcessor) {
        if (!Files.exists(zipPath)) {
            return;
        }

        URI zipUri = URI.create("jar:" + zipPath.toUri());
        Map<String, String> env = new HashMap<>(1);
        env.put("create", "false");
        try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
            fileSystemProcessor.accept(zipFs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void processFileAsByteArray(@Nonnull Path filepath, @Nonnull ByteArrayProcessor byteArrayProcessor) {
        if (!Files.exists(filepath)) {
            return;
        }

        byte[] bytes = FileContentUtils.readBytes(filepath);
        byte[] newBytes = byteArrayProcessor.apply(bytes);
        FileContentUtils.writeBytes(filepath, newBytes);
    }
}
