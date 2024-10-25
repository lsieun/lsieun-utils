package lsieun.core.sam.chain;

import java.nio.file.Path;

@FunctionalInterface
public interface AddZip<T> {
    T withZip(Path zipPath, String entry);
}
