package lsieun.core.sam.chain;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@FunctionalInterface
public interface AddFileSystem<T> {
    T withFileSystem(@NotNull FileSystem fs);

    default T withDefaultFileSystem() {
        FileSystem fileSystem = FileSystems.getDefault();
        return withFileSystem(fileSystem);
    }
}
