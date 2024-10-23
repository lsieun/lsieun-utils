package lsieun.core.sam;

import java.nio.file.Path;

@FunctionalInterface
public interface AddDirTo<T> {
    T withTargetDir(Path targetDir);
}
