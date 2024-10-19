package lsieun.core.processor.vfs;

import java.nio.file.Path;

@FunctionalInterface
public interface AddZip<T> {
    T withZip(Path zipPath, String entry);
}
