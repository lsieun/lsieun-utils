package lsieun.core.sam.chain;

import java.nio.file.Path;

@FunctionalInterface
public interface AddFile<T> {
    T withFile(Path filepath);
}
