package lsieun.core.processor.vfs;

import java.nio.file.Path;

public interface AddFile<T> {
    T withFile(Path filepath);
}
