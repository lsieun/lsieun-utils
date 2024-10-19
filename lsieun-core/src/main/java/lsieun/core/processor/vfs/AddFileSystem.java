package lsieun.core.processor.vfs;

import jakarta.annotation.Nonnull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@FunctionalInterface
public interface AddFileSystem<T> {
    T withFileSystem(@Nonnull FileSystem fs);

    default T withDefaultFileSystem() {
        FileSystem fileSystem = FileSystems.getDefault();
        return withFileSystem(fileSystem);
    }
}
