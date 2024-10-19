package lsieun.core.processor.vfs;

import java.nio.file.Path;

public interface AddDstDir<T> {
    T withTargetDir(Path dirPath);
}
